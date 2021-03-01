package main.java.ga;

import main.java.utils.SymmetricPair;
import main.java.utils.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;

import java.util.*;

public class Population implements Iterable<Chromosome> {

    private static final Random random = Util.random;
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

    public Population(Population population) {
        this.generation = population.generation;
        this.averageFitness = population.averageFitness;
        this.diversity = population.diversity;
        this.modified = true;
        this.population = new ArrayList<>(population.population);
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

    public List<Chromosome> getElite(int eliteSize) {
        Collections.sort(population);
        return population.subList(0, eliteSize - 1);
    }

    public Population update() {
        generation++;
        return this;
    }

    public boolean removeChromosome(Chromosome chromosome) {
        return population.remove(chromosome);
    }

    public void evaluate() {
        // Weighted sum or Pareto ranking
        getAverageFitness();
    }

    public SymmetricPair<Chromosome> selection() {
        Chromosome parentA = eliteTournamentSelection();
        Chromosome parentB = eliteTournamentSelection();
        removeChromosome(parentA);
        removeChromosome(parentB);
        return new SymmetricPair<>(parentA, parentB);
    }

    public Chromosome eliteTournamentSelection() {
        List<Chromosome> tournamentSet = Util.randomChoice(population, 2);
        Chromosome chosenChromosome;
        if (random.nextDouble() < ELITE_SELECTION_RATE) {
            chosenChromosome = Collections.min(tournamentSet);
        } else {
            chosenChromosome = tournamentSet.get(random.nextInt(2));
        }
        return chosenChromosome;
    }

    public SymmetricPair<Chromosome> recombination(Chromosome parentA, Chromosome parentB) { return Chromosome.bestCostRouteCrossover.crossover(parentA, parentB); } ;

    public Chromosome mutate(Chromosome chromosome) {
        // TODO
        if (generation % APP_RATE == 0) {
            // Do inter-depot clustering
            return Chromosome.mutate(chromosome);
        } else {
            // Do one type of intra-depot clustering
            return Chromosome.mutate(chromosome);
        }
    }

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
