package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link HerbivoreDinosaur} class.
 *
 * <p>
 * These tests validate the basic behavior of herbivore dinosaurs,
 * including initialization, movement updates, and inherited
 * combat/stat functionality from {@link Dinosaur}.
 * </p>
 *
 * <p>
 * Random movement is controlled in tests by setting the internal
 * direction fields using reflection to ensure deterministic results.
 * </p>
 */
public class HerbivoreDinosaurTest {

    /**
     * Tests that the constructor correctly initializes all properties.
     */
    @Test
    void testConstructorInitialization() {

        HerbivoreDinosaur dino = new HerbivoreDinosaur(
                "TestHerb",
                100,
                10,
                20,
                1,
                50,
                60,
                Dinosaur.Rarity.COMMON,
                "/images/test.png",
                100,
                100,
                25
        );

        assertEquals("TestHerb", dino.getName());
        assertEquals(100, dino.getHp());
        assertEquals(50, dino.getX());
        assertEquals(60, dino.getY());
        assertEquals(Dinosaur.Rarity.COMMON, dino.getRarity());
    }

    /**
     * Tests that the dinosaur moves when update() is called
     * and a direction is manually set.
     */
    @Test
    void testMovementUpdate() throws Exception {

        HerbivoreDinosaur dino = new HerbivoreDinosaur(
                "Mover",
                100,
                10,
                20,
                1,
                0,
                0,
                Dinosaur.Rarity.COMMON,
                "/images/test.png",
                100,
                100,
                25
        );

        // set direction manually
        Field dx = HerbivoreDinosaur.class.getDeclaredField("directionX");
        Field dy = HerbivoreDinosaur.class.getDeclaredField("directionY");

        dx.setAccessible(true);
        dy.setAccessible(true);

        dx.set(dino, 1.0);
        dy.set(dino, 0.0);

        double beforeX = dino.getX();

        dino.update(null);

        assertTrue(dino.getX() > beforeX);
    }

    /**
     * Tests inherited damage functionality from {@link Dinosaur}.
     */
    @Test
    void testTakeDamage() {

        HerbivoreDinosaur dino = new HerbivoreDinosaur(
                "Tank",
                100,
                5,
                10,
                1,
                0,
                0,
                Dinosaur.Rarity.COMMON,
                "/images/test.png",
                100,
                100,
                20
        );

        dino.takeDamage(30);

        assertEquals(70, dino.getHp());
    }

    /**
     * Tests that HP cannot go below zero.
     */
    @Test
    void testHpClamp() {

        HerbivoreDinosaur dino = new HerbivoreDinosaur(
                "Clamp",
                50,
                5,
                10,
                1,
                0,
                0,
                Dinosaur.Rarity.COMMON,
                "/images/test.png",
                100,
                100,
                20
        );

        dino.takeDamage(100);

        assertEquals(0, dino.getHp());
    }

    /**
     * Ensures update() can be called safely even if the player is null.
     */
    @Test
    void testUpdateDoesNotDependOnPlayer() {

        HerbivoreDinosaur dino = new HerbivoreDinosaur(
                "Passive",
                80,
                5,
                10,
                1,
                10,
                10,
                Dinosaur.Rarity.COMMON,
                "/images/test.png",
                100,
                100,
                20
        );

        assertDoesNotThrow(() -> dino.update(null));
    }
}