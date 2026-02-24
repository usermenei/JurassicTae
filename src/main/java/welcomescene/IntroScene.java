package welcomescene;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import gamemode.lobby.logic.GameController;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.text.Font;

public class IntroScene {

    private Scene scene;
    private MediaPlayer introPlayer;
    private MediaPlayer loopPlayer;

    public IntroScene() {

        StackPane root = new StackPane();

        Rectangle background = new Rectangle(1422, 800);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);

        scene = new Scene(root, 1422, 800);

        // ---------------- INTRO VIDEO ----------------
        Media introMedia = new Media(
                getClass().getResource("/welcomescene/intro.mp4").toExternalForm()
        );

        introPlayer = new MediaPlayer(introMedia);
        MediaView mediaView = new MediaView(introPlayer);

        mediaView.setFitWidth(1422);
        mediaView.setFitHeight(800);
        mediaView.setVisible(false);

        root.getChildren().add(mediaView);

        // ---------------- TEXT ----------------
        Label infoText = new Label("Press F to Start");

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

        // ---------------- KEY LISTENER ----------------
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F) {
                stopAll();
                GameController.getInstance().switchScene(
                        new ChongsamVideoScene().getScene()
                );
            }
        });

        // ---------------- INTRO READY ----------------
        introPlayer.setOnReady(() -> {

            mediaView.setOpacity(0);
            mediaView.setVisible(true);
            introPlayer.play();

            FadeTransition fade = new FadeTransition(Duration.seconds(1), mediaView);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        });

        // ---------------- INTRO END → LOOP VIDEO ----------------
        introPlayer.setOnEndOfMedia(() -> {
            playLoop(mediaView);
        });
    }

    private void playLoop(MediaView mediaView) {

        Media loopMedia = new Media(
                getClass().getResource("/welcomescene/loopstart.mp4").toExternalForm()
        );

        loopPlayer = new MediaPlayer(loopMedia);
        loopPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 🔥 loop forever

        mediaView.setMediaPlayer(loopPlayer);
        loopPlayer.play();
    }

    private void stopAll() {
        if (introPlayer != null) introPlayer.stop();
        if (loopPlayer != null) loopPlayer.stop();
    }

    public Scene getScene() {
        return scene;
    }
}