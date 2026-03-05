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
 * <p>
 * Since {@link Potion} relies on JavaFX image loading through {@link Item},
 * the JavaFX runtime is initialised once before all tests via
 * {@link testutil.JavaFXInitializer}.
 * </p>
 *
 * @see Potion
 * @see Item
 */
class PotionTest {

    /**
     * Initializes JavaFX runtime once for all tests.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Minimal concrete subclass of {@link Potion} used solely for testing.
     * Delegates all construction to the superclass and passes a fixed
     * placeholder description of {@code "Test Potion"}.
     */
    static class TestPotion extends Potion {

        /**
         * Constructs a {@code TestPotion} with the given attributes and
         * a fixed description of {@code "Test Potion"}.
         *
         * @param name      the display name of the potion
         * @param imgUrl    the resource path to the potion image
         * @param buyPrice  the purchase price; must be &gt;= 0
         * @param sellPrice the selling price; must be &gt;= 0
         */
        public TestPotion(String name, String imgUrl, int buyPrice, int sellPrice) {
            super(name, imgUrl, buyPrice, sellPrice, "Test Potion");
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
     * throws {@link IllegalArgumentException}.
     */
    @Test
    void testNegativeBuyPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestPotion("Potion", "/item/test.png", -1, 10)
        );
    }

    /**
     * Tests that a negative sell price
     * throws {@link IllegalArgumentException}.
     */
    @Test
    void testNegativeSellPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestPotion("Potion", "/item/test.png", 10, -5)
        );
    }
}