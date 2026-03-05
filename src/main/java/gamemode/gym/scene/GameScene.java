package gamemode.gym.scene;

import gamemode.gym.ui.GameUI;
import gamemode.gym.input.InputHandler;
import gamemode.gym.core.GameLoop;
import gamemode.gym.note.HitController;
import gamemode.gym.note.NoteManager;
import gamemode.gym.system.ComboManager;
import gamemode.gym.core.GameTimer;
import gamemode.gym.system.GameEffect;
import gamemode.gym.system.ScoreManager;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.util.function.Consumer;

/**
 * The active gameplay scene for the gym rhythm mini-game.
 * <p>
 * Assembles and wires all game subsystems — visuals, state managers, controllers,
 * and input — then exposes {@link #start()} to begin the session. When the timer
 * expires or the player presses ESC, the final score is passed to the
 * {@code onBack} callback and the scene returns to the main menu.
 * </p>
 *
 * <p>Subsystem responsibilities:</p>
 * <ul>
 *   <li>{@link GameUI} — renders the canvas, score, combo, timer, and judgment text.</li>
 *   <li>{@link NoteManager} — spawns, moves, and removes falling notes.</li>
 *   <li>{@link ScoreManager} — tracks and displays the player's score.</li>
 *   <li>{@link ComboManager} — tracks the combo count and score multiplier.</li>
 *   <li>{@link GameTimer} — counts down from 60 seconds and triggers game over.</li>
 *   <li>{@link HitController} — processes lane key presses and registers hits.</li>
 *   <li>{@link GameLoop} — drives the per-frame note spawning and miss detection.</li>
 *   <li>{@link InputHandler} — maps D / F / ESC key presses to game actions.</li>
 * </ul>
 */
public class GameScene {

    /** The UI layer managing all visual elements and the JavaFX scene. */
    private final GameUI ui;

    /** The animation timer driving note spawning and miss penalty logic each frame. */
    private final GameLoop gameLoop;

    /** The countdown timer that triggers game over when it reaches zero. */
    private final GameTimer timer;

    /** Tracks and updates the player's score throughout the session. */
    private final ScoreManager scoreManager;

    /**
     * Callback invoked at the end of a session (game over or ESC), receiving
     * the final score to pass back to the main menu.
     */
    private final Consumer<Integer> onBackToMenu;

    /**
     * Constructs the {@code GameScene} and fully initialises all subsystems.
     * <p>
     * Subsystems are created in dependency order: visuals first, then state
     * managers, then controllers that depend on both, and finally input binding.
     * The game does not begin until {@link #start()} is called.
     * </p>
     *
     * @param width           the width of the game canvas in pixels
     * @param height          the height of the game canvas in pixels
     * @param noteSize        the width and height of each note in pixels
     * @param startX          the base X offset from which lane positions are calculated
     * @param red             the image used for lane 0 notes
     * @param blue            the image used for lane 1 notes
     * @param backgroundImage the background image displayed behind the game canvas
     * @param onBack          callback invoked with the final score when the session ends
     */
    public GameScene(int width, int height, int noteSize, int startX,
                     Image red, Image blue, Image backgroundImage,
                     Consumer<Integer> onBack) {

        this.onBackToMenu = onBack;

        // 1. Initialize Visuals
        this.ui = new GameUI(width, height, noteSize, startX, backgroundImage, this::exitGame);
        GameEffect hitEffect  = new GameEffect("/gamemode/gym/hit.gif",  150, 3);
        GameEffect missEffect = new GameEffect("/gamemode/gym/miss.gif", 150, 3);

        // 2. Initialize State Managers
        NoteManager  noteManager  = new NoteManager(ui.getGameLayer(), height);
        this.scoreManager         = new ScoreManager(ui.getScoreText());
        ComboManager comboManager = new ComboManager(ui.getComboText(), ui.getMultiplierText());
        this.timer                = new GameTimer(60, ui.getTimerText(), this::handleGameOver);

        // 3. Initialize Controllers & Loop
        HitController hitController = new HitController(
                ui, noteManager, comboManager, scoreManager, hitEffect, noteSize, startX);
        this.gameLoop = new GameLoop(
                ui, noteManager, comboManager, scoreManager, missEffect, noteSize, startX, red, blue);

        // 4. Wire Inputs
        InputHandler.setup(
                ui.getScene(),
                () -> hitController.handleKeyPress(0),
                () -> hitController.handleKeyPress(1),
                this::exitGame
        );
    }

    /**
     * Starts the game loop and countdown timer, beginning the session.
     * Should be called once after the scene has been set on the primary stage.
     */
    public void start() {
        gameLoop.start();
        timer.start();
    }

    /**
     * Handles the end of the session when the countdown timer expires.
     * Stops all active loops and returns the final score to the menu callback.
     */
    private void handleGameOver() {
        stopAll();
        onBackToMenu.accept(scoreManager.getScore());
    }

    /**
     * Handles an early exit triggered by the player pressing ESC.
     * Stops all active loops and returns the current score to the menu callback.
     */
    private void exitGame() {
        stopAll();
        onBackToMenu.accept(scoreManager.getScore());
    }

    /**
     * Stops both the game loop and the countdown timer.
     */
    private void stopAll() {
        gameLoop.stop();
        timer.stop();
    }

    /**
     * Returns the JavaFX {@link Scene} for this game screen.
     *
     * @return the scene instance
     */
    public Scene getScene() {
        return ui.getScene();
    }
}