package gamemode.lobby.logic;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.JavaFXInitializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link KeyboardController}.
 *
 * <p>
 * This test verifies that keyboard key press and key release
 * events correctly update the internal boolean state inside
 * {@code KeyboardController}.
 * </p>
 *
 * <p>
 * Since {@code KeyboardController} depends on JavaFX {@link Scene},
 * the JavaFX Toolkit must be initialized before running tests.
 * This is handled safely by {@link JavaFXInitializer}.
 * </p>
 *
 * <h2>Test Coverage:</h2>
 * <ul>
 *     <li>W key press and release</li>
 *     <li>Multiple key presses (W + D)</li>
 *     <li>Special keys (P and F)</li>
 * </ul>
 *
 * @author YourName
 * @version 1.0
 */
class KeyboardControllerTest {

    private KeyboardController controller;
    private Scene scene;

    /**
     * Initializes JavaFX Toolkit once before all tests.
     *
     * <p>
     * Uses {@link JavaFXInitializer} to prevent
     * "Toolkit already initialized" errors.
     * </p>
     */
    @BeforeAll
    static void setupJavaFX() {
        JavaFXInitializer.init();
    }

    /**
     * Creates a new {@link KeyboardController}
     * and {@link Scene} before each test.
     */
    @BeforeEach
    void setUp() {
        scene = new Scene(new Pane());
        controller = new KeyboardController(scene);
    }

    /**
     * Tests that pressing W sets upPressed to true.
     */
    @Test
    void testUpKeyPress() {
        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.W,
                        false, false, false, false)
        );

        assertTrue(controller.isUpPressed(),
                "W key press should set upPressed to true");
    }

    /**
     * Tests that releasing W sets upPressed to false.
     */
    @Test
    void testUpKeyRelease() {

        // First press
        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.W,
                        false, false, false, false)
        );

        // Then release
        scene.getOnKeyReleased().handle(
                new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.W,
                        false, false, false, false)
        );

        assertFalse(controller.isUpPressed(),
                "W key release should set upPressed to false");
    }

    /**
     * Tests multiple key presses (W and D).
     */
    @Test
    void testMultipleKeyPress() {

        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.W,
                        false, false, false, false)
        );

        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.D,
                        false, false, false, false)
        );

        assertTrue(controller.isUpPressed(),
                "W key press should set upPressed to true");

        assertTrue(controller.isRightPressed(),
                "D key press should set rightPressed to true");
    }

    /**
     * Tests special keys P and F.
     */
    @Test
    void testSpecialKeys() {

        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.P,
                        false, false, false, false)
        );

        scene.getOnKeyPressed().handle(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F,
                        false, false, false, false)
        );

        assertTrue(controller.isPPressed(),
                "P key press should set pPressed to true");

        assertTrue(controller.isFPressed(),
                "F key press should set fPressed to true");
    }
}