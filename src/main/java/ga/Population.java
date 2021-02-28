package main.java.ga;

import main.java.utils.SymmetricPair;
import main.java.utils.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;

public class Population implements Iterable<Chromosome> {

    private static final Random random = new Random();
    private static final double BOUND = 20;
    private static final double APP_RATE = 10;
    private static final double ELITE_SELECTION_RATE = 0.8;

    private int generation = 0;
    private double averageFitness = 0.0;
    private double diversity = 0.0;
    private boolean modified = true;

    private final List<Chromosome> population;

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public static Population heuristicInitialization(int populationSize, List<Depot> depots, List<Customer> customers) {
        List<Chromosome> initialPopulation = new ArrayList<>();

        List<Customer> swappableCustomerList = Population.assignCustomersToNearestDepot(depots, customers);
        for (int i = 0; i < populationSize; i++) {
            initialPopulation.add(new Chromosome(depots, swappableCustomerList));
        }
        return new Population(initialPopulation);
    }

    public int getGeneration() { return generation; }

    public double getBestFitness() { return getAlpha().getFitness(); }

    public double getAverageFitness() {
        if (modified) {
            averageFitness = population.stream().mapToDouble(Chromosome::getFitness).average().getAsDouble();
            modified = false;
        }
        return averageFitness;
    }

    public double getDiversity() { return diversity; }

    public Chromosome getAlpha() { return Collections.min(population); }

    public Population update() {
        generation++;
        return this;
    }

    public void evaluate() {
        // Weighted sum or Pareto ranking
        getAverageFitness();
    }

    public Chromosome eliteTournamentSelection() {
        List<Chromosome> contestants = Util.randomChoice(population, 2);
        if (random.nextDouble() < ELITE_SELECTION_RATE) {
            return Collections.min(contestants);
        } else {
            return contestants.get(random.nextInt(1));
        }
    }

    public Chromosome mutate(Chromosome chromosome) {
        // TODO
        if (generation % APP_RATE == 0) {
            // Do intra-depot clustering
            return Chromosome.mutate(chromosome);
        } else {
            // Do one type of inter-depot clustering
            return Chromosome.mutate(chromosome);
        }
    }

    public Crossover bestCostRouteCrossover = (parentA, parentB) -> {
        modified = true;

        int depotIndex = new Random().nextInt(parentA.getChromosome().size());
        Depot parentADepot = parentA.getChromosome().get(depotIndex);
        Depot parentBDepot = parentB.getChromosome().get(depotIndex);

        if (parentADepot.isEmpty() || parentBDepot.isEmpty()) {
            return null;
        }

        Vehicle vehicleA = parentADepot.getVehicles().get(random.nextInt(parentADepot.getVehicles().size()));
        Vehicle vehicleB = parentBDepot.getVehicles().get(random.nextInt(parentBDepot.getVehicles().size()));

        List<Customer> vehicleACustomers = new ArrayList<>(vehicleA.getCustomers());
        List<Customer> vehicleBCustomers = new ArrayList<>(vehicleB.getCustomers());

        parentA.removeCustomers(vehicleBCustomers);
        parentB.removeCustomers(vehicleACustomers);

        for (Customer customer : vehicleACustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentBDepot, customer);
        }
        for (Customer customer : vehicleBCustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentADepot, customer);
        }
        return new SymmetricPair<>(parentA, parentB);
    };

    private static List<Customer> assignCustomersToNearestDepot(final List<Depot> depots, final List<Customer> customers) {
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
                        customer.setBorderLine();
                        customer.addCandidateDepot(depot);
                        swappableCustomerList.add(customer);
                    }
                }
            }
            customer.addCandidateDepot(nearestDepot);
        }
        return swappableCustomerList;
    }

    @Override
    public Iterator<Chromosome> iterator() { return population.iterator(); }
}
