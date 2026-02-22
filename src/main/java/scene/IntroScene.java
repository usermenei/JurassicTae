package scene;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import spawnscreen.logic.GameController;

public class IntroScene {

    private Scene scene;
    private MediaPlayer introPlayer;
    private MediaPlayer loopPlayer;

    public IntroScene() {

        // ---------------- ROOT ----------------
        StackPane root = new StackPane();

        // กันหน้าขาว
        Rectangle background = new Rectangle(1422, 800);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);

        scene = new Scene(root, 1422, 800);

        // ---------------- INTRO VIDEO ----------------
        Media introMedia = new Media(
                getClass().getResource("/intro.mp4").toExternalForm()
        );

        introPlayer = new MediaPlayer(introMedia);
        MediaView mediaView = new MediaView(introPlayer);

        mediaView.setFitWidth(1422);
        mediaView.setFitHeight(800);

        // ซ่อนไว้ก่อนจนกว่าจะ READY
        mediaView.setVisible(false);

        root.getChildren().add(mediaView);

        // รอ video โหลดเสร็จ
        introPlayer.setOnReady(() -> {

            mediaView.setOpacity(0);
            mediaView.setVisible(true);
            introPlayer.play();

            // Fade in แบบเนียน ๆ
            FadeTransition fade = new FadeTransition(Duration.seconds(1), mediaView);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        });

        // เมื่อ intro จบ → ไป loop
        introPlayer.setOnEndOfMedia(() -> playLoop(root, mediaView));
    }

    private void playLoop(StackPane root, MediaView mediaView) {

        // ---------------- LOOP VIDEO ----------------
        Media loopMedia = new Media(
                getClass().getResource("/loopstart.mp4").toExternalForm()
        );

        loopPlayer = new MediaPlayer(loopMedia);
        loopPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        mediaView.setMediaPlayer(loopPlayer);

        loopPlayer.setOnReady(() -> loopPlayer.play());

        // ---------------- START BUTTON ----------------
        Button startBtn = new Button("START");
        startBtn.setStyle("""
                -fx-font-size: 28px;
                -fx-padding: 15 50;
                -fx-background-color: rgba(0,0,0,0.6);
                -fx-text-fill: white;
                -fx-background-radius: 10;
                """);

        root.getChildren().add(startBtn);

        startBtn.setOnAction(e -> {

            loopPlayer.stop();

            // เปลี่ยนไป Dialogue Scene
            GameController.getInstance().switchScene(
                    new DialogueScene().getScene()
            );
        });
    }

    public Scene getScene() {
        return scene;
    }
}