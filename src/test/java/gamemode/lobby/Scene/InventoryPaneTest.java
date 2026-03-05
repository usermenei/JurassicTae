package gamemode.lobby.Scene;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InventoryPane}.
 *
 * <p>This test suite verifies the correct initialization
 * and basic functionality of the inventory UI component.</p>
 *
 * <p>Because InventoryPane depends on JavaFX, the JavaFX
 * runtime must be initialized before running tests.</p>
 */
class InventoryPaneTest {

    /**
     * Initializes the JavaFX Toolkit once before all tests.
     */
    @BeforeAll
    static void initFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // already initialized
        }
    }

    /**
     * Ensures the InventoryPane object can be created
     * without throwing exceptions.
     */
    @Test
    void testPaneCreation() {
        InventoryPane pane = new InventoryPane();
        assertNotNull(pane);
    }

    /**
     * Verifies that loadItems() executes successfully.
     */
    @Test
    void testLoadItemsRuns() {
        InventoryPane pane = new InventoryPane();
        assertDoesNotThrow(pane::loadItems);
    }

    /**
     * Ensures the pane has children nodes after initialization.
     */
    @Test
    void testPaneContainsUIComponents() {
        InventoryPane pane = new InventoryPane();
        assertTrue(pane.getChildren().size() > 0);
    }

}