package main.java.ga;

import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;

public class Chromosome implements Iterable<Depot> {

    private static final int ALPHA = 100;
    private static final float BETA = 0.001F;

    private List<Depot> chromosome; // Genotype
    private List<List<Vehicle>> routes; // Phenotype

    private float fitness = 0.0F;

    public float getFitness() {
        if (fitness == 0) {
            for (List<Vehicle> depotVehicles : routes) {
                fitness += ALPHA * depotVehicles.size();
                for (Vehicle vehicle : depotVehicles) {
                    fitness += BETA * vehicle.getDuration();
                }
            }
        }
        return fitness;
    }
    public List<Depot> getChromosome() { return chromosome; }

    public List<List<Vehicle>> getRoutes() { return routes; }

    @Override
    public Iterator<Depot> iterator() {
        return chromosome.iterator();
    }

    public Chromosome(final List<Depot> chromosome) {
        this.chromosome = chromosome;
        for (Depot depot : this) {
            Collections.shuffle(depot.getCustomers());
        }
        routes = RouteScheduler.schedule(this);
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
