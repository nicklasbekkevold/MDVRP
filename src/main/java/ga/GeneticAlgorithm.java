package main.java.ga;

import main.java.MDVRP;
import main.java.domain.Customer;
import main.java.domain.Depot;

import java.util.*;

public class GeneticAlgorithm {

    // GA Parameters
    private final int populationSize;
    private final float crossOverRate;
    private final float mutationRate;
    private final boolean elitism;

    private final static float BOUND = 2;

    // Constraints
    private final int numberOfVehiclesPerDepot;
    private final int maxRouteDuration;
    private final int maxVehicleLoad;

    private Population population;

    public GeneticAlgorithm(
            final MDVRP problemInstance,
            int populationSize,
            float crossOverRate,
            float mutationRate,
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
        assignCustomersToNearestDepot(problemInstance.getCustomers(), problemInstance.getDepots());

        List<Chromosome> initialPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            initialPopulation.add(new Chromosome(problemInstance.getDepots()));
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
        // Acceptance (replacement)
        // Elitism step
        // Do one loop
        return population;
    }

    private void assignCustomersToNearestDepot(final List<Customer> customers, final List<Depot> depots) {
        final Set<Customer> swappableCustomerList = new HashSet<>();

        for (Customer customer : customers) {

            float minimumDistance = Float.MAX_VALUE;
            Depot nearestDepot = null;

            for (Depot depot : depots) {
                float distance = customer.distance(depot);
                if (distance < minimumDistance) {
                    minimumDistance = distance;
                    nearestDepot = depot;
                }
            }
            nearestDepot.addCustomer(customer);

            // Check for borderline customers
            for (Depot depot : depots) {
                if (!depot.equals(nearestDepot)) {
                    float distance = customer.distance(depot);
                    if ((distance - minimumDistance / minimumDistance) <= BOUND) {
                        customer.addCandidateDepot(depot);
                        swappableCustomerList.add(customer);
                    }
                }
            }
            customer.addCandidateDepot(nearestDepot);
        }
    }
}
