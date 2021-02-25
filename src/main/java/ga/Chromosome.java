package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class Chromosome implements Iterable<Depot>, Comparable<Chromosome> {

    private static final int ALPHA = 100;
    private static final double BETA = 0.001;

    private List<Depot> chromosome;
    private double fitness = 0.0;

    public Chromosome(final List<Depot> chromosome) {
        this.chromosome = chromosome.stream().map(depot -> new Depot(depot)).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public double getFitness() {
        if (fitness == 0) {
            List<Vehicle> vehicles = getVehicles();
            fitness += ALPHA * vehicles.size();
            for (Vehicle vehicle : vehicles) {
                    fitness += BETA * vehicle.getDuration();
            }
        }
        return fitness;
    }

    public List<Vehicle> getVehicles() {
        return chromosome.stream().flatMap(depot -> depot.getVehicles().stream()).collect(Collectors.toList());
    }

    public void removeCustomers(List<Customer> customers) {
        for (Vehicle vehicle : getVehicles()) {
            vehicle.removeCustomers(customers);
        }
    }

    public void inverseMutation() {
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
    }

    @Override
    public Iterator<Depot> iterator() {
        return chromosome.iterator();
    }

    @Override
    public int compareTo(Chromosome otherChromosome) {
        return Double.compare(this.getFitness(), otherChromosome.getFitness());
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
