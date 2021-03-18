package main.java;

import main.java.ga.GeneticAlgorithm;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        int generations = 200;
        int populationSize = 100;
        double crossoverRate = 0.8;
        double mutationRate = 0.3;
        boolean elitism = true;
        boolean memoizeRoutes = true;

        String problemId = "p22";
        MDVRP problemInstance = FileParser.readFromFile(problemId);
        double benchmark = FileParser.getBenchmarkDistancesFromFile(problemId).get(4); // 0=0%, 1=5%, 2=10%, 3=20%, 4=30%
        System.out.printf("Benchmark %f. %n", benchmark);

        GeneticAlgorithm ga = new GeneticAlgorithm(problemInstance, populationSize, crossoverRate, mutationRate, elitism, memoizeRoutes);

        System.out.println("---------------------------------");
        while (ga.getPopulation().getGeneration() <= generations && ga.getPopulation().getBestDuration() > benchmark) {
            System.out.printf("Generation %d. %n", ga.getPopulation().getGeneration());
            ga.update();
        }
        ga.exit();
        System.out.println("---------------------------------");

        System.out.printf("Best duration: %.2f %n", ga.getPopulation().getBestDuration());
        System.out.printf("Average duration: %.2f %n", ga.getPopulation().getAverageDuration());

        // FileParser.writeToFile(problemId, ga.getPopulation().getAlpha());
    }
}
