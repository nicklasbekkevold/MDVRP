package main.java.ga;

import main.java.MDVRP;

public class GeneticAlgorithm {

    // Parameters
    private final int populationSize = 100;
    private final float crossOverRate = 0.5F;
    private final float mutationRate = 0.01F;
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
