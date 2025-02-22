package main.java.domain;

import main.java.utils.Memorandum;
import main.java.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Vehicle implements Iterable<Node> {

    private static final Function<Vehicle, Double> routeDuration = vehicle -> {
        double routeDuration = 0;
        Node previousNode = vehicle.depot;
        for (Node node : vehicle) {
            routeDuration += previousNode.distance(node);
            previousNode = node;
        }
        return routeDuration;
    };
    private static final Function<Vehicle, Double> memoizedDurations = Memorandum.memoize(routeDuration);

    private static final Random random = Util.random;
    private static Function<Vehicle, Double> durationFunction;

    private final Depot depot;
    private List<Customer> customers = new ArrayList<>();
    private int load = 0;

    public Vehicle(Depot depot) {
        this.depot = depot;
    }

    public Vehicle(final Vehicle vehicle) {
        depot = vehicle.depot;
        customers = new ArrayList<>(vehicle.customers);
        load = vehicle.load;
    }

    public Vehicle(final Vehicle vehicle, final Depot depot) {
        this.depot = depot;
        customers = new ArrayList<>(vehicle.customers);
        load = vehicle.load;
    }

    public static void memoizeRoutes(boolean memoizeRoutes) {
        if (memoizeRoutes) {
            durationFunction = memoizedDurations;
        }
        durationFunction = routeDuration;
    }

    public List<Customer> getCustomers() { return customers; }

    public double getDuration() { return durationFunction.apply(this); }

    public int getLoad() { return load; }

    public double getInsertionCost(int index, Customer customer) {
        if (customers.size() == 0) {
            return 2 * customer.distance(depot);
        } else if (index == 0 || index == customers.size()) {
            Customer endCustomer = customers.get(index == customers.size() ? customers.size() - 1 : index);
            return depot.distance(customer) + customer.distance(endCustomer) - depot.distance(endCustomer);
        }
        return customers.get(index - 1).distance(customer) + customer.distance(customers.get(index)) - customers.get(index - 1).distance(customers.get(index));
    }

    public void insertCustomer(int index, Customer customer) {
        load += customer.getDemand();
        customers.add(index, customer);
    }

    public void addCustomer(Customer customer) {
        load += customer.getDemand();
        customers.add(customer);
    }

    public Customer popLastCustomer() {
        Customer customer = customers.remove(customers.size() - 1);
        load -= customer.getDemand();
        if (this.customers.size() == 0) {
            depot.removeVehicle(this);
        }
        return customer;
    }

    public void removeCustomer(Customer customerToRemove) {
        boolean removed = customers.remove(customerToRemove);
        if (removed) {
            load -= customerToRemove.getDemand();
        }
        if (customers.size() == 0) {
            depot.removeVehicle(this);
        }
    }

    public void removeCustomers(final List<Customer> customersToRemove) {
        customers.stream().filter(customersToRemove::contains).forEach(customer -> load -= customer.getDemand());
        customers.removeAll(customersToRemove);
        if (customers.size() == 0) {
            depot.removeVehicle(this);
        }
    }

    private List<Node> getRoute() {
        List<Node> route = new ArrayList<>();
        route.add(depot);
        route.addAll(customers);
        route.add(depot);
        return route;
    }

    private String getRouteString(List<? extends Node> route) {
        return route.stream().map(node -> Integer.toString(node.getId())).collect(Collectors.joining(" "));
    }

    @Override
    public Iterator<Node> iterator() {
        return getRoute().iterator();
    }

    @Override
    public String toString() {
        return String.format("%d %4d %9.2f %5d    0 " + getRouteString(customers), depot.getDepotNumber(), depot.getVehicles().indexOf(this) + 1, getDuration(), load);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return getRouteString(getRoute()).equals(vehicle.getRouteString(vehicle.getRoute()));
    }

    @Override
    public int hashCode() {
        return getRouteString(getRoute()).hashCode();
    }
}
