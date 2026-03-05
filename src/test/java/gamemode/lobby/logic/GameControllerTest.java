package gamemode.lobby.logic;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1>GameControllerTest</h1>
 *
 * <p>Unit tests for GameController using proper JavaFX thread handling.</p>
 *
 * <p>
 * This class ensures:
 * </p>
 * <ul>
 *     <li>Singleton behavior</li>
 *     <li>Initialization</li>
 *     <li>Scene switching</li>
 * </ul>
 *
 * <p><b>Important:</b> JavaFX components must be created on the FX Application Thread.</p>
 *
 * @author Pongtawan
 * @version 2.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameControllerTest {

    private GameController controller;
    private Stage stage;
    private Scene scene;

    /**
     * Initialize JavaFX Toolkit before all tests.
     */
    @BeforeAll
    void initJFX() {
        new JFXPanel(); // Initializes JavaFX environment
        Platform.setImplicitExit(false);
    }

    /**
     * Setup runs before each test.
     */
    @BeforeEach
    void setUp() throws Exception {

        controller = GameController.getInstance();

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            stage = new Stage();
            scene = new Scene(new StackPane(), 800, 600);
            controller.init(stage, scene);
            latch.countDown();
        });

        // Wait for FX thread to finish
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "FX initialization timed out");
    }

    /**
     * Test Singleton pattern.
     */
    @Test
    void testSingletonInstance() {
        GameController instance1 = GameController.getInstance();
        GameController instance2 = GameController.getInstance();

        assertSame(instance1, instance2,
                "GameController should follow Singleton pattern");
    }

    /**
     * Test controller initialization.
     */
    @Test
    void testInitialization() {
        assertNotNull(controller.getKeyboard(),
                "KeyboardController should be initialized");

        assertFalse(controller.isGameEnded(),
                "Game should not be ended by default");
    }

    /**
     * Test scene switching.
     */
    @Test
    void testSwitchScene() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Scene newScene = new Scene(new StackPane(), 400, 300);
            controller.switchScene(newScene);
            assertEquals(newScene, stage.getScene(),
                    "Stage scene should update correctly");
            latch.countDown();
        });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "Scene switch timed out");
    }
}