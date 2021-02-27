package main.java.ga;

import main.java.MDVRP;
import main.java.domain.Customer;

import java.util.*;

public class GeneticAlgorithm {

    // GA Parameters
    private final int populationSize;
    private final double crossOverRate;
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
            double crossOverRate,
            double mutationRate,
            boolean elitism
    ) {
        numberOfVehiclesPerDepot = problemInstance.getNumberOfVehiclesPerDepot();
        maxRouteDuration = problemInstance.getMaxRouteDuration();
        maxVehicleLoad = problemInstance.getMaxVehicleLoad();

        this.populationSize = populationSize;
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;

        RouteScheduler.setNumberOfVehiclesPerDepot(numberOfVehiclesPerDepot);
        RouteScheduler.setMaxRouteDuration(maxRouteDuration);
        RouteScheduler.setMaxVehicleLoad(maxVehicleLoad);

        population = Population.heuristicInitialization(populationSize, problemInstance.getDepots(), problemInstance.getCustomers());
    }

    public Population getPopulation() {
        return population;
    }

    public Population update() {
        // Selection
        List<Chromosome> offspring = population.bestCostRouteCrossover();
        // Mutation (intra-depot & inter-depot)
        // population.mutate();
        // Acceptance (replacement), we use generational replacement here
        // Elitism step
        // Do one loop
        return population.update();
    }

}
