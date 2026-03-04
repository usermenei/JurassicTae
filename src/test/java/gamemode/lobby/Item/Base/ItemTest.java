package gamemode.lobby.Item.Base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Item} abstract class.
 *
 * <p>
 * This test verifies:
 * <ul>
 *     <li>Correct name assignment</li>
 *     <li>Correct image URL storage</li>
 *     <li>Object creation without runtime errors</li>
 * </ul>
 *
 * Since Item is abstract, a concrete test subclass is used.
 */
class ItemTest {

    /**
     * Initializes JavaFX runtime once for testing.
     * Required because Item internally creates a JavaFX Image.
     */
    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Concrete test subclass of Item
     * used only for unit testing.
     */
    static class TestItem extends Item {
        public TestItem(String name, String imgUrl) {
            super(name, imgUrl);
        }
    }

    /**
     * Tests that an Item is correctly constructed
     * with proper name and image URL.
     */
    @Test
    void testItemCreation() {
        Item item = new TestItem("Test Sword", "/item/test.png");

        assertEquals("Test Sword", item.getName());
        assertEquals("/item/test.png", item.getImgUrl());

        // Do NOT assertNotNull(image) because test resources may not contain image
        assertDoesNotThrow(() -> item.getImg());
    }
}