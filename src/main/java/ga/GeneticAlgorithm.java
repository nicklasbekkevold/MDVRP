package main.java.ga;

import main.java.MDVRP;
import main.java.utils.SymmetricPair;
import main.java.utils.Util;

import java.util.*;

public class GeneticAlgorithm {

    private static final Random random = Util.random;

    // GA Parameters
    private final int populationSize;
    private final double crossoverRate;
    private final double mutationRate;
    private final boolean elitism;
    private final int eliteSize;
    private final boolean useParetoRanking;

    // Constraints
    private final int numberOfVehiclesPerDepot;
    private final int maxRouteDuration;
    private final int maxVehicleLoad;

    private Population population;

    public GeneticAlgorithm(
            final MDVRP problemInstance,
            int populationSize,
            double crossoverRate,
            double mutationRate,
            boolean elitism,
            boolean useParetoRanking
    ) {
        numberOfVehiclesPerDepot = problemInstance.getNumberOfVehiclesPerDepot();
        maxRouteDuration = problemInstance.getMaxRouteDuration();
        maxVehicleLoad = problemInstance.getMaxVehicleLoad();

        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.eliteSize = (int) Math.ceil(populationSize * 0.01);
        this.useParetoRanking = useParetoRanking;

        printParameters();

        RouteScheduler.setNumberOfVehiclesPerDepot(numberOfVehiclesPerDepot);
        RouteScheduler.setMaxRouteDuration(maxRouteDuration);
        RouteScheduler.setMaxVehicleLoad(maxVehicleLoad);

        population = Population.heuristicInitialization(populationSize, problemInstance.getDepots(), problemInstance.getCustomers());
        population.evaluate(useParetoRanking);
    }

    private void printParameters() {
        System.out.println(String.format("Population size: %d", populationSize));
        System.out.println(String.format("Crossover rate: %.2f", crossoverRate));
        System.out.println(String.format("Mutation rate: %.2f", mutationRate));
        System.out.println(String.format("Number of elite: %d", eliteSize));
        System.out.println("Evaluation function: " + (useParetoRanking ? "pareto" : "weighted sum"));
    }

    public Population getPopulation() {
        return population;
    }

    public Population update() {
        Population oldPopulation = new Population(population);
        List<Chromosome> newPopulation = new ArrayList<>();

        while (newPopulation.size() < populationSize) {
            SymmetricPair<Chromosome> parents = oldPopulation.selection(false);
            Chromosome offspringA = parents.first;
            Chromosome offspringB = parents.second;
            if (random.nextDouble() < crossoverRate) {
                SymmetricPair<Chromosome> offspring = oldPopulation.crossover(parents.first, parents.second);
                offspringA = offspring.first;
                offspringB = offspring.second;
            }
            if (random.nextDouble() < mutationRate) {
                offspringA = oldPopulation.mutate(offspringA);
            }
            if (random.nextDouble() < mutationRate) {
                offspringB = oldPopulation.mutate(offspringB);
            }
            newPopulation.add(offspringA);
            newPopulation.add(offspringB);
        }
        if (elitism) {
            for (Chromosome chromosome : population.getElite(eliteSize)) {
                newPopulation.set(random.nextInt(populationSize), chromosome);
            }
        }
        population = oldPopulation.replacement(newPopulation);
        population.evaluate(useParetoRanking);
        return population;
    }

}
