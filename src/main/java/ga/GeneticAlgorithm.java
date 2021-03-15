package main.java.ga;

import main.java.FileParser;
import main.java.MDVRP;
import main.java.utils.SymmetricPair;
import main.java.utils.Util;

import java.nio.charset.CharsetEncoder;
import java.util.*;

public class GeneticAlgorithm {

    private static final Random random = Util.random;

    private final List<Double> bestDurations = new ArrayList<>();
    private final List<Double> averageDurations = new ArrayList<>();

    private final MDVRP problemInstance;

    // GA Parameters
    private final int populationSize;
    private final double crossoverRate;
    private final double mutationRate;
    private final boolean elitism;

    private Population population;

    public GeneticAlgorithm(
            final MDVRP problemInstance,
            int populationSize,
            double crossoverRate,
            double mutationRate,
            boolean elitism
    ) {
        this.problemInstance = problemInstance;
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;

        printParameters();

        RouteScheduler.setNumberOfVehiclesPerDepot(problemInstance.getNumberOfVehiclesPerDepot());
        RouteScheduler.setMaxRouteDuration(problemInstance.getMaxRouteDuration());
        RouteScheduler.setMaxVehicleLoad(problemInstance.getMaxVehicleLoad());

        population = Population.heuristicInitialization(populationSize, problemInstance.getDepots(), problemInstance.getCustomers());
    }

    private void printParameters() {
        System.out.printf("Population size: %d%n", populationSize);
        System.out.printf("Crossover rate: %.2f%n", crossoverRate);
        System.out.printf("Mutation rate: %.2f%n", mutationRate);
        System.out.println("Evaluation function: weighted sum");
    }

    public Population getPopulation() { return population; }

    public Population update() {
        bestDurations.add(population.getBestDuration());
        averageDurations.add(population.getAverageDuration());

        Population oldPopulation = new Population(population);

        List<Chromosome> newPopulation = new ArrayList<>();

        while (newPopulation.size() < populationSize) {
            SymmetricPair<Chromosome> parents = oldPopulation.selection(true);
            Chromosome offspringA = new Chromosome(parents.first);
            Chromosome offspringB = new Chromosome(parents.second);
            if (random.nextDouble() < crossoverRate) {
                SymmetricPair<Chromosome> offspring = oldPopulation.crossover(offspringA, offspringB);
                offspringA = offspring.first;
                offspringB = offspring.second;
                offspringA.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
                offspringB.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
            }
            if (random.nextDouble() < mutationRate) {
                offspringA = oldPopulation.mutate(offspringA);
            }
            if (random.nextDouble() < mutationRate) {
                offspringB = oldPopulation.mutate(offspringB);
            }
            offspringA.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
            offspringB.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
            newPopulation.add(offspringA);
            newPopulation.add(offspringB);
        }
        if (elitism) {
            for (Chromosome elite : population.getElite(4)) {
                newPopulation.set(random.nextInt(populationSize), new Chromosome(elite));
            }
        }
        population = oldPopulation.replacement(newPopulation);
        population.evaluate();
        // System.out.println("Update finished. Overall time consumed: "+ (System.currentTimeMillis() - start)+" ms");
        return population;
    }

    public void exit() {
        // Verify that the constraints are not violated.
        Chromosome solution = population.getAlpha();
        solution.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
        solution.checkNumberOfVehiclesPerDepot(problemInstance.getNumberOfVehiclesPerDepot());
        if (problemInstance.getMaxRouteDuration() == 0) {
            solution.checkRoutes(Integer.MAX_VALUE, problemInstance.getMaxVehicleLoad());
        } else {
            solution.checkRoutes(problemInstance.getMaxRouteDuration(), problemInstance.getMaxVehicleLoad());
        }

        FileParser.saveTrainingData(bestDurations, averageDurations);
    }

}
