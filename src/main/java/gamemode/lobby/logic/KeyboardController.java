package gamemode.lobby.logic;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * The {@code KeyboardController} class handles keyboard input
 * for the lobby game mode.
 *
 * <p>
 * It listens to key press and key release events from a JavaFX {@link Scene}
 * and maintains the current pressed state of specific keys:
 * </p>
 *
 * <ul>
 *     <li>W - Move Up</li>
 *     <li>S - Move Down</li>
 *     <li>A - Move Left</li>
 *     <li>D - Move Right</li>
 *     <li>P - Custom action (e.g., Pause / Pushup / etc.)</li>
 *     <li>F - Custom interaction key</li>
 * </ul>
 *
 * <p>
 * This class only tracks input states and does not perform any game logic.
 * The game loop should query the boolean getters to determine player actions.
 * </p>
 *
 * @author Pongtawan
 */
public class KeyboardController {

    /** True if W key is currently pressed */
    private boolean upPressed;

    /** True if S key is currently pressed */
    private boolean downPressed;

    /** True if A key is currently pressed */
    private boolean leftPressed;

    /** True if D key is currently pressed */
    private boolean rightPressed;

    /** True if P key is currently pressed */
    private boolean pPressed;

    /** True if F key is currently pressed */
    private boolean fPressed;

    /**
     * Constructs a KeyboardController and attaches
     * key listeners to the provided JavaFX Scene.
     *
     * @param scene the JavaFX Scene where key events are captured
     */
    public KeyboardController(Scene scene) {
        keyboardSetup(scene);
    }

    /**
     * Sets up key pressed and key released event handlers.
     *
     * @param scene the JavaFX Scene to attach event listeners to
     */
    private void keyboardSetup(Scene scene) {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) upPressed = true;
            if (e.getCode() == KeyCode.S) downPressed = true;
            if (e.getCode() == KeyCode.A) leftPressed = true;
            if (e.getCode() == KeyCode.D) rightPressed = true;
            if (e.getCode() == KeyCode.P) pPressed = true;
            if (e.getCode() == KeyCode.F) fPressed = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) upPressed = false;
            if (e.getCode() == KeyCode.S) downPressed = false;
            if (e.getCode() == KeyCode.A) leftPressed = false;
            if (e.getCode() == KeyCode.D) rightPressed = false;
            if (e.getCode() == KeyCode.P) pPressed = false;
            if (e.getCode() == KeyCode.F) fPressed = false;
        });
    }

    /**
     * @return true if W key is currently pressed
     */
    public boolean isUpPressed() {
        return upPressed;
    }

    /**
     * @return true if S key is currently pressed
     */
    public boolean isDownPressed() {
        return downPressed;
    }

    /**
     * @return true if A key is currently pressed
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     * @return true if D key is currently pressed
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * @return true if P key is currently pressed
     */
    public boolean isPPressed() {
        return pPressed;
    }

    /**
     * @return true if F key is currently pressed
     */
    public boolean isFPressed() {
        return fPressed;
    }
}