package test.java.domain;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;
import main.java.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class VehicleTest {

    private static Depot depot;
    private static Customer c1;
    private static Customer c2;
    private static Customer c3;
    private static Vehicle vehicle;

    @BeforeEach
    public void setUp(){
        depot = new Depot(0, 0, 0);
        c1 = new Customer(1, 3, 4, 1);
        c2 = new Customer(2, -3, -4, 2);
        c3 = new Customer(3, -3, 4, 3);
        vehicle = new Vehicle(depot);
    }

    @Test
    public void testGetDuration() {
        assertEquals(0.0, vehicle.getDuration());
        vehicle.addCustomer(c1);
        assertEquals(10.0, vehicle.getDuration());
        vehicle.addCustomer(c2);
        assertEquals(20.0, vehicle.getDuration());
    }

    @Test
    public void testInsertCustomer() {
        vehicle.insertCustomer(0, c1);
        vehicle.insertCustomer(0, c2);
        assertEquals(c2, vehicle.getCustomers().get(0));
        assertEquals(c1, vehicle.getCustomers().get(1));
        assertEquals(3, vehicle.getLoad());
        assertEquals(20.0, vehicle.getDuration());
    }

    @Test
    public void testAddCustomer() {
        vehicle.addCustomer(c1);
        vehicle.addCustomer(c2);
        assertEquals(c1, vehicle.getCustomers().get(0));
        assertEquals(c2, vehicle.getCustomers().get(1));
        assertEquals(3, vehicle.getLoad());
        assertEquals(20.0, vehicle.getDuration());
    }

    @Test
    public void testGetInsertionCost() {
        assertEquals(10.0, vehicle.getInsertionCost(0, c1));
        vehicle.addCustomer(c1);
        assertEquals(10.0, vehicle.getInsertionCost(0, c2));
        assertEquals(10.0, vehicle.getInsertionCost(1, c2));
        vehicle.addCustomer(c2);
        assertEquals(4.0, vehicle.getInsertionCost(1, c3));
    }

    @Test
    public void testSplit() {
        vehicle.addCustomer(c1);
        vehicle.addCustomer(c2);
        vehicle.addCustomer(c3);
        Pair<Vehicle> split = vehicle.split(1);
        assertEquals(1, split.first.getCustomers().size());
        assertEquals(c1, split.first.getCustomers().get(0));
        assertFalse(split.first.getCustomers().contains(c2));
        assertFalse(split.first.getCustomers().contains(c3));

        assertEquals(2, split.second.getCustomers().size());
        assertEquals(c2, split.second.getCustomers().get(0));
        assertEquals(c3, split.second.getCustomers().get(1));
        assertFalse(split.second.getCustomers().contains(c1));

        assertFalse(depot.getVehicles().contains(vehicle));
    }
}
