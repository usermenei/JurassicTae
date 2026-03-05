package gamemode.gym.input;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InputHandler}.
 *
 * <p>This test verifies that specific key presses correctly trigger
 * the corresponding Runnable callbacks registered by
 * {@link InputHandler#setup(Scene, Runnable, Runnable, Runnable)}.
 *
 * <p>No mocking framework is used. Simple boolean flags are used
 * to verify whether the correct actions were executed.
 */
public class InputHandlerTest {

    /**
     * Initializes the JavaFX runtime once before running tests.
     */
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX already started
        }
    }

    /**
     * Tests that pressing the D key triggers the lane0 action.
     */
    @Test
    void testLane0KeyPress() {

        Scene scene = new Scene(new Group(), 800, 600);

        final boolean[] lane0Called = {false};
        final boolean[] lane1Called = {false};
        final boolean[] escCalled = {false};

        InputHandler.setup(
                scene,
                () -> lane0Called[0] = true,
                () -> lane1Called[0] = true,
                () -> escCalled[0] = true
        );

        KeyEvent event = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.D,
                false, false, false, false
        );

        scene.getOnKeyPressed().handle(event);

        assertTrue(lane0Called[0]);
        assertFalse(lane1Called[0]);
        assertFalse(escCalled[0]);
    }

    /**
     * Tests that pressing the F key triggers the lane1 action.
     */
    @Test
    void testLane1KeyPress() {

        Scene scene = new Scene(new Group(), 800, 600);

        final boolean[] lane0Called = {false};
        final boolean[] lane1Called = {false};

        InputHandler.setup(
                scene,
                () -> lane0Called[0] = true,
                () -> lane1Called[0] = true,
                () -> {}
        );

        KeyEvent event = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.F,
                false, false, false, false
        );

        scene.getOnKeyPressed().handle(event);

        assertFalse(lane0Called[0]);
        assertTrue(lane1Called[0]);
    }

    /**
     * Tests that pressing the ESC key triggers the escape action.
     */
    @Test
    void testEscapeKeyPress() {

        Scene scene = new Scene(new Group(), 800, 600);

        final boolean[] escCalled = {false};

        InputHandler.setup(
                scene,
                () -> {},
                () -> {},
                () -> escCalled[0] = true
        );

        KeyEvent event = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.ESCAPE,
                false, false, false, false
        );

        scene.getOnKeyPressed().handle(event);

        assertTrue(escCalled[0]);
    }
}