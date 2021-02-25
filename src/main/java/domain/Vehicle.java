package main.java.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Vehicle implements Iterable<Node> {

    private static int serialNumber = 1;
    private final int vehicleNumber;
    private final Depot depot;
    private List<Customer> customers = new ArrayList<>();
    private boolean modified = true;
    private float routeDuration = 0.0F;
    private int load = 0;

    public Vehicle(Depot depot) {
        this.vehicleNumber = serialNumber++;
        this.depot = depot;
    }

    public Vehicle(final Vehicle vehicle) {
        this.vehicleNumber = vehicle.vehicleNumber;
        this.depot = vehicle.depot;
        this.customers = vehicle.customers;
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.load;
        modified = false;
    }

    public static void resetSerialNumber() {
        Vehicle.serialNumber = 1;
    }

    public Depot getDepot() { return depot; }

    public List<Customer> getCustomers() { return customers; }

    public float getDuration() {
        if (modified) {
            update();
            modified = false;
        }
        return routeDuration;
    }

    public int getLoad() { return load; }

    public void addCustomer(Customer customer) {
        modified = true;
        load += customer.getDemand();
        customers.add(customer);
    }

    public void update() {
        Node previousNode = depot;
        for (Customer customer : customers) {
            routeDuration += previousNode.distance(customer) + customer.getServiceDuration();
            previousNode = customer;
        }
        routeDuration += previousNode.distance(depot);
    }

    public Customer popLastCustomer() {
        modified = true;
        return customers.remove(customers.size() - 1);
    }

    public void removeCustomers(List<Customer> customers) {
        modified = true;
        for (Customer customer : customers) {
            this.customers.remove(customer);
        }
    }

    @Override
    public Iterator<Node> iterator() {
        List<Node> route = new ArrayList<>();
        route.add(depot);
        route.addAll(customers);
        route.add(depot);
        return route.iterator();
    }

    @Override
    public String toString() {
        String customerString = customers.stream().map(customer -> Integer.toString(customer.getId())).collect(Collectors.joining(" "));
        return String.format("%d %4d %9.2f %5d    0 " + customerString, depot.getId(), vehicleNumber, getDuration(), load);
    }
}
