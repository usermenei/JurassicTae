package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Dinosaur abstract class.
 * Uses a simple concrete subclass for testing purposes.
 */
public class DinosaurTest {

    /**
     * Simple concrete dinosaur used only for testing the abstract class.
     */
    static class TestDinosaur extends Dinosaur {

        public TestDinosaur() {
            super(
                    "TestDino",
                    100,
                    20,
                    50,
                    1,
                    0,
                    0,
                    Rarity.COMMON,
                    "/images/dinosaur/raptor.gif",
                    100,
                    100,
                    2,
                    30
            );
        }

        @Override
        public void update(Player player) {
            // No AI behavior needed for testing base class
        }
    }

    @Test
    void testConstructorInitializesValues() {
        TestDinosaur d = new TestDinosaur();

        assertEquals("TestDino", d.getName());
        assertEquals(100, d.getHp());
        assertEquals(100, d.getMaxHp());
        assertEquals(20, d.getStrength());
        assertEquals(50, d.getExpDrop());
        assertEquals(1, d.getRequiredLevel());
        assertEquals(0, d.getX());
        assertEquals(0, d.getY());
        assertEquals(Dinosaur.Rarity.COMMON, d.getRarity());
    }

    @Test
    void testSetNameNullDefaults() {
        TestDinosaur d = new TestDinosaur();

        d.setName(null);

        assertEquals("Dinosaur", d.getName());
    }

    @Test
    void testSetAndGetPosition() {
        TestDinosaur d = new TestDinosaur();

        d.setX(150);
        d.setY(300);

        assertEquals(150, d.getX());
        assertEquals(300, d.getY());
    }

    @Test
    void testHpClampedToZero() {
        TestDinosaur d = new TestDinosaur();

        d.setHp(-50);

        assertEquals(0, d.getHp());
    }

    @Test
    void testTakeDamageReducesHp() {
        TestDinosaur d = new TestDinosaur();

        d.takeDamage(30);

        assertEquals(70, d.getHp());
    }

    @Test
    void testTakeDamageCannotGoNegative() {
        TestDinosaur d = new TestDinosaur();

        d.takeDamage(200);

        assertEquals(0, d.getHp());
    }

    @Test
    void testStrengthCannotBeNegative() {
        TestDinosaur d = new TestDinosaur();

        d.setStrength(-10);

        assertEquals(0, d.getStrength());
    }

    @Test
    void testExpDropCannotBeNegative() {
        TestDinosaur d = new TestDinosaur();

        d.setExpDrop(-100);

        assertEquals(0, d.getExpDrop());
    }

    @Test
    void testRequiredLevelMinimumOne() {
        TestDinosaur d = new TestDinosaur();

        d.setRequiredLevel(0);

        assertEquals(1, d.getRequiredLevel());
    }

    @Test
    void testChunkCoordinates() {
        TestDinosaur d = new TestDinosaur();

        d.setChunk(3, 5);

        assertEquals(3, d.getChunkX());
        assertEquals(5, d.getChunkY());
    }

    @Test
    void testMoveTowardChangesPosition() {
        TestDinosaur d = new TestDinosaur();

        double beforeX = d.getX();

        d.moveToward(100, 0);

        double afterX = d.getX();

        assertTrue(afterX > beforeX);
    }

    @Test
    void testWidthSetter() {
        TestDinosaur d = new TestDinosaur();

        d.setWidth(250);

        assertEquals(250, d.getWidth());
    }

}