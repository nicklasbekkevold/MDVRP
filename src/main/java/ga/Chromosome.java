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

    public Chromosome(final Chromosome otherChromosome) {
        chromosome = otherChromosome.chromosome.stream().map(Depot::new).collect(Collectors.toList());
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

    public static Crossover bestCostRouteCrossover = (parentA, parentB) -> {
        int depotIndex = random.nextInt(parentA.getChromosome().size());
        Depot parentADepot = parentA.getChromosome().get(depotIndex);
        Depot parentBDepot = parentB.getChromosome().get(depotIndex);

        Vehicle vehicleA = parentADepot.getVehicles().get(random.nextInt(parentADepot.getVehicles().size()));
        Vehicle vehicleB = parentBDepot.getVehicles().get(random.nextInt(parentBDepot.getVehicles().size()));

        List<Customer> vehicleACustomers = new ArrayList<>(vehicleA.getCustomers());
        List<Customer> vehicleBCustomers = new ArrayList<>(vehicleB.getCustomers());

        parentA.removeCustomers(vehicleBCustomers);
        parentB.removeCustomers(vehicleACustomers);

        for (Customer customer : vehicleACustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentBDepot, customer);
        }
        for (Customer customer : vehicleBCustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentADepot, customer);
        }
        return new SymmetricPair<>(parentA, parentB);
    };

    private static final Mutation reversalMutation = (chromosome) -> {
        Depot depot = chromosome.getChromosome().get(random.nextInt(chromosome.getChromosome().size()));
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
        RouteScheduler.reschedule(depot);
        return chromosome;
    };

    private static final Mutation singleCustomerReRoutingMutation = (chromosome) -> {
        Depot depot = chromosome.getChromosome().get(random.nextInt(chromosome.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();

        Customer customer = depot.getCustomers().get(random.nextInt(customers.size()));
        chromosome.removeCustomer(customer);

        List<Depot> candidateDepots = chromosome.chromosome.stream().map(Depot::new).collect(Collectors.toList());
        int bestFeasibleInsertLocation = -1;
        double bestInsertionCost = Double.MAX_VALUE;

        for (Depot candidateDepot : candidateDepots) {
            double initialCost = candidateDepot.getVehicles().stream().mapToDouble(Vehicle::getDuration).sum();
            RouteScheduler.insertCustomerWithBestRouteCost(candidateDepot, customer, 1.0);
            double insertionCost = candidateDepot.getVehicles().stream().mapToDouble(Vehicle::getDuration).sum() - initialCost;
            if (insertionCost < bestInsertionCost) {
                bestInsertionCost = insertionCost;
                bestFeasibleInsertLocation = candidateDepots.indexOf(candidateDepot);
            }
        }
        chromosome.getChromosome().set(bestFeasibleInsertLocation, candidateDepots.get(bestFeasibleInsertLocation));
        return chromosome;
    };

    private static final Mutation swapMutation = (chromosome) -> {
        Depot depot = chromosome.getChromosome().get(random.nextInt(chromosome.getChromosome().size()));

        SymmetricPair<Vehicle> vehicles = Util.randomPair(depot.getVehicles());
        vehicles.first.swapRandomCustomer(vehicles.second);
        return chromosome;
    };

    public static final Mutation intraDepotMutation = (chromosome) -> {
        int randomFunction = random.nextInt(3);
        switch (randomFunction) {
            case 0 -> {
                return reversalMutation.mutate(chromosome);
            }
            case 1 -> {
                return singleCustomerReRoutingMutation.mutate(chromosome);
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
        Customer customer = swappableCustomerList.get(random.nextInt(swappableCustomerList.size()));
        List<Integer> candidateDepotIds = new ArrayList<>(customer.getCandidateDepotIds());

        // Remove customer from sourceDepot
        for (Depot source : chromosome) {
            if (source.getCustomers().contains(customer)) {
                candidateDepotIds.remove((Integer) source.getId());
                chromosome.removeCustomer(customer);
                break;
            }
        }
        // Add customer to destination depot
        Integer destinationDepotId = candidateDepotIds.get(random.nextInt(candidateDepotIds.size()));
        for (Depot destinationDepot : chromosome) {
            if (destinationDepot.getId() == destinationDepotId) {
                RouteScheduler.insertCustomerWithBestRouteCost(destinationDepot, customer);
                break;
            }
        }
        return chromosome;
    };

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
