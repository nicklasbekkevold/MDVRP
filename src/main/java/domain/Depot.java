package main.java.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Depot extends Node {

    private static int serialNumber = 1;

    private final int depotNumber;
    private List<Customer> customers = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
        this.depotNumber = serialNumber++;
    }

    public Depot(Depot depot) {
        super(depot);
        this.depotNumber = depot.depotNumber;
        this.customers = new ArrayList<>(depot.customers); // Shallow copy
        this.vehicles = depot.vehicles.stream().map(vehicle -> new Vehicle(vehicle, this)).collect(Collectors.toList()); // Deep copy
    }

    public static void resetSerialNumber() { Depot.serialNumber = 1; }

    public int getDepotNumber() { return depotNumber; }

    public List<Vehicle> getVehicles() { return vehicles; }

    public List<Customer> getCustomers() { return customers; }

    public void arrangeCustomers() {
        customers = vehicles.stream().flatMap(vehicle -> vehicle.getCustomers().stream()).collect(Collectors.toList());
    }

    public void addCustomer(Customer customer) { customers.add(customer); }

    public void removeCustomer(Customer customerToRemove) { customers.remove(customerToRemove); }

    public void removeCustomers(List<Customer> customersToRemove) { this.customers.removeAll(customersToRemove); }

    public void addVehicle(Vehicle vehicle) { vehicles.add(new Vehicle(vehicle, this)); }

    public void removeVehicle(Vehicle vehicle) { vehicles.remove(vehicle); }

}
