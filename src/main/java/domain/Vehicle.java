package main.java.domain;

import java.util.List;

public class Vehicle extends Node {

    private final Depot startDepot;
    private final Depot endDepot;
    private final List<Customer> customers;

    private Double duration = null; //d
    private Integer load = null; //q

    public Vehicle(int id, int x, int y) {
        super(id, x, y);
        startDepot = null;
        endDepot = null;
        customers = null;
    }

    public Vehicle(int id, Depot depot) {
        super(id, depot.getX(), depot.getY());
        this.startDepot = depot;
        endDepot = startDepot;
        customers = null;
    }

    public double getDuration() {
        if (duration != null) {
           return duration;
        }

        double totalDuration = startDepot.distance(customers.get(0));
        for (int i = 0; i < customers.size() - 1; i++) {
            totalDuration += customers.get(i).distance(customers.get(i + 1));
        }
        totalDuration += customers.get(-1).distance(endDepot);

        totalDuration += customers
                .stream()
                .reduce(0, (total, customer) -> total + customer.getServiceDuration(), Integer::sum);

        duration = totalDuration;
        return duration;
    }

    public double getLoad() {
        if (load != null) {
           return load;
        }

        int totalLoad = customers
                .stream()
                .reduce(0, (total, customer) -> total + customer.getDemand(), Integer::sum);

        load = totalLoad;
        return load;
    }
}
