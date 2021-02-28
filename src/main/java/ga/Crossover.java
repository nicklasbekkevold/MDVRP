package main.java.ga;

import main.java.utils.SymmetricPair;

public interface Crossover {

    SymmetricPair<Chromosome> crossover(Chromosome parentA, Chromosome parentB);
}
