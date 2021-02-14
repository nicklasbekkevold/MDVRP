package main.java.domain;

import java.util.List;

public class Depot extends Node {

    private List<Customer> customers;
    private List<Vehicle> vehicles;

    public Depot(int id, int x, int y) {
        super(id, x, y);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}
