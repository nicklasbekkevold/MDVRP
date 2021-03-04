package main.java.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Depot extends Node {

    private static int serialNumber = 1;

    private final int depotNumber;
    private List<Vehicle> vehicles = new CopyOnWriteArrayList<>();
    private List<Customer> customers = new CopyOnWriteArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
        this.depotNumber = serialNumber++;
    }

    public Depot(Depot depot) {
        super(depot);
        this.depotNumber = depot.depotNumber;
        this.customers = new CopyOnWriteArrayList<>(depot.customers); // Shallow copy
        this.vehicles = new CopyOnWriteArrayList<>(); // Reset vehicles
    }

    public static void resetSerialNumber() { Depot.serialNumber = 1; }

    public int getDepotNumber() { return depotNumber; }

    public List<Vehicle> getVehicles() { return vehicles; }

    public List<Customer> getCustomers() { return customers; }

    public boolean isEmpty() { return vehicles.isEmpty(); }

    public void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    public void addCustomer(Customer customer) { customers.add(customer); }

    public void removeVehicle(Vehicle vehicle) { vehicles.remove(vehicle); }

}
