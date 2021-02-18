package main.java.ga;

import main.java.Util;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;

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
        List<Chromosome> parents = Util.randomChoice(population, 2);
        return null;
    }

    private List<Chromosome> BestCostRouteCrossover(List<Chromosome> parents) {
        assert parents.size() == 2;
        Chromosome parentA = parents.get(0);
        Chromosome parentB = parents.get(1);
        int depotIndex = new Random().nextInt(parentA.getChromosome().size());
        List<Depot> a = parentA.getRoutes().get();
        Vehicle vehicleA = Util.randomChoice(parentA.ge, 1).get(0);
        parents.get(1).getChromosome().get(depotIndex);
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
