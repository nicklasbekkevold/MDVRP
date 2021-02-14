package main.java.ga;

import main.java.MDVRP;

public class GeneticAlgorithm {

    // Parameters
    private final int populationSize = 100;
    private final double crossOverRate = 0.5;
    private final double mutationRate = 0.01;
    private final int durationPenaltyRate = 20;
    private final boolean elitism = true;

    private Population population;

    public GeneticAlgorithm(MDVRP mdvpr) {
        population = new Population();
    }

    public Population update() {
        // Do one loop
        return population;
    }
}
