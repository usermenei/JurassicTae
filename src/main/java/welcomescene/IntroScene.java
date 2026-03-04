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
import javafx.scene.text.Font;

/**
 * The opening/splash screen of <b>Jurassic Tae</b>.
 *
 * <p>The intro sequence works in three stages:
 * <ol>
 *   <li><b>Intro video</b> – plays {@code /welcomescene/intro.mp4} once with a
 *       fade-in effect.</li>
 *   <li><b>Loop video</b> – when the intro finishes, seamlessly switches to
 *       {@code /welcomescene/loopstart.mp4} which repeats indefinitely.</li>
 *   <li><b>Start prompt</b> – a blinking "Press F to Start" label is shown
 *       throughout. Pressing {@link KeyCode#F} stops all media and transitions
 *       to {@link ChongsamVideoScene}.</li>
 * </ol>
 *
 * <p>Usage:
 * <pre>{@code
 * stage.setScene(new IntroScene().getScene());
 * }</pre>
 *
 * @see ChongsamVideoScene
 * @see GameController#switchScene(javafx.scene.Scene)
 */
public class IntroScene {

    /** The JavaFX {@link Scene} that hosts all intro UI elements. */
    private Scene scene;

    /** {@link MediaPlayer} for the one-shot intro video. */
    private MediaPlayer introPlayer;

    /**
     * {@link MediaPlayer} for the indefinitely looping background video
     * that plays after the intro finishes.
     */
    private MediaPlayer loopPlayer;

    /**
     * Guard flag that prevents the "Press F" handler from firing more than once
     * while the scene-transition animation is already in progress.
     */
    private boolean ending = false;

    /**
     * Constructs and fully initialises the intro scene.
     *
     * <p>The constructor:
     * <ul>
     *   <li>Builds the scene graph (black background → video view → text label)</li>
     *   <li>Attaches a blinking {@link FadeTransition} to the "Press F" label</li>
     *   <li>Registers a key listener that triggers the game start on {@link KeyCode#F}</li>
     *   <li>Starts the intro video as soon as its media is ready</li>
     *   <li>Chains the loop video to begin automatically when the intro ends</li>
     * </ul>
     */
    public IntroScene() {

        StackPane root = new StackPane();

        // Solid black background shown before the video is ready
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
        mediaView.setVisible(false); // hidden until media is ready to avoid black flash

        root.getChildren().add(mediaView);

        // ---------------- TEXT ----------------
        Label infoText = new Label("Press F to Start");

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
        FadeTransition blink = new FadeTransition(Duration.seconds(0.8), infoText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();

        // ---------------- KEY LISTENER ----------------
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F && !ending) {
                this.ending = true;
                stopAll();
                GameController.getInstance().switchScene(
                        new ChongsamVideoScene().getScene()
                );
            }
        });

        // ---------------- INTRO READY ----------------
        // Fade the video in smoothly once the media player has buffered enough data
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
        // When the intro video finishes, swap to the looping background video
        introPlayer.setOnEndOfMedia(() -> playLoop(mediaView));
    }

    /**
     * Swaps the {@link MediaView} to a new looping {@link MediaPlayer} and starts
     * playback immediately.
     *
     * <p>The loop video ({@code /welcomescene/loopstart.mp4}) repeats indefinitely
     * via {@link MediaPlayer#INDEFINITE} until the player presses F or closes the
     * window.
     *
     * @param mediaView the {@link MediaView} whose media player will be replaced
     *                  with the loop player
     */
    private void playLoop(MediaView mediaView) {

        Media loopMedia = new Media(
                getClass().getResource("/welcomescene/loopstart.mp4").toExternalForm()
        );

        loopPlayer = new MediaPlayer(loopMedia);
        loopPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop forever

        mediaView.setMediaPlayer(loopPlayer);
        loopPlayer.play();
    }

    /**
     * Stops both the intro and loop media players if they are currently running.
     *
     * <p>Called when the player presses F to transition away from this scene,
     * ensuring no audio or video continues playing in the background.
     */
    private void stopAll() {
        if (introPlayer != null) introPlayer.stop();
        if (loopPlayer  != null) loopPlayer.stop();
    }

    /**
     * Returns the {@link Scene} that contains all intro UI elements.
     *
     * <p>Pass this to {@link javafx.stage.Stage#setScene(Scene)} or
     * {@link GameController#switchScene(Scene)} to display the intro screen.
     *
     * @return the fully initialised intro {@link Scene}
     */
    public Scene getScene() {
        return scene;
    }
}