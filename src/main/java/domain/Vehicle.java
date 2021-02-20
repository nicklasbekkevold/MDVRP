package main.java.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Vehicle {

    private Depot depot;
    private List<Customer> customers = new ArrayList<>();
    private boolean modified = true;
    private float routeDuration = 0.0F;
    private int load = 0;

    public Vehicle(Depot depot) { this.depot = depot; }

    public Vehicle(final Vehicle vehicle) {
        this.depot = vehicle.depot;
        this.customers = vehicle.getCustomers();
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.getLoad();
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
        Customer lastCustomer = customers.remove(customers.size() - 1);
        return lastCustomer;
    }

    public void removeCustomers(List<Customer> customers) {
        modified = true;
        for (Customer customer : customers) {
            this.customers.remove(customer);
        }
    }

}
