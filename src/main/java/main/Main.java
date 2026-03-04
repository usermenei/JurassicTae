package main;

import javafx.application.Platform;
import welcomescene.IntroScene;
import gamemode.lobby.Scene.SpawnScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.GameLogic;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameLogic.getInstance();
        SpawnScreen root = new SpawnScreen();
        Scene mainScene = new Scene(root, 1422, 800);

        GameController.getInstance().init(stage, mainScene);
        GameController.getInstance().setRoot(root);

        stage.setTitle("Jurassic Tae");
        stage.setScene(new IntroScene().getScene());

        // ✅ Force shutdown all threads when window is closed
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
