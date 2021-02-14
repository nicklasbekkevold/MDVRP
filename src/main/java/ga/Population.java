package main.java.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Population implements Iterable<Chromosome> {

    private int generation = 0;
    private double averageFitness = 0.0;
    private double diversity = 0.0;

    private List<Chromosome> population;

    public Population() {
        this.population = null;
    }

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public Population selection() {
        // roulette wheel
        return null;
    }

    public void update() {
        generation++;
    }

    public int getGeneration() {
        return generation;
    }

    public double getMaxFitness() {
        return averageFitness;
    }

    public double getDiversity() {
        return diversity;
    }

    public Chromosome getAlpha() {
        return population.get(0);
    }

    @Override
    public Iterator<Chromosome> iterator() {
        return population.iterator();
    }
}
