package main.java.ga;

import main.java.utils.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class Chromosome implements Iterable<Depot>, Comparable<Chromosome> {

    private static final int ALPHA = 0;
    private static final double BETA = 1;

    private List<Depot> chromosome;
    private List<Customer> swappableCustomerList;
    private double fitness = 0.0;
    private boolean modified = true;

    public Chromosome(final Chromosome chromosome) {
        new Chromosome(chromosome.getChromosome(), chromosome.getSwappableCustomerList());
    }

    public Chromosome(final List<Depot> chromosome, final List<Customer> swappableCustomerList) {
        this.swappableCustomerList = new ArrayList<>(swappableCustomerList);
        this.chromosome = chromosome.stream().map(depot -> new Depot(depot)).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public List<Customer> getSwappableCustomerList() { return swappableCustomerList; }

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

    public void removeCustomers(final List<Customer> customers) {
        modified = true;
        for (Vehicle vehicle : getVehicles()) {
            vehicle.removeCustomers(customers);
        }
    }

    public static Chromosome mutate(Chromosome chromosome) {
        int randomFunction = new Random().nextInt(3);
        switch (randomFunction) {
            case 0: {
                return inverseMutation(chromosome);
            }
            case 1: {
                return reRoutingMutation(chromosome);
            }
            case 2: {
                return swapMutation(chromosome);
            }
            default: {
                return null;
            }
        }
    }

    private static Chromosome inverseMutation(Chromosome chromosome) {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(new Random().nextInt(offspring.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();
        List<Customer> customersCopy = new ArrayList<>(customers);

        int cutoffPointA = new Random().nextInt(customers.size());
        int cutoffPointB = new Random().nextInt(customers.size());

        if (cutoffPointA < cutoffPointB) {
            for (int i = cutoffPointA; i < cutoffPointB; i++) {
                customers.set(i, customersCopy.get(cutoffPointB - i));
            }
        } else {
            for (int i = cutoffPointB; i < cutoffPointA; i++) {
                customers.set(i, customersCopy.get(cutoffPointA - i));
            }
        }
        return offspring;
    }

    private static Chromosome reRoutingMutation(Chromosome chromosome) {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(new Random().nextInt(offspring.getChromosome().size()));
        List<Customer> customers = depot.getCustomers();

        int randomCustomer = new Random().nextInt(customers.size());
        List<Customer> customer = customers.subList(randomCustomer, randomCustomer);

        offspring.removeCustomers(customer);
        RouteScheduler.insertCustomerWithBestRouteCost(depot, customer.get(0)); //TODO: needs to be across all depots
        return offspring;
    }

    private static Chromosome swapMutation(Chromosome chromosome) {
        Chromosome offspring = new Chromosome(chromosome);
        Depot depot = offspring.getChromosome().get(new Random().nextInt(offspring.getChromosome().size()));

        List<Vehicle> vehicles = Util.randomChoice(depot.getVehicles(), 2);
        vehicles.get(0).swapRandomCustomer(vehicles.get(1));
        return offspring;
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
