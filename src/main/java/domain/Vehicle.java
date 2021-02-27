package main.java.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Vehicle implements Iterable<Node> {

    private static int serialNumber = 1;

    private final int vehicleNumber;
    private final Depot depot;

    private List<Customer> customers = new CopyOnWriteArrayList<>();
    private double routeDuration = 0.0;
    private int load = 0;
    private boolean modified = true;

    public Vehicle(Depot depot) {
        this.vehicleNumber = serialNumber++;
        this.depot = depot;
    }

    public Vehicle(final Vehicle vehicle) {
        this.vehicleNumber = vehicle.vehicleNumber;
        this.depot = vehicle.depot;
        this.customers = new CopyOnWriteArrayList<>(vehicle.customers);
        this.routeDuration = vehicle.getDuration();
        this.load = vehicle.load;
        modified = false;
    }

    public static void resetSerialNumber() {
        Vehicle.serialNumber = 1;
    }

    public Depot getDepot() { return depot; }

    public List<Customer> getCustomers() { return customers; }

    public double getDuration() {
        if (modified) {
            update();
            modified = false;
        }
        return routeDuration;
    }

    public int getLoad() { return load; }

    public double getInsertionCost(int index, Customer customer) {
        if (customers.size() == 1) {
            return customers.get(0).distance(customer) - depot.distance(customers.get(0)) - depot.distance(customer);
        } else if (index == 0 || index == customers.size()) {
            Customer endCustomer = customers.get(index == customers.size() ? customers.size() - 1 : index);
            return depot.distance(customer) + customer.distance(endCustomer) - depot.distance(endCustomer);
        }
        return customers.get(index - 1).distance(customer) + customer.distance(customers.get(index)) - customers.get(index - 1).distance(customers.get(index));
    }

    public void insertCustomer(int index, Customer customer) {
        modified = true;
        load += customer.getDemand();
        customers.add(index, customer);
    }

    public void addCustomer(Customer customer) {
        modified = true;
        load += customer.getDemand();
        customers.add(customer);
    }

    public void swapRandomCustomer(Vehicle otherVehicle) {
        int customerIndex = new Random().nextInt(customers.size());
        int otherCustomerIndex = new Random().nextInt(customers.size());
        Customer temp = otherVehicle.customers.get(otherCustomerIndex);
        otherVehicle.customers.set(otherCustomerIndex, customers.get(customerIndex));
        customers.set(customerIndex, temp);
    }

    public void update() {
        routeDuration = 0;
        Node previousNode = depot;
        for (Customer customer : customers) {
            routeDuration += previousNode.distance(customer);
            previousNode = customer;
        }
        routeDuration += previousNode.distance(depot);
    }

    public Customer popLastCustomer() {
        modified = true;
        return customers.remove(customers.size() - 1);
    }

    public void removeCustomers(final List<Customer> customers) {
        modified = true;
        this.customers.removeAll(customers);
        if (this.customers.size() == 0) {
            depot.removeVehicle(this);
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
