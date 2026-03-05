package gamemode.lobby.Item.Potion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all predefined Potion implementations.
 *
 * <p>
 * This test suite verifies that each potion variant:
 * <ul>
 *     <li>Has the correct predefined name</li>
 *     <li>Has the correct buy price</li>
 *     <li>Has the correct sell price</li>
 * </ul>
 * </p>
 *
 * <p>
 * Since Potion extends Item and internally loads JavaFX {@code Image},
 * the JavaFX runtime must be initialized before executing the tests.
 * </p>
 */
class PotionVariantsTest {

    /**
     * Initializes JavaFX runtime once before all tests.
     * Required because Item loads JavaFX Image objects.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Tests that HealPotion is created
     * with correct predefined values.
     */
    @Test
    void testHealPotion() {
        HealPotion potion = new HealPotion();

        assertEquals("Heal Potion", potion.getName());
        assertEquals(50, potion.getBuyPrice());
        assertEquals(10, potion.getSellPrice());
    }

    /**
     * Tests that SpeedPotion is created
     * with correct predefined values.
     */
    @Test
    void testSpeedPotion() {
        SpeedPotion potion = new SpeedPotion();

        assertEquals("Speed Potion", potion.getName());
        assertEquals(50, potion.getBuyPrice());
        assertEquals(10, potion.getSellPrice());
    }

    /**
     * Tests that StrengthPotion is created
     * with correct predefined values.
     */
    @Test
    void testStrengthPotion() {
        StrengthPotion potion = new StrengthPotion();

        assertEquals("Strength Potion", potion.getName());
        assertEquals(100, potion.getBuyPrice());
        assertEquals(10, potion.getSellPrice());
    }

    /**
     * Tests that ExpPotion is created
     * with correct predefined values.
     */
    @Test
    void testExpPotion() {
        ExpPotion potion = new ExpPotion();

        assertEquals("Exp Potion", potion.getName());
        assertEquals(40, potion.getBuyPrice());
        assertEquals(10, potion.getSellPrice());
    }
}