package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private Depot depot;
    private List<Customer> customers;
    private float routeDuration = 0.0F;
    private int load = 0;

    public List<Customer> getCustomers() { return customers; }

    public Customer popLastCustomer() {
        Customer lastCustomer = customers.remove(customers.size() - 1);
        routeDuration -= lastCustomer.distance(depot);
        load -= lastCustomer.getDemand();
        if (customers.size() >= 2) {
            routeDuration -= customers.get(customers.size() - 2).distance(lastCustomer);
            routeDuration += customers.get(customers.size() - 2).distance(depot);
        }
        return lastCustomer;
    }

    public Depot getDepot() { return depot; }

    public float getDuration() { return routeDuration; }

    public int getLoad() { return load; }

    public void setDepot(Depot depot) { this.depot = depot; }

    public void setDuration(float routeDuration) { this.routeDuration = routeDuration; }

    public void setLoad(int load) { this.load = load; }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        if (customers.size() >= 2) {
            routeDuration -= customers.get(customers.size() - 2).distance(depot);
            routeDuration += customers.get(customers.size() - 2).distance(customer);
        }
        routeDuration += customer.distance(depot);
        load += customer.getDemand();
    }

    public void addCustomer(Customer customer, float routeDuration, int load) {
        customers.add(customer);
        this.routeDuration += routeDuration;
        this.load += load;
    }

    public void removeCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            this.customers.remove(customer);
        }
    }

    public Vehicle() {
        customers = new ArrayList<>();
    }

    public Vehicle(final Vehicle vehicle) {
        this.customers = vehicle.getCustomers();
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.getLoad();
    }
}
