package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Node {

    private final int demand; //q

    private List<Depot> candidateDepots;

    public Customer(int id, int x, int y, int demand) {
        super(id, x, y);
        this.demand = demand;
        this.candidateDepots = new ArrayList<>();
    }

    public int getDemand() { return demand; }

    public void addCandidateDepot(Depot depot) { this.candidateDepots.add(depot); }
}
