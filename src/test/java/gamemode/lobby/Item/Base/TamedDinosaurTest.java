package gamemode.lobby.Item.Base;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TamedDinosaur} class.
 *
 * <p>
 * This test suite verifies:
 * <ul>
 *     <li>Correct creation of a TamedDinosaur</li>
 *     <li>Sell price is copied from the original Dinosaur</li>
 *     <li>Null validation in constructor</li>
 * </ul>
 *
 * Since Dinosaur loads JavaFX Image internally,
 * JavaFX runtime must be initialized before testing.
 */
class TamedDinosaurTest {

    /**
     * Initializes JavaFX runtime once for testing.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Minimal concrete Dinosaur implementation for testing.
     * Only required constructor values are provided.
     */
    static class TestDinosaur extends Dinosaur {

        public TestDinosaur(String name, int sellPrice) {
            super(
                    name,
                    100,              // hp
                    20,               // strength
                    10,               // expDrop
                    1,                // requiredLevel
                    0, 0,             // x, y
                    Rarity.COMMON,
                    "/item/test.png", // image path (safe even if missing)
                    50, 50,           // width, height
                    1.0,              // speed
                    sellPrice
            );
        }

        @Override
        public void update(Player player) {
            // No behavior needed for unit test
        }
    }

    /**
     * Tests that a TamedDinosaur is correctly created
     * from an existing Dinosaur.
     */
    @Test
    void testTamedDinosaurCreation() {
        Dinosaur dino = new TestDinosaur("Test", 300);
        TamedDinosaur tamed = new TamedDinosaur(dino);

        assertEquals("Tamed Test", tamed.getName());
        assertEquals(300, tamed.getSellPrice());
        assertEquals(dino, tamed.getDinosaur());

        // Avoid strict image assertion (depends on resources)
        assertDoesNotThrow(tamed::getImg);
    }

    /**
     * Tests that the sell price of the TamedDinosaur
     * is copied from the original Dinosaur.
     */
    @Test
    void testSellPriceCopiedFromDinosaur() {
        Dinosaur dino = new TestDinosaur("Raptor", 500);
        TamedDinosaur tamed = new TamedDinosaur(dino);

        assertEquals(dino.getSellPrice(), tamed.getSellPrice());
    }

    /**
     * Tests that passing null to the constructor
     * throws IllegalArgumentException.
     */
    @Test
    void testNullDinosaurThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TamedDinosaur(null)
        );
    }
}