package gamemode.forest.util;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AssetLoader}.
 *
 * <p>These tests verify that image resources are correctly loaded
 * from the classpath and that appropriate exceptions are thrown
 * when resources are missing.</p>
 */
public class AssetLoaderTest {

    /**
     * Initializes the JavaFX toolkit before any tests run.
     * Required because {@link Image} is part of JavaFX.
     */
    @BeforeAll
    static void initJavaFX() {
        new JFXPanel();
    }

    /**
     * Tests that a valid resource path successfully loads an image.
     */
    @Test
    void testLoadValidImage() {
        Image image = AssetLoader.load("/item/test.png");

        assertNotNull(image);
        assertFalse(image.isError());
    }

    /**
     * Tests that loading a non-existent resource throws a RuntimeException.
     */
    @Test
    void testLoadMissingImageThrowsException() {

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> AssetLoader.load("/images/does_not_exist.png")
        );

        assertTrue(exception.getMessage().contains("Missing resource"));
    }

    /**
     * Tests that different valid paths return independent Image objects.
     */
    @Test
    void testMultipleLoads() {
        Image image1 = AssetLoader.load("/item/test.png");
        Image image2 = AssetLoader.load("/item/test.png");

        assertNotNull(image1);
        assertNotNull(image2);
        assertNotSame(image1, image2);
    }
}