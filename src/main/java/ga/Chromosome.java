package main.java.ga;

import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class Chromosome implements Iterable<Depot> {

    private static final int ALPHA = 100;
    private static final float BETA = 0.001F;

    private List<Depot> chromosome;
    private float fitness = 0.0F;

    public Chromosome(final List<Depot> chromosome) {
        this.chromosome = chromosome.stream().map(depot -> new Depot(depot)).collect(Collectors.toList());
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        RouteScheduler.schedule(this);
    }

    public List<Depot> getChromosome() { return chromosome; }

    public float getFitness() {
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

    @Override
    public Iterator<Depot> iterator() {
        return chromosome.iterator();
    }

    // public Chromosome insertion (Chromosome chromosome);
    // public Chromosome deletion (Chromosome chromosome);
    // public Chromosome orderOneCrossover (Chromosome chromosome);
    // public Chromosome partiallyMappedCrossover (Chromosome chromosome);
    // public Chromosome cycleCrossover (Chromosome chromosome);

    // public Chromosome insertMutation (Chromosome chromosome);
    // public Chromosome swapMutation (Chromosome chromosome);
    // public Chromosome scrambleMutation (Chromosome chromosome);
    // public Chromosome inversionMutation (Chromosome chromosome);

}
