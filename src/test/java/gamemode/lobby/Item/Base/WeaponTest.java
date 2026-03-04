package gamemode.lobby.Item.Base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Weapon} class.
 *
 * <p>
 * This test suite verifies:
 * <ul>
 *     <li>Correct weapon creation</li>
 *     <li>Buy price and damage assignment</li>
 *     <li>Validation of negative buy price</li>
 *     <li>Validation of negative damage</li>
 * </ul>
 *
 * Since Weapon extends Item and loads JavaFX Image internally,
 * JavaFX runtime must be initialized before testing.
 */
class WeaponTest {

    /**
     * Initializes JavaFX runtime once for testing.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Concrete test subclass of Weapon
     * used only for unit testing.
     */
    static class TestWeapon extends Weapon {
        public TestWeapon(String name, String imgUrl, int buyPrice, int damage) {
            super(name, imgUrl, buyPrice, damage);
        }
    }

    /**
     * Tests that a Weapon is correctly created
     * with proper name, buy price, and damage.
     */
    @Test
    void testWeaponCreation() {
        Weapon weapon = new TestWeapon(
                "Sword",
                "/item/test.png",
                100,
                25
        );

        assertEquals("Sword", weapon.getName());
        assertEquals(100, weapon.getBuyPrice());
        assertEquals(25, weapon.getDamage());

        // Avoid strict image assertion (depends on test resources)
        assertDoesNotThrow(weapon::getImg);
    }

    /**
     * Tests that a negative buy price
     * throws IllegalArgumentException.
     */
    @Test
    void testNegativeBuyPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestWeapon("Sword", "/item/test.png", -1, 10)
        );
    }

    /**
     * Tests that negative damage
     * throws IllegalArgumentException.
     */
    @Test
    void testNegativeDamageThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TestWeapon("Sword", "/item/test.png", 100, -5)
        );
    }
}