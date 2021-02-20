package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public final class Customer extends Node {

    private final int serviceDuration; //d
    private final int demand; //q

    private List<Depot> candidateDepots;

    public Customer(int id, int x, int y, int serviceDuration, int demand) {
        super(id, x, y);
        this.serviceDuration = serviceDuration;
        this.demand = demand;
        this.candidateDepots = new ArrayList<>();
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public int getDemand() { return demand; }

    public void addCandidateDepot(Depot depot) { this.candidateDepots.add(depot); }
}
