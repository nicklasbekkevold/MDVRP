package main.java.domain;

import main.java.ga.Gene;

public class Customer extends Node implements Gene {

    private final int serviceDuration; //d
    private final int demand; //q

    public Customer(int id, int x, int y, int serviceDuration, int demand) {
        super(id, x, y);
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public int getDemand() {
        return demand;
    }
}
