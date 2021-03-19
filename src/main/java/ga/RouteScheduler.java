package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;
import main.java.utils.Util;

import java.util.List;
import java.util.Random;

public class RouteScheduler {

    public static final double FEASIBILITY_THRESHOLD = 0.8;
    public static final Random random = Util.random;

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

    public static void schedule(final Chromosome chromosome) {
        phaseTwo(phaseOne(chromosome));
    }

    private static Chromosome phaseOne(Chromosome chromosome) {
        for (Depot depot : chromosome) {
            reschedule(depot);
        }
        return chromosome;
    }

    private static void phaseTwo(Chromosome chromosome) {
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
    }

    public static void reschedule(Depot depot) {
        depot.getVehicles().clear();
        reschedule(depot, depot.getCustomers());

    }

    private static void reschedule(Depot depot, List<Customer> customers) {
        Vehicle vehicle = new Vehicle(depot);

        Node previousNode = depot;
        double duration = 0;
        int load = 0;

        for (Customer customer : customers) {
            double proposedDuration = duration + previousNode.distance(customer);
            int proposedLoad = load + customer.getDemand();

            if (proposedDuration + customer.distance(depot) <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
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

    public static void insertCustomerWithBestRouteCost(Depot depot, Customer customer) {
        insertCustomerWithBestRouteCost(depot, customer, FEASIBILITY_THRESHOLD);
    }

    public static void insertCustomerWithBestRouteCost(Depot depot, Customer customer, double feasibilityThreshold) {
        if (!depot.getCustomers().contains(customer)) {
            depot.addCustomer(customer);
        }
        List<Vehicle> vehicles = depot.getVehicles();

        int feasibleRouteIndex = -1;
        int feasibleRouteInsertionIndex = -1;
        double minFeasibleInsertionCost = Double.MAX_VALUE;

        int infeasibleRouteIndex = -1;
        int infeasibleRouteInsertionIndex = -1;
        double minInfeasibleInsertionCost = Double.MAX_VALUE;

        for (int routeIndex = 0; routeIndex < vehicles.size(); routeIndex++) {
            Vehicle currentVehicle = vehicles.get(routeIndex);

            for (int insertionIndex = 0; insertionIndex <= currentVehicle.getCustomers().size(); insertionIndex++) {
                double insertionCost = currentVehicle.getInsertionCost(insertionIndex, customer);
                int proposedLoad = currentVehicle.getLoad() + customer.getDemand();

                if (currentVehicle.getDuration() + insertionCost <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
                    if (insertionCost < minFeasibleInsertionCost) {
                        feasibleRouteIndex = routeIndex;
                        feasibleRouteInsertionIndex = insertionIndex;
                        minFeasibleInsertionCost = insertionCost;
                    }
                } else {
                    if (insertionCost < minInfeasibleInsertionCost) {
                        infeasibleRouteIndex = routeIndex;
                        infeasibleRouteInsertionIndex = insertionIndex;
                        minInfeasibleInsertionCost = insertionCost;
                    }
                }
            }
        }

        if (random.nextDouble() <= feasibilityThreshold) {
            insertCustomerOrCreateNewRoute(feasibleRouteIndex, feasibleRouteInsertionIndex, customer, depot);
        } else {
            if (minFeasibleInsertionCost < minInfeasibleInsertionCost) {
                insertCustomerOrCreateNewRoute(feasibleRouteIndex, feasibleRouteInsertionIndex, customer, depot);
            } else {
                createNewRouteOrSplit(infeasibleRouteIndex, infeasibleRouteInsertionIndex, customer, depot);
            }
        }
    }

    private static void insertCustomerOrCreateNewRoute(int routeIndex, int insertionIndex, Customer customer, Depot depot) {
        if (routeIndex == -1) {
            Vehicle newVehicle = new Vehicle(depot);
            newVehicle.addCustomer(customer);
            depot.getVehicles().add(newVehicle);
        } else {
            depot.getVehicles().get(routeIndex).insertCustomer(insertionIndex, customer);
        }
    }

    private static void createNewRouteOrSplit(int routeIndex, int insertionIndex, Customer customer, Depot depot) {
        if (routeIndex == -1) {
            Vehicle newVehicle = new Vehicle(depot);
            newVehicle.addCustomer(customer);
            depot.getVehicles().add(newVehicle);
        } else {
            depot.getVehicles().get(routeIndex).insertCustomer(insertionIndex, customer);
            Vehicle vehicleToSplit = depot.getVehicles().get(routeIndex);
            depot.removeVehicle(vehicleToSplit);
            reschedule(depot, vehicleToSplit.getCustomers());
        }
    }

}
