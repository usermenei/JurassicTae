package gamemode.lobby.Scene;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ShopScene}.
 *
 * <p>This test suite verifies that the shop UI initializes correctly
 * and that major methods such as loadShop() and loadSell()
 * execute without runtime errors.</p>
 *
 * <p>Because ShopScene uses JavaFX components, the JavaFX toolkit
 * must be initialized before tests run.</p>
 */
class ShopSceneTest {

    /**
     * Initializes JavaFX runtime before tests.
     */
    @BeforeAll
    static void initFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already started
        }
    }

    /**
     * Verifies that the ShopScene object can be created.
     */
    @Test
    void testShopSceneCreation() {
        ShopScene shop = new ShopScene();
        assertNotNull(shop);
    }

    /**
     * Ensures loadShop() runs without throwing exceptions.
     */
    @Test
    void testLoadShop() {
        ShopScene shop = new ShopScene();
        assertDoesNotThrow(shop::loadShop);
    }

    /**
     * Ensures loadSell() runs without throwing exceptions.
     */
    @Test
    void testLoadSell() {
        ShopScene shop = new ShopScene();
        assertDoesNotThrow(shop::loadSell);
    }

    /**
     * Verifies that the switch button exists and has default text.
     */
    @Test
    void testSwitchButtonExists() {
        ShopScene shop = new ShopScene();
        assertNotNull(shop.getSwitchBtt());
        assertEquals("Sell", shop.getSwitchBtt().getText());
    }

}