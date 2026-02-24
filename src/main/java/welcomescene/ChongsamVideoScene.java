package welcomescene;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import gamemode.lobby.logic.GameController;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class ChongsamVideoScene {

    private Scene scene;
    private MediaPlayer player;

    public ChongsamVideoScene() {

        StackPane root = new StackPane();
        scene = new Scene(root, 1422, 800);

        Media media = new Media(
                getClass().getResource("/welcomescene/chongsamintro.mp4").toExternalForm()
        );

        player = new MediaPlayer(media);
        MediaView view = new MediaView(player);
        view.setFitWidth(1422);
        view.setFitHeight(800);

        root.getChildren().add(view);

        player.setOnReady(player::play);

        // When video ends → go to News scene
        player.setOnEndOfMedia(() -> {
            stopAll();
            GameController.getInstance().switchScene(
                    new NewsReportScene().getScene()
            );
        });

        // 🔥 PRESS F TO SKIP
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F) {
                stopAll();
                GameController.getInstance().switchScene(
                        new NewsReportScene().getScene()
                );
            }
        });
        // ---------------- TEXT ----------------
        Label infoText = new Label("Press F to Skip");

        // Load pixel font
        Font pixelFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),
                32
        );
        infoText.setFont(pixelFont);

        infoText.setStyle("""
        -fx-text-fill: white;
        """); // removed background box

        StackPane.setAlignment(infoText, Pos.BOTTOM_CENTER);
        infoText.setTranslateY(-40);

        root.getChildren().add(infoText);

        // ---------------- BLINK EFFECT ----------------
        FadeTransition blink = new FadeTransition(Duration.seconds(0.8), infoText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
    }

    private void stopAll() {
        if (player != null) {
            player.stop();
            player.dispose(); // important
        }
    }

    public Scene getScene() {
        return scene;
    }
}