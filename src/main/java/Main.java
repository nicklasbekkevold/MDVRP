package main.java;

import main.java.ga.GeneticAlgorithm;

public class Main {

    public static void main(String[] args) {
        int generations = 200;
        int populationSize = 100;
        double crossoverRate = 0.8;
        double mutationRate = 0.3;
        boolean elitism = true;

        String problemId = "p22";
        MDVRP problemInstance = FileParser.readFromFile(problemId);
        GeneticAlgorithm ga = new GeneticAlgorithm(problemInstance, populationSize, crossoverRate, mutationRate, elitism);

        System.out.println("---------------------------------");
        for (int i = 1; i <= generations; i++) {
            System.out.printf("Generation %d. %n", i);
            ga.update();
        }
        ga.exit();
        System.out.println("---------------------------------");

        System.out.printf("Best duration: %.2f %n", ga.getPopulation().getBestDuration());
        System.out.printf("Average duration: %.2f %n", ga.getPopulation().getAverageDuration());

        // FileParser.writeToFile(problemId, ga.getPopulation().getAlpha());
    }
}
