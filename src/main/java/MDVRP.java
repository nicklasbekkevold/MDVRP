package main.java;

import main.java.domain.Customer;
import main.java.domain.Depot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MDVRP {

    private final String problem;

    private final int numberOfVehiclesPerDepot; // m
    private final int numberOfCustomers; // n
    private final int numberOfDepots; // t

    private final int maxRouteDuration; // D
    private final int maxVehicleLoad; // Q

    private final List<Depot> depots;
    private final List<Customer> customers;

    // Used for plotting:
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public MDVRP(
        String problem,
        int numberOfVehiclesPerDepot,
        int numberOfCustomers,
        int numberOfDepots,
        int maxRouteDuration,
        int maxVehicleLoad,
        List<Depot> depots,
        List<Customer> customers,
        int minX,
        int minY,
        int maxX,
        int maxY
    ) {
        this.problem = problem;
        this.numberOfVehiclesPerDepot = numberOfVehiclesPerDepot;
        this.numberOfCustomers = numberOfCustomers;
        this.numberOfDepots = numberOfDepots;
        this.maxRouteDuration = maxRouteDuration;
        this.maxVehicleLoad = maxVehicleLoad;
        this.depots = depots;
        this.customers = customers;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public String getProblem() { return problem; }

    public int getNumberOfVehiclesPerDepot() { return numberOfVehiclesPerDepot; }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public int getNumberOfDepots() {
        return numberOfDepots;
    }

    public int getMaxRouteDuration() {
        return maxRouteDuration;
    }

    public int getMaxVehicleLoad() {
        return maxVehicleLoad;
    }

    public List<Depot> getDepots() {
        return depots;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getMinX() { return minX; }

    public int getMinY() { return minY; }

    public int getMaxX() { return maxX; }

    public int getMaxY() { return maxY; }

}
