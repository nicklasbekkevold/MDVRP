package main.java.ga;

import main.java.utils.SymmetricPair;
import main.java.utils.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class Chromosome implements Iterable<Depot>, Comparable<Chromosome> {

    private static final Random random = Util.random;
    private static List<Customer> swappableCustomerList;

    private static final int ALPHA = 100;
    private static final double BETA = 0.001;

    private final List<Depot> chromosome;
    private int rank = 0;
    private double fitness = 0.0;
    private boolean modified = true;

    public Chromosome(final Chromosome chromosome) {
        this(chromosome.getChromosome());
        this.rank = 0;
        this.fitness = 0.0;
        this.modified = true;
    }

    public Chromosome(final List<Depot> chromosome) {
        this.chromosome = chromosome.stream().map(Depot::new).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public double getFitness() {
        if (modified) {
            fitness = 0.0;
            List<Vehicle> vehicles = getVehicles();
            fitness += ALPHA * vehicles.size();
            for (Vehicle vehicle : vehicles) {
                    fitness += BETA * vehicle.getDuration();
            }
            modified = false;
        }
        return fitness;
    }

    public List<Vehicle> getVehicles() {
        return chromosome.stream().flatMap(depot -> depot.getVehicles().stream()).collect(Collectors.toList());
    }

    public int getVehicleCount() {
        return chromosome.stream().mapToInt(depot -> depot.getVehicles().size()).sum();
    }

    public void setRank(int rank) { this.rank = rank; }

    public static void setSwappableCustomerList(List<Customer> swappableCustomerList) { Chromosome.swappableCustomerList = swappableCustomerList; };

    public void removeCustomers(final List<Customer> customers) {
        modified = true;
        for (Vehicle vehicle : getVehicles()) {
            vehicle.removeCustomers(customers);
        }
    }

    public static Recombination bestCostRouteCrossover = (parentA, parentB) -> {
        Chromosome parentACopy = new Chromosome(parentA);
        Chromosome parentBCopy = new Chromosome(parentB);

        int depotIndex = random.nextInt(parentACopy.getChromosome().size());
        Depot parentADepot = parentACopy.getChromosome().get(depotIndex);
        Depot parentBDepot = parentBCopy.getChromosome().get(depotIndex);

        Vehicle vehicleA = parentADepot.getVehicles().get(random.nextInt(parentADepot.getVehicles().size()));
        Vehicle vehicleB = parentBDepot.getVehicles().get(random.nextInt(parentBDepot.getVehicles().size()));

        List<Customer> vehicleACustomers = new ArrayList<>(vehicleA.getCustomers());
        List<Customer> vehicleBCustomers = new ArrayList<>(vehicleB.getCustomers());

        parentACopy.removeCustomers(vehicleBCustomers);
        parentBCopy.removeCustomers(vehicleACustomers);

        for (Customer customer : vehicleACustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentBDepot, customer);
        }
        for (Customer customer : vehicleBCustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentADepot, customer);
        }
        return new SymmetricPair<>(parentACopy, parentBCopy);
    };

    private static final Mutation inverseMutation = (chromosome) -> {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(random.nextInt(offspring.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();
        List<Customer> customersCopy = new ArrayList<>(customers);

        int cutoffPointA = random.nextInt(customers.size());
        int cutoffPointB = random.nextInt(customers.size());

        if (cutoffPointA < cutoffPointB) {
            for (int i = cutoffPointA; i < cutoffPointB; i++) {
                customers.set(i, customersCopy.get(cutoffPointB - i));
            }
        } else {
            for (int i = cutoffPointB; i < cutoffPointA; i++) {
                customers.set(i, customersCopy.get(cutoffPointA - i));
            }
        }
        RouteScheduler.schedule(offspring);
        return offspring;
    };

    private static final Mutation reRoutingMutation = (chromosome) -> {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(random.nextInt(offspring.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();

        int randomCustomer = random.nextInt(customers.size());
        List<Customer> customer = customers.subList(randomCustomer, randomCustomer + 1);

        offspring.removeCustomers(customer);
        RouteScheduler.insertCustomerWithBestRouteCost(depot, customer.get(0)); //TODO: needs to be across all depots
        return offspring;
    };

    private static final Mutation swapMutation = (chromosome) -> {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(random.nextInt(offspring.getChromosome().size()));

        List<Vehicle> vehicles = Util.randomChoice(depot.getVehicles(), 2);
        if (vehicles == null) {
            return offspring;
        }
        vehicles.get(0).swapRandomCustomer(vehicles.get(1));
        return offspring;
    };

    public static final Mutation intraDepotMutation = (chromosome) -> {
        int randomFunction = random.nextInt(3);
        switch (randomFunction) {
            case 0 -> {
                return inverseMutation.mutate(chromosome);
            }
            case 1 -> {
                return chromosome; // return reRoutingMutation.mutate(chromosome);
            }
            case 2 -> {
                return swapMutation.mutate(chromosome);
            }
            default -> {
                return null;
            }
        }
    };

    public static final Mutation interDepotMutation = (chromosome) -> {
        Chromosome offspring = new Chromosome(chromosome);
        Customer customer = swappableCustomerList.get(random.nextInt(swappableCustomerList.size()));
        List<Integer> candidateDepotIds = new ArrayList<>(customer.getCandidateDepotIds());

        // Remove customer from sourceDepot
        for (Depot source : offspring) {
            if (source.getCustomers().contains(customer)) {
                candidateDepotIds.remove((Integer) source.getId());
                offspring.removeCustomers(Collections.singletonList(customer));
                break;
            }
        }
        // Add customer to destination depot
        Integer destinationDepotId = candidateDepotIds.get(random.nextInt(candidateDepotIds.size()));
        for (Depot destinationDepot : offspring) {
            if (destinationDepot.getId() == destinationDepotId) {
                RouteScheduler.insertCustomerWithBestRouteCost(destinationDepot, customer);
                break;
            }
        }
        return offspring;
    };

    public boolean dominates(Chromosome otherChromosome) {
        return (
                this.getFitness() <= otherChromosome.getFitness() &&
                this.getVehicleCount() <= otherChromosome.getVehicleCount() &&
                (this.getFitness() < otherChromosome.getFitness() ||
                this.getVehicleCount() < otherChromosome.getVehicleCount())
        );
    }

    @Override
    public int compareTo(Chromosome otherChromosome) {
        return Double.compare(this.getFitness(), otherChromosome.getFitness());
    }

    @Override
    public Iterator<Depot> iterator() {
        return chromosome.iterator();
    }

    @Override
    public String toString() {
        return String.format("%.2f", getFitness());
    }

    // public Chromosome insertion (Chromosome chromosome);
    // public Chromosome deletion (Chromosome chromosome);
    // public Chromosome orderOneCrossover (Chromosome chromosome);
    // public Chromosome partiallyMappedCrossover (Chromosome chromosome);
    // public Chromosome cycleCrossover (Chromosome chromosome);

    // public Chromosome insertMutation (Chromosome chromosome);
    // public Chromosome swapMutation (Chromosome chromosome);
    // public Chromosome scrambleMutation (Chromosome chromosome);

}
