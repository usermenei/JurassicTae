package gamemode.lobby.Item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DinoBall} class.
 *
 * <p>
 * This test verifies:
 * <ul>
 *     <li>Correct item name</li>
 *     <li>Correct buy price</li>
 *     <li>Object instantiation without errors</li>
 * </ul>
 * </p>
 *
 * <p>
 * JavaFX runtime must be initialized because
 * Item internally loads a JavaFX Image.
 * </p>
 */
class DinoBallTest {

    /**
     * Initializes JavaFX runtime before running tests.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Tests that DinoBall is created
     * with correct predefined values.
     */
    @Test
    void testDinoBallCreation() {
        DinoBall ball = new DinoBall();

        assertEquals("DinoBall", ball.getName());
        assertEquals(20, ball.getBuyPrice());
    }

    /**
     * Tests that multiple instances
     * have consistent buy price.
     */
    @Test
    void testBuyPriceConsistency() {
        DinoBall ball1 = new DinoBall();
        DinoBall ball2 = new DinoBall();

        assertEquals(ball1.getBuyPrice(), ball2.getBuyPrice());
    }
}