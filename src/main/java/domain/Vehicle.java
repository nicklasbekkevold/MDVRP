package main.java.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vehicle implements Iterable<Node> {

    private Depot depot;
    private List<Customer> customers = new ArrayList<>();
    private boolean modified = true;
    private float routeDuration = 0.0F;
    private int load = 0;

    public Vehicle(Depot depot) { this.depot = depot; }

    public Vehicle(final Vehicle vehicle) {
        this.depot = vehicle.depot;
        this.customers = vehicle.customers;
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.load;
        modified = false;
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
}
