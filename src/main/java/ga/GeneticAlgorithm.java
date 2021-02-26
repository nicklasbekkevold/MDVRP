package main.java.ga;

import main.java.MDVRP;
import main.java.domain.Customer;
import main.java.domain.Depot;

import java.util.*;

public class GeneticAlgorithm {

    // GA Parameters
    private final int populationSize;
    private final double crossOverRate;
    private final double mutationRate;
    private final boolean elitism;

    private final static double APPRATE = 10;
    private final static double BOUND = 2;

    // Constraints
    private final int numberOfVehiclesPerDepot;
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
        maxVehicleLoad = problemInstance.getMaxVehicleLoad();

        this.populationSize = populationSize;
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;

        RouteScheduler.setNumberOfVehiclesPerDepot(numberOfVehiclesPerDepot);
        RouteScheduler.setMaxVehicleLoad(maxVehicleLoad);
        List<Customer> swappableCustomerList = assignCustomersToNearestDepot(problemInstance.getCustomers(), problemInstance.getDepots());

        List<Chromosome> initialPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            initialPopulation.add(new Chromosome(problemInstance.getDepots(), swappableCustomerList));
        }
        population = new Population(initialPopulation);
    }

    public Population getPopulation() {
        return population;
    }

    public Population update() {
        // Selection
        // Recombination (crossover)
        // Mutation (intra-depot & inter-depot)
        if (population.getGeneration() % APPRATE == 0) {
            // Do intra-depot clustering
        } else {
            // Do one type of inter-depot clustering
        }
        // Acceptance (replacement)
        // Elitism step
        // Do one loop
        population.update();
        return population;
    }

    private List<Customer> assignCustomersToNearestDepot(final List<Customer> customers, final List<Depot> depots) {
        final List<Customer> swappableCustomerList = new ArrayList<>();

        for (Customer customer : customers) {

            double minimumDistance = Float.MAX_VALUE;
            Depot nearestDepot = null;

            for (Depot depot : depots) {
                double distance = customer.distance(depot);
                if (distance < minimumDistance) {
                    minimumDistance = distance;
                    nearestDepot = depot;
                }
            }
            nearestDepot.addCustomer(customer);

            // Check for borderline customers
            for (Depot depot : depots) {
                if (!depot.equals(nearestDepot)) {
                    double distance = customer.distance(depot);
                    if ((distance - minimumDistance / minimumDistance) <= BOUND) {
                        customer.addCandidateDepot(depot);
                        swappableCustomerList.add(customer);
                    }
                }
            }
            customer.addCandidateDepot(nearestDepot);
        }
        return swappableCustomerList;
    }
}
