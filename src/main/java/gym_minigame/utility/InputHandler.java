package gym_minigame.utility;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {

    public static void setup(Scene scene,
                             Runnable lane0,
                             Runnable lane1,
                             Runnable escapeAction) {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) {
                lane0.run();
            }
            else if (e.getCode() == KeyCode.F) {
                lane1.run();
            }
            else if (e.getCode() == KeyCode.ESCAPE) {
                escapeAction.run();
            }
        });
    }
}