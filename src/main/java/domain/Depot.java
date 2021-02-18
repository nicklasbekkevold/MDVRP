package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Depot extends Node {

    private List<Customer> customers = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    public Depot(int id, int x, int y) {
        super(id, x, y);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}
