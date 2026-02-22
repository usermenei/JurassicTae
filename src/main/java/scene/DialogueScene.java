package scene;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import spawnscreen.logic.GameController;

public class DialogueScene {

    private Scene scene;
    private int index = 0;

    private String[] dialogue = {
            "Welcome to Jurassic Tae...",
            "Dinosaurs roam freely in this jungle...",
            "Only the strongest survive...",
            "Your journey begins now."
    };

    public DialogueScene() {

        StackPane root = new StackPane();

        Label text = new Label(dialogue[0]);
        text.setFont(new Font(36));

        root.getChildren().add(text);

        scene = new Scene(root, 1422, 800);

        scene.setOnMouseClicked(e -> {
            index++;
            if (index < dialogue.length) {
                text.setText(dialogue[index]);
            } else {
                GameController.getInstance().returnToMain();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}