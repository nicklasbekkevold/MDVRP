package main.java.ga;

import main.java.Util;
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

    public Chromosome(final List<Depot> chromosome, final List<Customer> swappableCustomerList) {
        this.swappableCustomerList = new ArrayList<>(swappableCustomerList);
        this.chromosome = chromosome.stream().map(depot -> new Depot(depot)).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public double getFitness() {
        if (modified) {
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

    public void removeCustomers(List<Customer> customers) {
        modified = true;
        for (Vehicle vehicle : getVehicles()) {
            vehicle.removeCustomers(customers);
        }
    }

    private void mutate(Chromosome chromosome) {
        int randomFunction = new Random().nextInt(3);
        switch (randomFunction) {
            case 0: {
                inverseMutation();
            }
            case 1: {
                reRoutingMutation();
            }
            case 2: {
                swapMutation();
            }

        }
    }

    private void inverseMutation() {
        Depot depot = chromosome.get(new Random().nextInt(chromosome.size()));
        List<Customer> customers = depot.getCustomers();
        List<Customer> customersCopy = new ArrayList<>(customers);
        int cutoffPointA = new Random().nextInt(customers.size());
        int cutoffPointB = new Random().nextInt(customers.size());
        if (cutoffPointA < cutoffPointB) {
            for (int i = cutoffPointA; i < cutoffPointB; i++) {
                customers.set(i, customersCopy.get(cutoffPointB - i));
            }
        }
        RouteScheduler.schedule(this);
    }

    private void reRoutingMutation() {
        Depot depot = chromosome.get(new Random().nextInt(chromosome.size()));
        List<Customer> customers = depot.getCustomers();
        Customer customer = customers.get(new Random().nextInt(customers.size()));
        // Insert customer into most feasible location ...
    }

    private void swapMutation() {
        Depot depot = chromosome.get(new Random().nextInt(chromosome.size()));
        List<Vehicle> vehicles = Util.randomChoice(depot.getVehicles(), 2);
        vehicles.get(0).swapRandomCustomer(vehicles.get(1));
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
