package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Route;

import java.util.*;

public class Chromosome implements Iterable<Depot> {

    private Map<Depot, List<Customer>> chromosome; // Genotype
    private Map<Depot, List<Route>> routes; // Phenotype

    private float fitness = 0.0F;

    public float getFitness() { return fitness; }
    public  Map<Depot, List<Customer>> getChromosome() { return chromosome; }

    @Override
    public Iterator<Depot> iterator() {
        return chromosome.keySet().iterator();
    }

    public Chromosome(final Map<Depot, List<Customer>> chromosome) {
        this.chromosome = chromosome;
        for (Depot depot : this) {
            Collections.shuffle(chromosome.get(depot));
        }
        routes = RouteScheduler.schedule(this.chromosome);
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
