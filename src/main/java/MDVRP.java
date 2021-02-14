package main.java;

import main.java.domain.Customer;
import main.java.domain.Depot;

import java.util.ArrayList;
import java.util.List;

public class MDVRP {

    private int numberOfVehiclesPerDepot = 0; //m
    private int numberOfCustomers = 0; //n
    private int numberOfDepots = 0; //t

    private int maxRouteDuration = 0; //D
    private int maxVehicleLoad = 0; //Q

    private final List<Depot> depots = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();

    public void assignCustomersToDepots() {
        for (Customer customer : customers) {

            double minimumDistance = Double.MAX_VALUE;
            Depot nearestDepot = null;

            for (Depot depot : depots) {
                double distance = customer.distance(depot);
                if (distance < minimumDistance) {
                    minimumDistance = distance;
                    nearestDepot = depot;
                }
            }

            nearestDepot.addCustomer(customer);
        }
    }

    // Getters:
    public int getNumberOfVehiclesPerDepot() {
        return numberOfVehiclesPerDepot;
    }

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


    // Setters:
    public void setNumberOfVehiclesPerDepot(int numberOfVehiclesPerDepot) {
        this.numberOfVehiclesPerDepot = numberOfVehiclesPerDepot;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public void setNumberOfDepots(int numberOfDepots) {
        this.numberOfDepots = numberOfDepots;
    }

    public void setMaxRouteDuration(int maxRouteDuration) {
        this.maxRouteDuration = maxRouteDuration;
    }

    public void setMaxVehicleLoad(int maxVehicleLoad) {
        this.maxVehicleLoad = maxVehicleLoad;
    }

    public void addDepot(Depot depot) {
        depots.add(depot);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}
