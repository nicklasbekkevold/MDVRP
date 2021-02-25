package main.java.ga;

import main.java.Util;
import main.java.domain.Customer;
import main.java.domain.Vehicle;

import java.util.*;

public class Population implements Iterable<Chromosome> {

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

    public Chromosome getAlpha() { return Collections.max(population); }

    @Override
    public Iterator<Chromosome> iterator() { return population.iterator(); }

    public void update() {
        modified = true;
        generation++;
    }

    public Population selection() {
        return null;
    }

    private List<Chromosome> BestCostRouteCrossover() {
        List<Chromosome> parents = Util.randomChoice(population, 2);
        Chromosome parentA = parents.get(0), parentB = parents.get(1);

        int depotIndex = new Random().nextInt(parentA.getChromosome().size());
        Vehicle vehicleA = Util.randomChoice(parentA.getChromosome().get(depotIndex).getVehicles(), 1).get(0);
        Vehicle vehicleB = Util.randomChoice(parentB.getChromosome().get(depotIndex).getVehicles(), 1).get(0);
        List<Customer> vehicleACustomers = new ArrayList<>(vehicleA.getCustomers()), vehicleBCustomers = new ArrayList<>(vehicleB.getCustomers());

        parentA.removeCustomers(vehicleBCustomers);
        parentB.removeCustomers(vehicleACustomers);

        for (Customer customer : vehicleACustomers) {
            List<Boolean> feasibleIndices = new ArrayList<>();
            List<Double> insertionCosts = new ArrayList<>();
            for (Vehicle parentBVehicle : parentB.getChromosome().get(depotIndex).getVehicles()) {
                for (int i = 0; i <= parentBVehicle.getCustomers().size(); i++) {
                    double insertionCost = parentBVehicle.getInsertionCost(customer, i);
                    insertionCosts.add(insertionCost);
                    if (parentBVehicle.getDuration() + insertionCost <= 0 && parentBVehicle.getLoad() <= 0) {
                        feasibleIndices.add(true);
                    } else {
                        feasibleIndices.add(false);
                    }
                }
            }
            if (new Random().nextDouble() <= 0.8) {
                for (int i = 0; i < feasibleIndices.size(); i++) {
                    if (feasibleIndices.get(i)) {

                    }
                }
            }
        }

        return parents;
    }

}
