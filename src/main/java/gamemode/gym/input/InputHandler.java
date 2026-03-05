package gamemode.gym.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Utility class for registering keyboard input handlers on a JavaFX {@link Scene}.
 * <p>
 * Maps specific key presses to game actions via {@link Runnable} callbacks,
 * keeping input binding decoupled from game logic.
 * </p>
 */
public class InputHandler {

    /**
     * Registers key press handlers on the given scene.
     * <p>Key bindings:</p>
     * <ul>
     *   <li><b>D</b> — triggers the lane 0 action</li>
     *   <li><b>F</b> — triggers the lane 1 action</li>
     *   <li><b>ESC</b> — triggers the escape action</li>
     * </ul>
     *
     * @param scene        the {@link Scene} to attach the key handler to
     * @param lane0        the action to run when D is pressed
     * @param lane1        the action to run when F is pressed
     * @param escapeAction the action to run when ESC is pressed
     */
    public static void setup(Scene scene,
                             Runnable lane0,
                             Runnable lane1,
                             Runnable escapeAction) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) {
                lane0.run();
            } else if (e.getCode() == KeyCode.F) {
                lane1.run();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                escapeAction.run();
            }
        });
    }
}