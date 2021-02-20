package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private Depot depot;
    private List<Customer> customers = new ArrayList<>();
    private float routeDuration = 0.0F;
    private int load = 0;

    public Vehicle(final Vehicle vehicle) {
        this.customers = vehicle.getCustomers();
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.getLoad();
    }

    public List<Customer> getCustomers() { return customers; }

    public Depot getDepot() { return depot; }

    public float getDuration() { return routeDuration; }

    public int getLoad() { return load; }

    public void setDepot(Depot depot) { this.depot = depot; }

    public void setDuration(float routeDuration) { this.routeDuration = routeDuration; }

    public void setLoad(int load) { this.load = load; }

    public void addCustomer(Customer customer) {
        if (customers.size() >= 1) {
            routeDuration -= customers.get(customers.size() - 2).distance(depot);
            routeDuration += customers.get(customers.size() - 2).distance(customer);
        } else {
            routeDuration += depot.distance(customer);
        }
        routeDuration += customer.distance(depot) + customer.getServiceDuration();
        load += customer.getDemand();
        customers.add(customer);
    }

    public void addCustomer(Customer customer, float routeDuration, int load) {
        customers.add(customer);
        this.routeDuration += routeDuration;
        this.load += load;
    }

    public Customer popLastCustomer() {
        Customer lastCustomer = customers.remove(customers.size() - 1);
        load -= lastCustomer.getDemand();
        routeDuration -= (lastCustomer.distance(depot) + lastCustomer.getServiceDuration());
        if (customers.size() >= 1) {
            routeDuration -= customers.get(customers.size() - 1).distance(lastCustomer);
            routeDuration += customers.get(customers.size() - 1).distance(depot);
        } else {
            routeDuration -= depot.distance(lastCustomer);
        }
        return lastCustomer;
    }

    public void removeCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            this.customers.remove(customer);
        }
    }

}
