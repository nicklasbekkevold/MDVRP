package main.java.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Population implements Iterable<Chromosome> {

    private int generation = 0;
    private float averageFitness = 0.0F;
    private float diversity = 0.0F;

    private List<Chromosome> population;

    public Population() {
        this.population = null;
    }

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public void update() {
        generation++;
    }

    public Population selection() {
        // roulette wheel
        return null;
    }

    public int getGeneration() {
        return generation;
    }

    public float getMaxFitness() {
        return averageFitness;
    }

    public float getAverageFitnessFitness() { return averageFitness; }

    public float getDiversity() {
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
