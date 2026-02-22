package main;

import scene.IntroScene;
import spawnscreen.Scene.SpawnScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import spawnscreen.logic.GameController;
import spawnscreen.logic.GameLogic;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameLogic.getInstance();
        // Create Hub root
        SpawnScreen root = new SpawnScreen();

        // Create main scene
        Scene mainScene = new Scene(root, 1422, 800);

        GameController.getInstance().init(stage, mainScene);
        GameController.getInstance().setRoot(root); // Initialize controller (FIXED)


        stage.setTitle("Jurassic Tae");

        // Start with intro scene instead of hub
        stage.setScene(new IntroScene().getScene());

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
