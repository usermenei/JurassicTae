package gamemode.gym_minigame.ui;

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

public class GameUI {

    private final Scene scene;
    private final Pane gameLayer;
    private final Rectangle judgmentLine;
    private final Text comboText;
    private final Text multiplierText;
    private final Text scoreText;
    private final Text timerText;

    public GameUI(int width, int height, int noteSize, int startX, Image backgroundImage, Runnable onBack) {
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

        overlayLayer.getChildren().addAll(judgmentLine, scoreText, timerText, backBtn, comboText, multiplierText);

        this.scene = new Scene(stackRoot);
    }

    private void setupBackground(Image backgroundImage, int width, int height) {
        BackgroundSize bgSize = new BackgroundSize(width, height, false, false, false, false);
        BackgroundImage bg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        gameLayer.setBackground(new Background(bg));
    }

    private void centerText(Text textNode, double centerX) {
        textNode.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            textNode.setLayoutX(centerX - newVal.getWidth() / 2);
        });
        Platform.runLater(() -> textNode.setLayoutX(centerX - textNode.getLayoutBounds().getWidth() / 2));
    }

    private Font loadFont(int size) {
        return Font.loadFont(getClass().getResourceAsStream("/fonts/pixel.ttf"), size);
    }

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

    public void showJudgmentHit() {
        judgmentLine.setOpacity(1.0);
    }

    public void fadeJudgmentLine() {
        if (judgmentLine.getOpacity() > 0.3) {
            judgmentLine.setOpacity(judgmentLine.getOpacity() - 0.05);
        }
    }

    // Getters
    public Scene getScene() { return scene; }
    public Pane getGameLayer() { return gameLayer; }
    public Text getComboText() { return comboText; }
    public Text getMultiplierText() { return multiplierText; }
    public Text getScoreText() { return scoreText; }
    public Text getTimerText() { return timerText; }
}