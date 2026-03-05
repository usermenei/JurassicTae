package gamemode.lobby.Item.Weapon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for predefined Weapon implementations.
 *
 * <p>
 * This test suite verifies that each weapon variant:
 * <ul>
 *     <li>Has the correct predefined name</li>
 *     <li>Has the correct buy price</li>
 *     <li>Has the correct damage value</li>
 * </ul>
 * </p>
 *
 * <p>
 * JavaFX runtime must be initialized because
 * Weapon extends Item which loads JavaFX Image.
 * </p>
 */
class WeaponVariantsTest {

    /**
     * Initializes JavaFX runtime once before all tests.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Tests AnestheticDart predefined values.
     */
    @Test
    void testAnestheticDart() {
        AnestheticDart weapon = new AnestheticDart();

        assertEquals("AnestheticDart", weapon.getName());
        assertEquals(50, weapon.getBuyPrice());
        assertEquals(10, weapon.getDamage());
    }

    /**
     * Tests ElectricGun predefined values.
     */
    @Test
    void testElectricGun() {
        ElectricGun weapon = new ElectricGun();

        assertEquals("Electric Gun", weapon.getName());
        assertEquals(400, weapon.getBuyPrice());
        assertEquals(290, weapon.getDamage());
    }

    /**
     * Tests RifleGun predefined values.
     */
    @Test
    void testRifleGun() {
        RifleGun weapon = new RifleGun();

        assertEquals("Rifle Gun", weapon.getName());
        assertEquals(150, weapon.getBuyPrice());
        assertEquals(100, weapon.getDamage());
    }
}