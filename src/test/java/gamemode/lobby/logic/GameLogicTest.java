package gamemode.lobby.logic;

import gamemode.lobby.Player.Player;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1>GameLogicTest</h1>
 *
 * Unit tests for the GameLogic class.
 *
 * This test verifies:
 * - Singleton behavior
 * - Player initialization
 * - Default game state
 * - State modification
 * - Update execution
 *
 * This class does NOT require JavaFX.
 *
 * @author Pongtawan
 * @version 1.0
 */
class GameLogicTest {

    private GameLogic gameLogic;

    /**
     * Setup before each test.
     */
    @BeforeAll
    static void initJavaFX() {
        new JFXPanel(); // initializes JavaFX safely
    }

    @BeforeEach
    void setUp() {
        GameLogic.reset();          // Reset singleton
        gameLogic = GameLogic.getInstance();
    }

    /**
     * Test that GameLogic follows Singleton pattern.
     */
    @Test
    void testSingletonInstance() {
        GameLogic instance1 = GameLogic.getInstance();
        GameLogic instance2 = GameLogic.getInstance();

        assertSame(instance1, instance2,
                "GameLogic should return the same instance");
    }

    /**
     * Test that player is initialized properly.
     */
    @Test
    void testPlayerInitialization() {
        Player player = gameLogic.getPlayer();

        assertNotNull(player,
                "Player should be initialized");
    }

    /**
     * Test default game state.
     */
    @Test
    void testDefaultGameState() {
        assertEquals(GameState.SPAWN,
                gameLogic.getGameState(),
                "Default GameState should be SPAWN");
    }

    /**
     * Test setting a new game state.
     */
    @Test
    void testSetGameState() {
        gameLogic.setGameState(GameState.PLAYING);

        assertEquals(GameState.PLAYING,
                gameLogic.getGameState(),
                "GameState should change to PLAYING");
    }

    /**
     * Test update method runs without exception.
     */
    @Test
    void testUpdateDoesNotCrash() {
        assertDoesNotThrow(() -> gameLogic.update(),
                "Update method should not throw exception");
    }
}