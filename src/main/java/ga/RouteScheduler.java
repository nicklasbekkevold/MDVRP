package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;

import java.util.List;
import java.util.Random;

public class RouteScheduler {

    public final static double FEASIBILITY_THRESHOLD = 0.8;
    public final static Random random = new Random();

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

    public static void insertCustomerWithBestRouteCost(Depot depot, Customer customer) {
        List<Vehicle> vehicles = depot.getVehicles();

        int feasibleRouteIndex = -1;
        int feasibleRouteInsertionIndex = -1;
        double minFeasibleInsertionCost = Double.MAX_VALUE;

        int infeasibleRouteIndex = -1;
        int infeasibleRouteInsertionIndex = -1;
        double minInfeasibleInsertionCost = Double.MAX_VALUE;

        for (int routeIndex = 0; routeIndex < vehicles.size(); routeIndex++) {
            Vehicle currentVehicle = vehicles.get(routeIndex);

            for (int index = 0; index <= currentVehicle.getCustomers().size(); index++) {
                double insertionCost = currentVehicle.getInsertionCost(index, customer);
                int proposedLoad = currentVehicle.getLoad() + customer.getDemand();

                if (currentVehicle.getDuration() + insertionCost <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
                    if (insertionCost < minFeasibleInsertionCost) {
                        feasibleRouteIndex = routeIndex;
                        feasibleRouteInsertionIndex = index;
                        minFeasibleInsertionCost = insertionCost;
                    }
                } else {
                    if (insertionCost < minInfeasibleInsertionCost) {
                        infeasibleRouteIndex = routeIndex;
                        infeasibleRouteInsertionIndex = index;
                        minInfeasibleInsertionCost = insertionCost;
                    }
                }
            }
        }

        if (random.nextDouble() <= FEASIBILITY_THRESHOLD) {
            if (feasibleRouteIndex == -1) {
                Vehicle newVehicle = new Vehicle(depot);
                newVehicle.addCustomer(customer);
                vehicles.add(newVehicle);
            } else {
                vehicles.get(feasibleRouteIndex).insertCustomer(feasibleRouteInsertionIndex, customer);
            }
        } else {
            if (minFeasibleInsertionCost < minInfeasibleInsertionCost) {
                vehicles.get(feasibleRouteIndex).insertCustomer(feasibleRouteInsertionIndex, customer);
            } else {
                vehicles.get(infeasibleRouteIndex).insertCustomer(infeasibleRouteInsertionIndex, customer);
            }
        }
    }

    public static Chromosome schedule(final Chromosome chromosome) {
        return phaseTwo(phaseOne(chromosome));
    }

    private static Chromosome phaseOne(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            Vehicle.resetSerialNumber();
            Vehicle vehicle = new Vehicle(depot);

            Node previousNode = depot;
            double duration = 0;
            int load = 0;

            for (Customer customer : depot.getCustomers()) {
                double proposedDuration = duration + previousNode.distance(customer);
                int proposedLoad = load + customer.getDemand();

                if (proposedDuration + previousNode.distance(depot) <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
                    vehicle.addCustomer(customer);
                    previousNode = customer;
                    duration = proposedDuration;
                    load = proposedLoad;
                } else {
                    if (vehicle.getCustomers().size() >= 1) {
                        depot.addVehicle(vehicle); // Add vehicle if there it has at least one customer
                    }

                    vehicle = new Vehicle(depot);
                    vehicle.addCustomer(customer);
                    previousNode = customer;
                    duration = depot.distance(customer);
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
                double combinedDuration = vehicle.getDuration();

                Customer lastCustomer = vehicle.popLastCustomer();
                vehicle.addCustomer(lastCustomer);
                double proposedCombinedDuration = previousVehicle.getDuration() + vehicle.getDuration();

                if (vehicle.getDuration() <= maxRouteDuration && vehicle.getLoad() <= maxVehicleLoad && combinedDuration < proposedCombinedDuration) {
                    vehicles.set(i, vehicle);
                }
                previousVehicle = vehicle;
            }
        }
        return chromosome;
    }

}
