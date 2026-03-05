package gamemode.forest.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WorldPoint.
 */
public class WorldPointTest {

    /**
     * Test constructor and getters.
     */
    @Test
    void testConstructorAndGetters() {

        WorldPoint point = new WorldPoint(5, 10);

        assertEquals(5, point.getX());
        assertEquals(10, point.getY());
    }

    /**
     * Test negative coordinate values.
     */
    @Test
    void testNegativeCoordinates() {

        WorldPoint point = new WorldPoint(-100, -50);

        assertEquals(-100, point.getX());
        assertEquals(-50, point.getY());
    }

    /**
     * Test equality for identical coordinates.
     */
    @Test
    void testEqualsSameCoordinates() {

        WorldPoint p1 = new WorldPoint(3, 7);
        WorldPoint p2 = new WorldPoint(3, 7);

        assertEquals(p1, p2);
    }

    /**
     * Test equality with itself.
     */
    @Test
    void testEqualsSameObject() {

        WorldPoint p1 = new WorldPoint(2, 2);

        assertEquals(p1, p1);
    }

    /**
     * Test inequality when X differs.
     */
    @Test
    void testNotEqualsDifferentX() {

        WorldPoint p1 = new WorldPoint(1, 2);
        WorldPoint p2 = new WorldPoint(3, 2);

        assertNotEquals(p1, p2);
    }

    /**
     * Test inequality when Y differs.
     */
    @Test
    void testNotEqualsDifferentY() {

        WorldPoint p1 = new WorldPoint(1, 2);
        WorldPoint p2 = new WorldPoint(1, 5);

        assertNotEquals(p1, p2);
    }

    /**
     * Test equals with null.
     */
    @Test
    void testEqualsNull() {

        WorldPoint p1 = new WorldPoint(1, 2);

        assertNotEquals(p1, null);
    }

    /**
     * Test equals with different object type.
     */
    @Test
    void testEqualsDifferentType() {

        WorldPoint p1 = new WorldPoint(1, 2);

        assertNotEquals(p1, "not a point");
    }

    /**
     * Test hashCode consistency with equals.
     */
    @Test
    void testHashCodeConsistency() {

        WorldPoint p1 = new WorldPoint(4, 8);
        WorldPoint p2 = new WorldPoint(4, 8);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

}