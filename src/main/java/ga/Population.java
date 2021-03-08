package main.java.ga;

import main.java.utils.SymmetricPair;
import main.java.utils.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;

import java.util.*;

public class Population implements Iterable<Chromosome> {

    private static final Random random = Util.random;
    private static final double BOUND = 0.4;
    private static final double APP_RATE = 10;
    private static final double ELITE_SELECTION_RATE = 0.8;

    private int generation = 0;
    private double averageFitness = 0.0;
    private double diversity = 0.0;
    private Chromosome alpha;

    private List<Chromosome> population;

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public Population(Population otherPopulation) {
        generation = otherPopulation.generation;
        averageFitness = otherPopulation.averageFitness;
        alpha = otherPopulation.alpha;
        diversity = otherPopulation.diversity;
        population = new ArrayList<>(otherPopulation.population);
    }

    public static Population heuristicInitialization(int populationSize, List<Depot> depots, List<Customer> customers) {
        List<Chromosome> initialPopulation = new ArrayList<>();

        List<Customer> swappableCustomerList = Population.assignCustomersToNearestDepot(depots, customers);
        Chromosome.setSwappableCustomerList(swappableCustomerList);
        for (int i = 0; i < populationSize; i++) {
            initialPopulation.add(new Chromosome(depots));
        }
        return new Population(initialPopulation);
    }

    public int getGeneration() { return generation; }

    public double getBestFitness() { return alpha.getFitness(); }

    public double getAverageFitness() { return averageFitness; }

    public double getDiversity() { return diversity; }

    public Chromosome getAlpha() { return alpha; }

    public List<Chromosome> getElite(int eliteSize) {
        Collections.sort(population);
        return population.subList(0, eliteSize);
    }

    public void removeChromosome(Chromosome chromosome) {
        population.remove(chromosome);
    }

    public void evaluate() {
        // Weighted sum or Pareto ranking
        OptionalDouble result = population.stream().mapToDouble(Chromosome::getFitness).average();
        if (result.isPresent()) {
            averageFitness = result.getAsDouble();
        }
        alpha = Collections.min(population);
    }

    public void paretoRanking() {
        List<Chromosome> remainingPopulation = new LinkedList<>(population);
        int currentRank = 1;
        while (!remainingPopulation.isEmpty()) {
            Collection<Chromosome> rankedChromosomes = new ArrayList<>();
            for (Chromosome chromosome : remainingPopulation) {
                boolean isNonDominated = true;
                for (Chromosome otherChromosome : remainingPopulation) {
                    if (otherChromosome != chromosome && otherChromosome.dominates(chromosome)) {
                        isNonDominated = false;
                        break;
                    }
                }
                if (isNonDominated) {
                    chromosome.setRank(currentRank);
                    rankedChromosomes.add(chromosome);
                }
            }
            remainingPopulation.removeAll(rankedChromosomes);
            currentRank++;
        }
    }

    public SymmetricPair<Chromosome> selection() {
        Chromosome parentA = eliteTournamentSelection();
        Chromosome parentB = eliteTournamentSelection();
        removeChromosome(parentA);
        removeChromosome(parentB);
        return new SymmetricPair<>(parentA, parentB);
    }

    public Chromosome eliteTournamentSelection() {
        SymmetricPair<Chromosome> tournamentSet = Util.randomPair(population);
        Chromosome chosenChromosome;
        if (random.nextDouble() < ELITE_SELECTION_RATE) {
            chosenChromosome = Collections.min(tournamentSet);
        } else {
            chosenChromosome = tournamentSet.get(random.nextInt(2));
        }
        return chosenChromosome;
    }

    public SymmetricPair<Chromosome> crossover(Chromosome parentA, Chromosome parentB) { return Chromosome.bestCostRouteCrossover.crossover(parentA, parentB); }

    public Chromosome mutate(Chromosome chromosome) {
        if (generation % APP_RATE == 0) {
            // Do inter-depot clustering
            return Chromosome.interDepotMutation.mutate(chromosome);
        } else {
            // Do one type of intra-depot clustering
            return Chromosome.intraDepotMutation.mutate(chromosome);
        }
    }

    public Population replacement(List<Chromosome> population) {
        this.population = population;
        generation++;
        return this;
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
                if (depot != nearestDepot) {
                    double distance = customer.distance(depot);
                    if ((distance / minimumDistance - 1) <= BOUND) {
                        customer.setBorderLine();
                        customer.addCandidateDepotId(depot.getId());
                        swappableCustomerList.add(customer);
                    }
                }
            }
            customer.addCandidateDepotId(nearestDepot.getId());
        }
        return swappableCustomerList;
    }

    @Override
    public Iterator<Chromosome> iterator() { return population.iterator(); }
}
