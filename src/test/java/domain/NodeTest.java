package test.java.domain;


import org.junit.jupiter.api.Test;


import main.java.domain.Customer;
import main.java.domain.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {

    @Test
    public void testDistance() {
        Node c1 = new Customer(1, 0, 0, 0);
        Node c2 = new Customer(2, 3, 4, 0);
        Node c3 = new Customer(3, -3, -4, 0);

        assertEquals(5.0, c1.distance(c2));
        assertEquals(5.0, c1.distance(c3));
        assertEquals(10.0, c2.distance(c3));
        assertEquals(0.0, c1.distance(c1));
        assertEquals(0.0, c2.distance(c2));
        assertEquals(0.0, c3.distance(c3));
    }

}
