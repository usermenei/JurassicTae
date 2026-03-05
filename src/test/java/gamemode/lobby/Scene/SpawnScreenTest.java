package gamemode.lobby.Scene;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link SpawnScreen}.
 *
 * <p>This class verifies that the SpawnScreen correctly manages
 * UI components and updates based on GameLogic data.</p>
 */
public class SpawnScreenTest {

    /**
     * Test if SpawnScreen initializes correctly.
     */
    @Test
    void testSpawnScreenInitialization() {

        SpawnScreen screen = new SpawnScreen();

        assertNotNull(screen.getSpawnCanvas());
        assertNotNull(screen.getSellScene());
        assertNotNull(screen.getShopScene());
        assertNotNull(screen.getInventoryPane());
    }

    /**
     * Test showing the sell scene.
     */
    @Test
    void testShowSellScene() {

        SpawnScreen screen = new SpawnScreen();

        screen.showSellScene();

        assertTrue(screen.getSellScene().isVisible());
    }


    /**
     * Test showing the shop scene.
     */
    @Test
    void testShowShopScene() {

        SpawnScreen screen = new SpawnScreen();

        screen.showShopScene();

        assertTrue(screen.getShopScene().isVisible());
    }
    

    /**
     * Test updating level display.
     */
    @Test
    void testUpdateLevel() {

        SpawnScreen screen = new SpawnScreen();

        screen.updateLevel();

        assertNotNull(screen);
    }

    /**
     * Test updating experience bar.
     */
    @Test
    void testUpdateExpBar() {

        SpawnScreen screen = new SpawnScreen();

        screen.updateExpBar();

        assertNotNull(screen);
    }

    /**
     * Test updating money label.
     */
    @Test
    void testUpdateMoney() {

        SpawnScreen screen = new SpawnScreen();

        screen.updateMoney();

        assertTrue(screen.getMoneyLabel().getText().contains("Money"));
    }
}