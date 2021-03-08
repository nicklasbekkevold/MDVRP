package main.java.domain;

import main.java.utils.Memorandum;
import main.java.utils.Pair;
import main.java.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Vehicle implements Iterable<Node> {

    private final static Function<Vehicle, Double> routeDuration = vehicle -> {
        double routeDuration = 0;
        Node previousNode = vehicle.depot;
        for (Node node : vehicle) {
            routeDuration += previousNode.distance(node);
            previousNode = node;
        }
        return routeDuration;
    };
    private final static Function<Vehicle, Double> memoizedDurations = Memorandum.memoize(routeDuration);

    private static final Random random = Util.random;

    private final Depot depot;

    private List<Customer> customers = new CopyOnWriteArrayList<>();
    private int load = 0;

    public Vehicle(Depot depot) {
        this.depot = depot;
    }

    public Vehicle(final Vehicle vehicle) {
        depot = vehicle.depot;
        customers = new CopyOnWriteArrayList<>(vehicle.customers);
        load = vehicle.load;
    }

    public List<Customer> getCustomers() { return customers; }

    public double getDuration() { return memoizedDurations.apply(this); }

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

    public void swapRandomCustomer(Vehicle otherVehicle) {
        int customerIndex = random.nextInt(customers.size());
        int otherCustomerIndex = random.nextInt(otherVehicle.customers.size());
        Customer temp = otherVehicle.customers.get(otherCustomerIndex);
        otherVehicle.customers.set(otherCustomerIndex, customers.get(customerIndex));
        customers.set(customerIndex, temp);
    }

    public Customer popLastCustomer() {
        Customer customer = customers.remove(customers.size() - 1);
        if (this.customers.size() == 0) {
            depot.removeVehicle(this);
        }
        return customer;
    }

    public void removeCustomer(Customer customerToRemove) {
        depot.removeCustomer(customerToRemove);
        customers.remove(customerToRemove);
        if (customers.size() == 0) {
            depot.removeVehicle(this);
        }
    }

    public void removeCustomers(final List<Customer> customersToRemove) {
        depot.removeCustomers(customersToRemove);
        customers.removeAll(customersToRemove);
        if (customers.size() == 0) {
            depot.removeVehicle(this);
        }
    }

    public Pair<Vehicle> split(int index) {
        List<Customer> firstHalf = customers.subList(0, index);
        List<Customer> secondHalf = customers.subList(index, customers.size());
        Vehicle v1 = new Vehicle(this);
        Vehicle v2 = new Vehicle(this);
        v1.removeCustomers(secondHalf);
        v2.removeCustomers(firstHalf);
        depot.removeVehicle(this); // 'Delete' this
        if (v1.customers.size() > 0) {
            depot.addVehicle(v1);
        }
        if (v2.customers.size() > 0) {
            depot.addVehicle(v2);
        }
        return new Pair<>(v1, v2);
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
