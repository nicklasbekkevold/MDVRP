package main.java.ga;

import main.java.Util;
import main.java.domain.Customer;
import main.java.domain.Vehicle;

import java.util.*;

public class Population implements Iterable<Chromosome> {

    private final static double APPRATE = 10;

    private int generation = 0;
    private double averageFitness = 0.0;
    private double diversity = 0.0;
    private boolean modified = true;

    private final List<Chromosome> population;

    public Population() {
        this.population = null;
    }

    public Population(List<Chromosome> population) {
        this.population = population;
    }

    public int getGeneration() { return generation; }

    public double getMaxFitness() { return getAlpha().getFitness(); }

    public double getAverageFitness() {
        if (modified) {
            averageFitness = population.stream().mapToDouble(Chromosome::getFitness).average().getAsDouble();
            modified = false;
        }
        return averageFitness;
    }

    public double getDiversity() { return diversity; }

    public Chromosome getAlpha() { return Collections.min(population); }

    public List<Chromosome> selection() {
        // TODO
        return null;
    }

    public void mutate() {
        // TODO
        if (generation % APPRATE == 0) {
            // Do intra-depot clustering
        } else {
            // Do one type of inter-depot clustering
        }
    }

    public List<Chromosome> bestCostRouteCrossover() {
        List<Chromosome> parents = Util.randomChoice(population, 2);

        Chromosome parentA = parents.get(0);
        Chromosome parentB = parents.get(1);

        int depotIndex = new Random().nextInt(parentA.getChromosome().size());
        Vehicle vehicleA = Util.randomChoice(parentA.getChromosome().get(depotIndex).getVehicles(), 1).get(0);
        Vehicle vehicleB = Util.randomChoice(parentB.getChromosome().get(depotIndex).getVehicles(), 1).get(0);

        List<Customer> vehicleACustomers = new ArrayList<>(vehicleA.getCustomers());
        List<Customer> vehicleBCustomers = new ArrayList<>(vehicleB.getCustomers());

        parentA.removeCustomers(vehicleBCustomers);
        parentB.removeCustomers(vehicleACustomers);

        for (Customer customer : vehicleACustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentB.getChromosome().get(depotIndex), customer);
        }
        for (Customer customer : vehicleBCustomers) {
            RouteScheduler.insertCustomerWithBestRouteCost(parentA.getChromosome().get(depotIndex), customer);
        }

        return parents;
    }

    @Override
    public Iterator<Chromosome> iterator() { return population.iterator(); }

}
