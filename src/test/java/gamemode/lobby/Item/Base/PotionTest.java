package gamemode.lobby.Item.Base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Potion} class.
 *
 * <p>
 * This test suite verifies:
 * <ul>
 *     <li>Correct object creation</li>
 *     <li>Proper buy and sell price assignment</li>
 *     <li>Validation of negative price inputs</li>
 * </ul>
 *
 * Since Potion may rely on JavaFX Image loading through Item,
 * JavaFX runtime is initialized once before tests run.
 */
class PotionTest {

    /**
     * Initializes JavaFX runtime once for testing.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Concrete test subclass used only for testing,
     * since Potion may be abstract or extended in production.
     */
    static class TestPotion extends Potion {
        public TestPotion(String name, String imgUrl, int buyPrice, int sellPrice) {
            super(name, imgUrl, buyPrice, sellPrice);
        }
    }

    /**
     * Tests that a Potion is correctly created
     * with proper name and price values.
     */
    @Test
    void testPotionCreation() {
        Potion potion = new TestPotion("Health Potion", "/item/test.png", 50, 25);

        assertEquals("Health Potion", potion.getName());
        assertEquals(50, potion.getBuyPrice());
        assertEquals(25, potion.getSellPrice());
    }

    /**
     * Tests that a negative buy price
     * throws IllegalArgumentException.
     */
    @Test
    void testNegativeBuyPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestPotion("Potion", "/item/test.png", -1, 10)
        );
    }

    /**
     * Tests that a negative sell price
     * throws IllegalArgumentException.
     */
    @Test
    void testNegativeSellPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestPotion("Potion", "/item/test.png", 10, -5)
        );
    }
}