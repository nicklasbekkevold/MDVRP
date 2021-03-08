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

    public Chromosome(final Chromosome chromosome) {
        this(chromosome.getChromosome());
        rank = 0;
    }

    public Chromosome(final List<Depot> otherChromosome) {
        chromosome = otherChromosome.stream().map(Depot::new).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public double getDuration() { return getVehicles().stream().mapToDouble(Vehicle::getDuration).sum(); }

    public double getFitness() {
        return ALPHA * getVehicleCount() + BETA * getDuration();
    }

    public List<Vehicle> getVehicles() {
        return chromosome.stream().flatMap(depot -> depot.getVehicles().stream()).collect(Collectors.toList());
    }

    public int getVehicleCount() { return chromosome.stream().mapToInt(depot -> depot.getVehicles().size()).sum(); }

    public void setRank(int rank) { this.rank = rank; }

    public static void setSwappableCustomerList(List<Customer> swappableCustomerList) { Chromosome.swappableCustomerList = swappableCustomerList; }

    public void removeCustomer(Customer customers) {
        for (Vehicle vehicle : getVehicles()) {
            vehicle.removeCustomer(customers);
        }
    }

    public void removeCustomers(final List<Customer> customers) {
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

    private static final Mutation reversalMutation = (chromosome) -> {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(random.nextInt(offspring.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();
        List<Customer> customersCopy = new ArrayList<>(customers);

        int cutoffPointA = random.nextInt(customers.size());
        int cutoffPointB = random.nextInt(customers.size());

        int windowSize = Math.abs(cutoffPointA - cutoffPointB);
        int start = Math.min(cutoffPointA, cutoffPointB);
        int end = Math.max(cutoffPointA, cutoffPointB);
        for (int i = 0; i <= windowSize; i++) {
            customers.set(start + i, customersCopy.get(end - i));
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

        SymmetricPair<Vehicle> vehicles = Util.randomPair(depot.getVehicles());
        vehicles.first.swapRandomCustomer(vehicles.second);
        return offspring;
    };

    public static final Mutation intraDepotMutation = (chromosome) -> {
        int randomFunction = random.nextInt(3);
        switch (randomFunction) {
            case 0 -> {
                return reversalMutation.mutate(chromosome);
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
                source.removeCustomer(customer);
                offspring.removeCustomer(customer);
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
                this.getDuration() <= otherChromosome.getDuration() &&
                this.getVehicleCount() <= otherChromosome.getVehicleCount() &&
                (this.getDuration() < otherChromosome.getDuration() ||
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
        return String.format("%.2f", getDuration());
    }

}
