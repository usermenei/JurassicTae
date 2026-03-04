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

/**
 * A cinematic cut-scene that plays the Chongsam introduction video
 * ({@code /welcomescene/chongsamintro.mp4}) as part of the game's
 * opening sequence.
 *
 * <p>This scene sits between {@link IntroScene} and {@link NewsReportScene}
 * in the intro flow:
 * <pre>
 *   IntroScene → ChongsamVideoScene → NewsReportScene → (lobby)
 * </pre>
 *
 * <p>The video plays automatically and the player may press {@link KeyCode#F}
 * at any time to skip directly to {@link NewsReportScene}. A blinking
 * "Press F to Skip" label is shown throughout.
 *
 * <p>Usage:
 * <pre>{@code
 * GameController.getInstance().switchScene(new ChongsamVideoScene().getScene());
 * }</pre>
 *
 * @see IntroScene
 * @see NewsReportScene
 * @see GameController#switchScene(javafx.scene.Scene)
 */
public class ChongsamVideoScene {

    /** The JavaFX {@link Scene} that hosts the video and skip label. */
    private Scene scene;

    /** {@link MediaPlayer} responsible for playing the Chongsam intro video. */
    private MediaPlayer player;

    /**
     * Constructs and fully initialises the Chongsam cut-scene.
     *
     * <p>Setup steps performed by the constructor:
     * <ul>
     *   <li>Creates a 1422 × 800 {@link Scene} with a {@link StackPane} root</li>
     *   <li>Loads and plays {@code /welcomescene/chongsamintro.mp4} via a
     *       {@link MediaPlayer} as soon as it is ready</li>
     *   <li>Registers an end-of-media callback that automatically advances to
     *       {@link NewsReportScene} when the video finishes</li>
     *   <li>Registers a key listener so pressing {@link KeyCode#F} skips the
     *       video and jumps straight to {@link NewsReportScene}</li>
     *   <li>Adds a blinking "Press F to Skip" label at the bottom of the screen</li>
     * </ul>
     */
    public ChongsamVideoScene() {

        StackPane root = new StackPane();
        scene = new Scene(root, 1422, 800);

        // Load the Chongsam intro video from resources
        Media media = new Media(
                getClass().getResource("/welcomescene/chongsamintro.mp4").toExternalForm()
        );

        player = new MediaPlayer(media);
        MediaView view = new MediaView(player);
        view.setFitWidth(1422);
        view.setFitHeight(800);

        root.getChildren().add(view);

        // Start playback as soon as the media is buffered and ready
        player.setOnReady(player::play);

        // Automatically advance to the News scene when the video ends
        player.setOnEndOfMedia(() -> {
            stopAll();
            GameController.getInstance().switchScene(
                    new NewsReportScene().getScene()
            );
        });

        // Allow the player to skip the video by pressing F
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F) {
                stopAll();
                GameController.getInstance().switchScene(
                        new NewsReportScene().getScene()
                );
            }
        });

        // ---------------- SKIP LABEL ----------------
        Label infoText = new Label("Press F to Skip");

        Font pixelFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),
                32
        );
        infoText.setFont(pixelFont);
        infoText.setStyle("-fx-text-fill: white;");

        StackPane.setAlignment(infoText, Pos.BOTTOM_CENTER);
        infoText.setTranslateY(-40);

        root.getChildren().add(infoText);

        // ---------------- BLINK EFFECT ----------------
        // Repeatedly fades the label between full and near-invisible opacity
        FadeTransition blink = new FadeTransition(Duration.seconds(0.8), infoText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
    }

    /**
     * Stops and disposes the {@link MediaPlayer} to release native media resources.
     *
     * <p>This must be called before switching away from this scene to prevent
     * audio/video from continuing to play in the background and to avoid
     * native memory leaks associated with undisposed media players.
     */
    private void stopAll() {
        if (player != null) {
            player.stop();
            player.dispose();
        }
    }

    /**
     * Returns the {@link Scene} containing the Chongsam cut-scene UI.
     *
     * <p>Pass this to {@link GameController#switchScene(javafx.scene.Scene)}
     * or {@link javafx.stage.Stage#setScene(javafx.scene.Scene)} to display
     * this scene.
     *
     * @return the fully initialised {@link Scene} for this cut-scene
     */
    public Scene getScene() {
        return scene;
    }
}