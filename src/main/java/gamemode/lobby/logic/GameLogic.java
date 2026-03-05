package gamemode.lobby.logic;

import gamemode.lobby.Player.Player;

/**
 * <h2>GameLogic</h2>
 *
 * <p>
 * The GameLogic class is responsible for controlling the core logic
 * of the lobby game mode. It manages the player instance and
 * updates the game state during runtime.
 * </p>
 *
 * <p>
 * This class follows the <b>Singleton Design Pattern</b> to ensure
 * that only one instance of GameLogic exists throughout the application.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Manage the Player object</li>
 *     <li>Track and update current GameState</li>
 *     <li>Provide a centralized access point for lobby game logic</li>
 * </ul>
 *
 * @author Pongtawan
 * @version 1.0
 */
public class GameLogic {

    /**
     * Singleton instance of GameLogic.
     */
    private static GameLogic instance;

    /**
     * The main player in the lobby.
     */
    private Player player;

    /**
     * Current state of the game.
     */
    private GameState gameState;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the player with default spawn coordinates.
     */
    private GameLogic() {
        player = new Player(400, 711);
        gameState = GameState.SPAWN;
    }

    /**
     * Returns the single instance of GameLogic.
     * If no instance exists, it will be created.
     *
     * @return the singleton GameLogic instance
     */
    public static GameLogic getInstance() {
        if (instance == null) {
            instance = new GameLogic();
        }
        return instance;
    }

    /**
     * Returns the current player.
     *
     * @return the Player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the current game state.
     *
     * @return current GameState
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the current game state.
     *
     * @param gameState the new GameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Updates the game logic depending on the current game state.
     *
     * <p>
     * This method should be called every frame (e.g., in a game loop).
     * </p>
     */
    public void update() {
        if (gameState == GameState.SPAWN) {
            // Logic executed during SPAWN state
            // Example: reset player position, initialize UI, etc.
        }

        // Future states can be added here
        // Example:
        // if (gameState == GameState.PLAYING) { ... }
        // if (gameState == GameState.PAUSED) { ... }
    }
    /**
     * Resets the singleton instance.
     * Used only for testing purposes.
     */
    public static void reset() {
        instance = null;
    }
}