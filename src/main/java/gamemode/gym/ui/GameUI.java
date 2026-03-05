package gamemode.gym.ui;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Builds and manages all visual elements for the gym rhythm mini-game.
 * <p>
 * The UI is composed of two stacked layers:
 * </p>
 * <ul>
 *   <li><b>Game layer</b> — the background, lane backdrop, and falling notes
 *       (notes are added and removed here by {@link gamemode.gym.note.NoteManager}).</li>
 *   <li><b>Overlay layer</b> — the judgment line, score, timer, combo, multiplier,
 *       and back button, drawn on top of the game layer every frame.</li>
 * </ul>
 * <p>
 * Combo and multiplier text are horizontally centered over the lane center
 * and update dynamically as their layout bounds change.
 * </p>
 */
public class GameUI {

    /** The JavaFX scene containing the full game UI. */
    private final Scene scene;

    /** The pane where the background and falling notes are rendered. */
    private final Pane gameLayer;

    /**
     * The horizontal white bar drawn at the judgment position.
     * Flashes to full opacity on a hit and fades gradually each frame.
     */
    private final Rectangle judgmentLine;

    /** Displays the current consecutive hit count. */
    private final Text comboText;

    /** Displays the current score multiplier derived from the combo. */
    private final Text multiplierText;

    /** Displays the player's current score. */
    private final Text scoreText;

    /** Displays the remaining time in the session. */
    private final Text timerText;
    /** Displays the fonts in the session. */
    private static Font pixelFont;

    /**
     * Constructs the {@code GameUI}, laying out all visual elements and
     * wiring the back button to the provided callback.
     *
     * @param width           the width of the game canvas in pixels
     * @param height          the height of the game canvas in pixels
     * @param noteSize        the size of each note in pixels, used to calculate lane width
     * @param startX          the X coordinate where the note lane begins
     * @param backgroundImage the image displayed as the game background
     * @param onBack          the action invoked when the back button is pressed
     */
    public GameUI(int width, int height, int noteSize, int startX,
                  Image backgroundImage, Runnable onBack) {

        StackPane stackRoot = new StackPane();
        this.gameLayer = new Pane();
        Pane overlayLayer = new Pane();

        stackRoot.getChildren().addAll(gameLayer, overlayLayer);
        gameLayer.setPrefSize(width, height);

        setupBackground(backgroundImage, width, height);

        // ================= LANE =================
        double laneWidth = noteSize * 2;
        Rectangle laneBg = new Rectangle(startX, 0, laneWidth, height);
        laneBg.setFill(Color.web("#121212"));
        laneBg.setOpacity(0.85);
        gameLayer.getChildren().add(laneBg);

        // ================= JUDGMENT LINE =================
        judgmentLine = new Rectangle(startX, height - 120, laneWidth, 8);
        judgmentLine.setFill(Color.WHITE);

        // ================= COMBO & MULTIPLIER =================
        comboText = new Text("Combo : 0");
        comboText.setFont(loadFont(40));
        comboText.setFill(Color.CYAN);

        multiplierText = new Text("x1.00");
        multiplierText.setFont(loadFont(30));
        multiplierText.setFill(Color.YELLOW);

        double comboY = judgmentLine.getY() + 60;
        comboText.setLayoutY(comboY);
        multiplierText.setLayoutY(comboY + 40);

        double laneCenter = startX + noteSize;
        centerText(comboText, laneCenter);
        centerText(multiplierText, laneCenter);

        // ================= SCORE & TIMER =================
        scoreText = new Text(100, 150, "Score: 0");
        scoreText.setFill(Color.RED);
        scoreText.setFont(loadFont(50));

        timerText = new Text(100, 220, "Time: 60");
        timerText.setFill(Color.YELLOW);
        timerText.setFont(loadFont(40));

        // ================= BACK BUTTON =================
        Button backBtn = new Button("Back");
        backBtn.setFont(loadFont(25));
        backBtn.setFocusTraversable(false);
        backBtn.setLayoutX(100);
        backBtn.setLayoutY(300);
        backBtn.setOnAction(e -> onBack.run());

        overlayLayer.getChildren().addAll(
                judgmentLine, scoreText, timerText, backBtn, comboText, multiplierText);

        this.scene = new Scene(stackRoot);
    }

    /**
     * Applies the given image as the background of the game layer, stretched
     * to exactly fit the specified dimensions with no repeat.
     *
     * @param backgroundImage the image to use as the background
     * @param width           the target width in pixels
     * @param height          the target height in pixels
     */
    private void setupBackground(Image backgroundImage, int width, int height) {
        BackgroundSize bgSize = new BackgroundSize(width, height, false, false, false, false);
        BackgroundImage bg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );
        gameLayer.setBackground(new Background(bg));
    }

    /**
     * Horizontally centers a {@link Text} node around the given X coordinate.
     * <p>
     * A listener on the node's layout bounds ensures it stays centered if the
     * text content changes. An initial centering is also applied immediately
     * via {@link Platform#runLater}.
     * </p>
     *
     * @param textNode the text node to center
     * @param centerX  the X coordinate to center the text around
     */
    private void centerText(Text textNode, double centerX) {
        textNode.layoutBoundsProperty().addListener((obs, oldVal, newVal) ->
                textNode.setLayoutX(centerX - newVal.getWidth() / 2));
        Platform.runLater(() ->
                textNode.setLayoutX(centerX - textNode.getLayoutBounds().getWidth() / 2));
    }

    /**
     * Loads the pixel font at the given size from the bundled font resource.
     *
     * @param size the font size in points
     * @return the loaded {@link Font}, or the default font if loading fails
     */
    private Font loadFont(int size) {
        if (pixelFont == null) {
            pixelFont = Font.loadFont(getClass().getResourceAsStream("/fonts/pixel.ttf"), size);
        }
        return Font.font(pixelFont.getFamily(), size);
    }

    /**
     * Plays a brief scale-up-and-back animation on the combo text node,
     * providing visual feedback on a successful hit.
     * The text pulses to 130% of its original size over 120 milliseconds.
     */
    public void animateCombo() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(120), comboText);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    /**
     * Flashes the judgment line to full opacity.
     * Should be called each time the player presses a lane key.
     */
    public void showJudgmentHit() {
        judgmentLine.setOpacity(1.0);
    }

    /**
     * Gradually reduces the judgment line's opacity by 0.05 per call,
     * down to a minimum of 0.3. Should be called once per frame to
     * produce a smooth fade-out after a hit.
     */
    public void fadeJudgmentLine() {
        if (judgmentLine.getOpacity() > 0.3) {
            judgmentLine.setOpacity(judgmentLine.getOpacity() - 0.05);
        }
    }

    /**
     * Returns the JavaFX {@link Scene} containing the full game UI.
     *
     * @return the scene instance
     */
    public Scene getScene() { return scene; }

    /**
     * Returns the game layer pane where falling notes are rendered.
     *
     * @return the game layer {@link Pane}
     */
    public Pane getGameLayer() { return gameLayer; }

    /**
     * Returns the text node displaying the current combo count.
     *
     * @return the combo {@link Text} node
     */
    public Text getComboText() { return comboText; }

    /**
     * Returns the text node displaying the current score multiplier.
     *
     * @return the multiplier {@link Text} node
     */
    public Text getMultiplierText() { return multiplierText; }

    /**
     * Returns the text node displaying the current score.
     *
     * @return the score {@link Text} node
     */
    public Text getScoreText() { return scoreText; }

    /**
     * Returns the text node displaying the remaining session time.
     *
     * @return the timer {@link Text} node
     */
    public Text getTimerText() { return timerText; }
}