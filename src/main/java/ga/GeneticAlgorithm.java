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
    public final int MAX_ROUTE_DURATION;
    public final int MAX_VEHICLE_LOAD;

    private Population population;

    public GeneticAlgorithm(final MDVRP problemInstance, int populationSize, float crossOverRate, float mutationRate, boolean elitism) {
        MAX_ROUTE_DURATION = problemInstance.getMaxRouteDuration();
        MAX_VEHICLE_LOAD = problemInstance.getMaxVehicleLoad();

        this.populationSize = 1;
        this.crossOverRate = 0.5F;
        this.mutationRate = 0.01F;
        this.elitism = elitism;

        assignCustomersToNearestDepot(problemInstance.getCustomers(), problemInstance.getDepots());
        population = new Population();

        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosome(problemInstance.getDepots()));
        }
    }

    public Population update() {
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
