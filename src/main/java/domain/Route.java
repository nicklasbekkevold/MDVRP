package main.java.domain;

import main.java.domain.Node;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private Depot depot;
    private List<Customer> customers;
    private float duration = 0.0F;
    private int load = 0;

    public List<Customer> getCustomers() { return customers; }

    public Customer getLastCustomer() { return customers.get(customers.size() - 1); }

    public Depot getDepot() { return depot; }

    public float getDuration() { return duration; }

    public int getLoad() { return load; }

    public void setDepot(Depot depot) { this.depot = depot; }

    public void setDuration(float duration) { this.duration = duration; }

    public void setLoad(int load) { this.load = load; }

    public void addCustomer(Customer customer) { customers.add(customer); }

    public void addCustomer(Customer customer, float duration, int load) {
        customers.add(customer);
        this.duration += duration;
        this.load += load;
    }

    public Route() {
        customers = new ArrayList<>();
    }
    public Route(final Route route) {
        this.customers = route.getCustomers();
        this.duration = route.getDuration();
        this.load = route.getLoad();
    }
}
