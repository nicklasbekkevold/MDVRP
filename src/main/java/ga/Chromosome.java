package main.java.ga;

import java.util.List;

interface Chromosome extends Iterable<Gene> {

    public double getFitness();
    public List<Gene> getChromosome();

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
