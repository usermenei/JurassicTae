package spawnscreen.logic;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyboardController {

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean pPressed;
    private boolean fPressed;

    public KeyboardController(Scene scene) {
        keyboardSetup(scene);
    }

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

    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isPPressed() { return pPressed; }
    public boolean isFPressed(){
        return fPressed;
    }
}
