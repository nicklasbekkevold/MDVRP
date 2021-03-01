package main.java.ga;

import main.java.utils.SymmetricPair;

public interface Recombination {

    SymmetricPair<Chromosome> crossover(Chromosome parentA, Chromosome parentB);
}
