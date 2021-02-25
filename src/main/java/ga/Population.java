package main.java.ga;

import main.java.Util;
import main.java.domain.Vehicle;

import java.util.*;

public class Population implements Iterable<Chromosome> {

    private int generation = 0;
    private double averageFitness = 0.0;
    private double diversity = 0.0;

    private final List<Chromosome> population;

    public Population() {
        this.population = null;
    }

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public int getGeneration() { return generation; }

    public double getMaxFitness() { return getAlpha().getFitness(); }

    public double getAverageFitness() {
        averageFitness = population.stream().mapToDouble(Chromosome::getFitness).average().getAsDouble();
        return averageFitness;
    }

    public double getDiversity() { return diversity; }

    public Chromosome getAlpha() { return Collections.max(population); }

    @Override
    public Iterator<Chromosome> iterator() { return population.iterator(); }

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
        Vehicle vehicleA = Util.randomChoice(parentA.getChromosome().get(depotIndex).getVehicles(), 1).get(0);
        Vehicle vehicleB = Util.randomChoice(parentB.getChromosome().get(depotIndex).getVehicles(), 1).get(0);
        vehicleA.removeCustomers(vehicleB.getCustomers());
        vehicleB.removeCustomers(vehicleA.getCustomers());
        parents.get(1).getChromosome().get(depotIndex);
        return parents;
    }

}
