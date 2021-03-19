package main.java;
import main.java.ga.GeneticAlgorithm;

public class Main {

    public static void main(String[] args) {
        int generations = 200;
        int populationSize = 50;
        double crossoverRate = 0.5;
        double mutationRate = 0.5;
        boolean elitism = true;
        boolean memoizeRoutes = false;

        String problemId = "p08";
        MDVRP problemInstance = FileParser.readFromFile(problemId);
        double benchmark = FileParser.getBenchmarkDistancesFromFile(problemId).get(2); // 0=0%, 1=5%, 2=10%, 3=20%, 4=30%
        System.out.printf("Benchmark %f. %n", benchmark);

        GeneticAlgorithm ga = new GeneticAlgorithm(problemInstance, populationSize, crossoverRate, mutationRate, elitism, memoizeRoutes);

        System.out.println("---------------------------------");
        while (ga.getPopulation().getGeneration() <= generations) {
            System.out.printf("Generation %d. %n", ga.getPopulation().getGeneration());
            if (ga.getPopulation().getBestDuration() > benchmark) {
                System.out.println("Solution found. Stopping.");
                FileParser.writeToFile(problemId, ga.getPopulation().getAlpha());
                break;
            }
            ga.update();
        }
        ga.exit();
        System.out.println("---------------------------------");

        System.out.printf("Best duration: %.2f %n", ga.getPopulation().getBestDuration());
        System.out.printf("Average duration: %.2f %n", ga.getPopulation().getAverageDuration());

        // FileParser.writeToFile(problemId, ga.getPopulation().getAlpha());
    }
}
