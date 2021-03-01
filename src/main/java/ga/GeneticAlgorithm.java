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
        Chromosome parentA = population.eliteTournamentSelection();
        Chromosome parentB = population.eliteTournamentSelection();
        if (random.nextDouble() < crossoverRate) {
            SymmetricPair<Chromosome> offspring = population.recombination(parentA, parentB);
            if (random.nextDouble() < mutationRate) {
                Chromosome offspringA = population.mutate(offspring.first);
            }
            if (random.nextDouble() < mutationRate) {
                Chromosome offspringB = population.mutate(offspring.second);
            }
        }
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
