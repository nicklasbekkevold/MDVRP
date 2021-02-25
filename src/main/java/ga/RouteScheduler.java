package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;

import java.util.List;

public class RouteScheduler {

    public static int numberOfVehiclesPerDepot;
    public static int maxRouteDuration;
    public static int maxVehicleLoad;

    public static void setNumberOfVehiclesPerDepot(int numberOfVehiclesPerDepot) {
        RouteScheduler.numberOfVehiclesPerDepot = numberOfVehiclesPerDepot;
    }

    public static void setMaxRouteDuration(int maxRouteDuration) {
        if (maxRouteDuration == 0) {
            RouteScheduler.maxRouteDuration = Integer.MAX_VALUE;
        } else {
            RouteScheduler.maxRouteDuration = maxRouteDuration;
        }
    }

    public static void setMaxVehicleLoad(int maxVehicleLoad) {
        RouteScheduler.maxVehicleLoad = maxVehicleLoad;
    }

    private static Chromosome phaseOne(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            Vehicle.resetSerialNumber();
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
                        depot.addVehicle(vehicle); // Add vehicle if there it has at least one customer
                    }

                    vehicle = new Vehicle(depot);
                    vehicle.addCustomer(customer);
                    previousNode = depot;
                    duration = customer.getServiceDuration() + previousNode.distance(customer);
                    load = customer.getDemand();
                }
            }
            depot.addVehicle(vehicle); // Add last route unconditionally
        }

        return chromosome;
    }

    private static Chromosome phaseTwo(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            List<Vehicle> vehicles = depot.getVehicles();
            if (vehicles.size() == 0) {
                continue;
            }
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
