package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;

import java.util.List;

public class RouteScheduler {

    public static final int numberOfVehiclesPerDepot = 4;
    public static final int maxRouteDuration = Integer.MAX_VALUE;
    public static final int maxVehicleLoad = 80;

    private static Chromosome phaseOne(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            Vehicle vehicle = new Vehicle(depot);

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
                    if (previousNode != depot) {
                        depot.addVehicle(vehicle); // Add vehicle if there is at least one customer
                    }

                    vehicle = new Vehicle(depot);
                    previousNode = depot;
                    duration = 0;
                    load = 0;
                }
            }
        }

        return chromosome;
    }

    private static Chromosome phaseTwo(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            List<Vehicle> vehicles = depot.getVehicles();
            Vehicle previousVehicle = new Vehicle(vehicles.get(0));

            for (int i = 1; i < vehicles.size(); i++) {
                Vehicle vehicle = new Vehicle(vehicles.get(i));
                float combinedDuration = previousVehicle.getDuration() + vehicle.getDuration();

                Customer lastCustomer = vehicle.popLastCustomer();
                vehicle.addCustomer(lastCustomer);
                float proposedCombinedDuration = previousVehicle.getDuration() + vehicle.getDuration();

                if (vehicle.getDuration() <= maxRouteDuration && vehicle.getLoad() <= maxVehicleLoad && combinedDuration < proposedCombinedDuration) {
                    vehicles.set(i, vehicle);
                }
                previousVehicle = vehicle;
            }
        }
        return chromosome;
    }

    public static Chromosome schedule(final Chromosome chromosome) {
        return phaseTwo(phaseOne(chromosome));
    }
}
