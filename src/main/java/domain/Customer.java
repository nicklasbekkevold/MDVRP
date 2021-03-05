package main.java.domain;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Node {

    private final int demand; //q
    private final List<Integer> candidateDepots;

    public Customer(int id, int x, int y, int demand) {
        super(id, x, y);
        this.demand = demand;
        this.candidateDepots = new ArrayList<>();
    }

    public int getDemand() { return demand; }

    public List<Integer> getCandidateDepotIds() { return candidateDepots; }

    public void addCandidateDepotId(Integer depotId) { this.candidateDepots.add(depotId); }
}
