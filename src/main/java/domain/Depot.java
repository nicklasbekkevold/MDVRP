package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public final class Depot extends Node {

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
    }

    public Depot(Depot depot) {
        super(depot);
        this.customers = new ArrayList<>(depot.customers); // Shallow copy
        this.vehicles = new ArrayList<>(); // Reset vehicles
    }

    public List<Vehicle> getVehicles() { return vehicles; }

    public List<Customer> getCustomers() { return customers; }

    public void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    public void addCustomer(Customer customer) { customers.add(customer); }

}
