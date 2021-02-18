package main.java.ga;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteScheduler {

    public static final int numberOfVehiclesPerDepot = 4;
    public static final int maxRouteDuration = Integer.MAX_VALUE;
    public static final int maxVehicleLoad = 80;

    private static Map<Depot, List<Route>> phaseOne(Map<Depot, List<Customer>> chromosome) {
        Map<Depot, List<Route>> routes = new HashMap<>();


        for (Depot depot : chromosome.keySet()) {
            routes.put(depot, new ArrayList<>());
            Route route = new Route();
            route.setDepot(depot);

            Node previousNode = depot;
            float duration = 0;
            int load = 0;

            for (Customer customer : chromosome.get(depot)) {
                float proposedDuration = duration + customer.getServiceDuration() + previousNode.distance(customer);
                int proposedLoad = load + customer.getDemand();

                if (proposedDuration + previousNode.distance(depot) <= maxRouteDuration && proposedLoad <= maxVehicleLoad) {
                    route.addCustomer(customer);
                    previousNode = customer;
                    duration = proposedDuration;
                    load = proposedLoad;
                } else {
                    route.setDuration(duration + previousNode.distance(depot));
                    route.setLoad(load);
                    routes.get(depot).add(route);

                    route = new Route();
                    route.setDepot(depot);
                    previousNode = depot;
                    duration = 0;
                    load = 0;
                }
            }
        }

        return routes;
    }

    private static Map<Depot, List<Route>> phaseTwo(final Map<Depot, List<Route>> routes) {
        Map<Depot, List<Route>> optimizedRoutes = new HashMap<>();

        for (Depot depot : routes.keySet()) {
            Route route = new Route(routes.get(depot).get(0));

            optimizedRoutes.put(depot, new ArrayList<>());
            optimizedRoutes.get(depot).add(route);

            for (int i = 1; i < routes.get(depot).size(); i ++) {
                Route nextRoute = new Route(routes.get(depot).get(i));

                Customer lastCustomer = route.getLastCustomer();
                Depot nextRouteDepot = nextRoute.getDepot();
                nextRoute.addCustomer(lastCustomer, nextRouteDepot.distance(lastCustomer), lastCustomer.getDemand());

                if (nextRoute.getDuration() <= maxRouteDuration && nextRoute.getLoad() <= maxVehicleLoad) {
                    optimizedRoutes.get(depot).add(nextRoute);
                    route = nextRoute;
                } else {
                    optimizedRoutes.get(depot).add(routes.get(depot).get(i + 1));
                }
            }
        }
        return optimizedRoutes;
    }

    public static Map<Depot, List<Route>> schedule(final Map<Depot, List<Customer>> chromosome) {
        return phaseTwo(phaseOne(chromosome));
    }
}
