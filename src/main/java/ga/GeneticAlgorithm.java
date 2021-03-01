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
            boolean elitism
    ) {
        numberOfVehiclesPerDepot = problemInstance.getNumberOfVehiclesPerDepot();
        maxRouteDuration = problemInstance.getMaxRouteDuration();
        maxVehicleLoad = problemInstance.getMaxVehicleLoad();

        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.eliteSize = (int) (populationSize * 0.01);

        RouteScheduler.setNumberOfVehiclesPerDepot(numberOfVehiclesPerDepot);
        RouteScheduler.setMaxRouteDuration(maxRouteDuration);
        RouteScheduler.setMaxVehicleLoad(maxVehicleLoad);

        population = Population.heuristicInitialization(populationSize, problemInstance.getDepots(), problemInstance.getCustomers());
        population.evaluate();
    }

    public Population getPopulation() {
        return population;
    }

    public Population update() {
        Population oldPopulation = new Population(population);
        List<Chromosome> newPopulation = new ArrayList<>();

        while (newPopulation.size() < populationSize) {
            SymmetricPair<Chromosome> parents = oldPopulation.selection();
            Chromosome offspringA = parents.first;
            Chromosome offspringB = parents.second;
            if (random.nextDouble() < crossoverRate) {
                SymmetricPair<Chromosome> offspring = oldPopulation.recombination(parents.first, parents.second);
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
        population = new Population(newPopulation);
        // Initialization
        // Evaluation
        // Selection
        // Recombination
        // Mutation (intra-depot & inter-depot)
        // Acceptance (replacement), we use generational replacement here
        // Elitism step
        return population.update();
    }

}
