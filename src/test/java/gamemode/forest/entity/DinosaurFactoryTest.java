package gamemode.forest.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DinosaurFactory} class.
 *
 * <p>
 * This test suite verifies that the factory correctly creates
 * different dinosaur types with the expected attributes,
 * including name, rarity, and spawn position.
 * </p>
 *
 * <p>
 * The tests do not use any mocking frameworks and instead rely
 * solely on verifying returned object state.
 * </p>
 */
public class DinosaurFactoryTest {

    /**
     * Tests that the factory correctly creates a LongNeck herbivore dinosaur.
     */
    @Test
    void testCreateLongNeckHerbivore() {

        Dinosaur dino = DinosaurFactory.createHerbivore("LongNeck", 100, 200);

        assertNotNull(dino);
        assertTrue(dino instanceof HerbivoreDinosaur);

        assertEquals("Long Neck", dino.getName());
        assertEquals(100, dino.getX());
        assertEquals(200, dino.getY());
        assertEquals(Dinosaur.Rarity.COMMON, dino.getRarity());
    }

    /**
     * Tests that the factory correctly creates a Triceratops herbivore dinosaur.
     */
    @Test
    void testCreateTriceratopsHerbivore() {

        Dinosaur dino = DinosaurFactory.createHerbivore("Triceratops", 50, 75);

        assertNotNull(dino);
        assertTrue(dino instanceof HerbivoreDinosaur);

        assertEquals("Triceratops", dino.getName());
        assertEquals(50, dino.getX());
        assertEquals(75, dino.getY());
        assertEquals(Dinosaur.Rarity.UNCOMMON, dino.getRarity());
    }

    /**
     * Tests that the factory returns null when an unknown herbivore type is requested.
     */
    @Test
    void testInvalidHerbivoreTypeReturnsNull() {

        Dinosaur dino = DinosaurFactory.createHerbivore("Unknown", 0, 0);

        assertNull(dino);
    }

    /**
     * Tests that the factory correctly creates a Raptor carnivore dinosaur.
     */
    @Test
    void testCreateRaptorCarnivore() {

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 300, 400);

        assertNotNull(dino);
        assertTrue(dino instanceof CarnivoreDinosaur);

        assertEquals("Raptor", dino.getName());
        assertEquals(300, dino.getX());
        assertEquals(400, dino.getY());
        assertEquals(Dinosaur.Rarity.COMMON, dino.getRarity());
    }

    /**
     * Tests that the factory correctly creates a TRex carnivore dinosaur.
     */
    @Test
    void testCreateTRexCarnivore() {

        Dinosaur dino = DinosaurFactory.createCarnivore("TRex", 10, 20);

        assertNotNull(dino);
        assertTrue(dino instanceof CarnivoreDinosaur);

        assertEquals("T-Rex", dino.getName());
        assertEquals(10, dino.getX());
        assertEquals(20, dino.getY());
        assertEquals(Dinosaur.Rarity.RARE, dino.getRarity());
    }

    /**
     * Tests that the factory returns null when an unknown carnivore type is requested.
     */
    @Test
    void testInvalidCarnivoreTypeReturnsNull() {

        Dinosaur dino = DinosaurFactory.createCarnivore("Alien", 0, 0);

        assertNull(dino);
    }

    /**
     * Tests that the factory correctly creates the Mega dinosaur.
     */
    @Test
    void testCreateMegaDinosaur() {

        Dinosaur dino = DinosaurFactory.createMega(500, 600);

        assertNotNull(dino);
        assertTrue(dino instanceof MegaDinosaur);

        assertEquals("Ancient Colossus", dino.getName());
        assertEquals(500, dino.getX());
        assertEquals(600, dino.getY());
        assertEquals(Dinosaur.Rarity.RARE, dino.getRarity());
    }
}