package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteScheduler {

    public static final int numberOfVehiclesPerDepot = 4;
    public static final int maxRouteDuration = Integer.MAX_VALUE;
    public static final int maxVehicleLoad = 80;

    private static List<List<Vehicle>> phaseOne(Chromosome chromosome) {
        List<List<Vehicle>> routes = new ArrayList<>();

        int depotIndex = -1;
        for (Depot depot : chromosome) {
            routes.add(new ArrayList<>());
            Vehicle vehicle = new Vehicle();
            vehicle.setDepot(depot);

            Node previousNode = depot;
            float duration = 0;
            int load = 0;

            for (Customer customer : depot.getCustomers()) {
                float proposedDuration = duration + customer.getServiceDuration() + previousNode.distance(customer);
                int proposedLoad = load + customer.getDemand();

                if (proposedDuration + previousNode.distance(depot) <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
                    vehicle.addCustomer(customer);
                    previousNode = customer;
                    duration = proposedDuration;
                    load = proposedLoad;
                } else {
                    vehicle.setDuration(duration + previousNode.distance(depot));
                    vehicle.setLoad(load);
                    routes.get(depotIndex++).add(vehicle);

                    vehicle = new Vehicle();
                    vehicle.setDepot(depot);
                    previousNode = depot;
                    duration = 0;
                    load = 0;
                }
            }
        }

        return routes;
    }

    private static List<List<Vehicle>> phaseTwo(final List<List<Vehicle>> routes) {
        int depotIndex = 0;
        for (List<Vehicle> depotRoute : routes) {
            Vehicle vehicle = new Vehicle(depotRoute.get(0));

            for (int i = 1; i < depotRoute.size(); i ++) {
                Vehicle nextVehicle = new Vehicle(depotRoute.get(i));

                Customer lastCustomer = vehicle.getLastCustomer();
                Depot nextRouteDepot = nextVehicle.getDepot();
                nextVehicle.addCustomer(lastCustomer, nextRouteDepot.distance(lastCustomer), lastCustomer.getDemand());

                if (nextVehicle.getDuration() <= maxRouteDuration && nextVehicle.getLoad() <= maxVehicleLoad) {
                    routes.get(depotIndex++).set(i, nextVehicle);
                    vehicle = nextVehicle;
                }
            }
        }
        return routes;
    }

    public static List<List<Vehicle>> schedule(final Chromosome chromosome) {
        return phaseTwo(phaseOne(chromosome));
    }
}
