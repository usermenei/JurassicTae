package gamemode.gym.core;

import javafx.application.Platform;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GameTimer}.
 *
 * <p>This test class verifies the logical behavior of the timer including:
 * <ul>
 *     <li>Initial text display after construction</li>
 *     <li>Countdown updates while running</li>
 *     <li>Execution of the finish callback</li>
 *     <li>Stopping the timer safely</li>
 * </ul>
 *
 * <p>No mocking frameworks are used. The tests rely only on simple
 * Java logic and JavaFX components.
 */
public class GameTimerTest {

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
     * Tests that the constructor correctly initializes
     * the timer text with the starting time.
     */
    @Test
    void testConstructorInitialText() {

        Text text = new Text();
        GameTimer timer = new GameTimer(30, text, () -> {});

        assertEquals("Time: 30", text.getText());
    }

    /**
     * Tests that calling stop() before start()
     * does not throw an exception.
     */
    @Test
    void testStopWithoutStart() {

        Text text = new Text();
        GameTimer timer = new GameTimer(10, text, () -> {});

        assertDoesNotThrow(timer::stop);
    }

    /**
     * Tests that the timer eventually triggers
     * the finish callback when time reaches zero.
     */
    @Test
    void testTimerFinishCallback() throws InterruptedException {

        Text text = new Text();

        final boolean[] finished = {false};

        GameTimer timer = new GameTimer(1, text, () -> finished[0] = true);

        timer.start();

        Thread.sleep(1200); // wait slightly longer than 1 second

        assertTrue(finished[0], "onFinish should be executed when time reaches zero");

        timer.stop();
    }

    /**
     * Tests that the timer updates the displayed time
     * while running.
     */
    @Test
    void testTimerUpdatesText() throws InterruptedException {

        Text text = new Text();
        GameTimer timer = new GameTimer(2, text, () -> {});

        timer.start();

        Thread.sleep(1100);

        assertNotEquals("Time: 2", text.getText(),
                "Timer text should update after 1 second");

        timer.stop();
    }
}