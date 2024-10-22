package main.java.ga;

import main.java.FileParser;
import main.java.MDVRP;
import main.java.domain.Vehicle;
import main.java.utils.SymmetricPair;
import main.java.utils.Util;

import java.util.*;

public class GeneticAlgorithm {

    private static final Random random = Util.random;

    private final List<Double> timeStamps = new ArrayList<>();
    private final List<Double> bestDurations = new ArrayList<>();
    private final List<Double> averageDurations = new ArrayList<>();

    private final MDVRP problemInstance;
    private final double startTime;

    // GA Parameters
    private final int populationSize;
    private final double crossoverRate;
    private final double mutationRate;
    private final boolean elitism;
    private final int eliteSize;
    private final boolean memoizeRoutes;

    private Population population;

    public GeneticAlgorithm(
            final MDVRP problemInstance,
            int populationSize,
            double crossoverRate,
            double mutationRate,
            boolean elitism,
            boolean memoizeRoutes
    ) {
        this.problemInstance = problemInstance;
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.eliteSize = (int) Math.ceil(populationSize * 0.01);
        this.memoizeRoutes = memoizeRoutes;

        printParameters();

        Vehicle.memoizeRoutes(memoizeRoutes);

        RouteScheduler.setNumberOfVehiclesPerDepot(problemInstance.getNumberOfVehiclesPerDepot());
        RouteScheduler.setMaxRouteDuration(problemInstance.getMaxRouteDuration());
        RouteScheduler.setMaxVehicleLoad(problemInstance.getMaxVehicleLoad());

        population = Population.heuristicInitialization(populationSize, problemInstance.getDepots(), problemInstance.getCustomers());
        population.evaluate();
        startTime = (double) System.currentTimeMillis() / 1000.0;
    }

    private void printParameters() {
        System.out.printf("Population size: %d%n", populationSize);
        System.out.printf("Crossover rate: %.2f%n", crossoverRate);
        System.out.printf("Mutation rate: %.2f%n", mutationRate);
        System.out.printf("Elitism: %s (elite size = %d) %n", (elitism ? "active" : "disabled"), eliteSize);
        System.out.println("Evaluation function: weighted sum");
        System.out.println("Memoize routes: " + (memoizeRoutes ? "on" : "off"));
    }

    public Population getPopulation() { return population; }

    public Population update() {
        timeStamps.add((double) System.currentTimeMillis() / 1000.0 - startTime);
        bestDurations.add(population.getBestDuration());
        averageDurations.add(population.getAverageDuration());

        List<Chromosome> newPopulation = new ArrayList<>();

        while (newPopulation.size() < populationSize) {
            SymmetricPair<Chromosome> parents = population.selection();
            Chromosome offspringA = new Chromosome(parents.first);
            Chromosome offspringB = new Chromosome(parents.second);
            if (random.nextDouble() < crossoverRate) {
                SymmetricPair<Chromosome> offspring = population.crossover(offspringA, offspringB);
                offspringA = offspring.first;
                offspringB = offspring.second;
            }
            if (random.nextDouble() < mutationRate) {
                offspringA = population.mutate(offspringA);
            }
            if (random.nextDouble() < mutationRate) {
                offspringB = population.mutate(offspringB);
            }
            newPopulation.add(offspringA);
            newPopulation.add(offspringB);
        }
        if (elitism) {
            for (Chromosome elite : population.getElite(eliteSize)) {
                newPopulation.set(random.nextInt(populationSize), new Chromosome(elite));
            }
        }
        this.population = population.replacement(newPopulation);
        this.population.evaluate();
        return this.population;
    }

    public void exit() {
        FileParser.saveTrainingData(timeStamps, bestDurations, averageDurations);

        // Verify that the constraints are not violated.
        Chromosome solution = population.getAlpha();
        solution.checkNumberOfCustomers(problemInstance.getNumberOfCustomers());
        solution.checkNumberOfVehiclesPerDepot(problemInstance.getNumberOfVehiclesPerDepot());
        if (problemInstance.getMaxRouteDuration() != 0) {
            solution.checkRoutes(problemInstance.getMaxRouteDuration(), problemInstance.getMaxVehicleLoad());
        }
    }

}
