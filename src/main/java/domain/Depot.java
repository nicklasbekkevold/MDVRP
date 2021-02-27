package main.java.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Depot extends Node {

    private List<Vehicle> vehicles = new CopyOnWriteArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
    }

    public Depot(Depot depot) {
        super(depot);
        this.customers = new ArrayList<>(depot.customers); // Shallow copy
        this.vehicles = new CopyOnWriteArrayList<>(); // Reset vehicles
    }

    public List<Vehicle> getVehicles() { return vehicles; }

    public List<Customer> getCustomers() { return customers; }

    public boolean isEmpty() { return vehicles.isEmpty(); }

    public void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    public void addCustomer(Customer customer) { customers.add(customer); }

    public void removeVehicle(Vehicle vehicle) { vehicles.remove(vehicle); }

}
