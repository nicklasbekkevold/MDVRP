package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Depot extends Node {

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
    }

    public List<Customer> getVehicles() { return customers; }
    public List<Customer> getCustomers() { return customers; }

    public void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }
    public void addCustomer(Customer customer) { customers.add(customer); }
}
