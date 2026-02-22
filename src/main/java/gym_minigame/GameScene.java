package gym_minigame;

import gym_minigame.note.NoteManager;
import gym_minigame.utility.ComboManager;
import gym_minigame.utility.GameTimer;
import gym_minigame.utility.InputHandler;
import gym_minigame.utility.ScoreManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class GameScene {

    private final Scene scene;
    private final Image backgroundImage;

    private final GameEffect hitEffect;
    private final GameEffect missEffect;
    private final Pane root;

    private final NoteManager noteManager;
    private final ScoreManager scoreManager;
    private final GameTimer timer;

    private final AnimationTimer gameLoop;
    private long lastSpawn = 0;

    private final int noteSize;
    private final int startX;
    private final Image red;
    private final Image blue;

    private final Rectangle judgmentLine;
    private final Consumer<Integer> onBackToMenu;
    private final ComboManager comboManager;

    private final Text comboText;
    private final Text multiplierText;

    public GameScene(int width,
                     int height,
                     int noteSize,
                     int startX,
                     Image red,
                     Image blue,
                     Image backgroundImage,
                     Consumer<Integer> onBack) {

        this.noteSize = noteSize;
        this.startX = startX;
        this.red = red;
        this.blue = blue;
        this.onBackToMenu = onBack;
        this.backgroundImage = backgroundImage;

        StackPane stackRoot = new StackPane();
        Pane gameLayer = new Pane();
        Pane overlayLayer = new Pane();

        stackRoot.getChildren().addAll(gameLayer, overlayLayer);
        this.root = gameLayer;

        root.setPrefSize(width, height);

        BackgroundSize bgSize = new BackgroundSize(
                width, height, false, false, false, false
        );

        BackgroundImage bg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );

        root.setBackground(new Background(bg));

        hitEffect = new GameEffect("/hit.gif", 150, 3);
        missEffect = new GameEffect("/miss.gif", 150, 3);

        // ================= UI SETUP =================

        // ================= LANE =================

        double laneWidth = noteSize * 2;
        double laneX = startX;
        double laneTop = 0;

        double laneHeight = height;

        Rectangle laneBg = new Rectangle();
        laneBg.setX(laneX);
        laneBg.setY(laneTop);
        laneBg.setWidth(laneWidth);
        laneBg.setHeight(laneHeight);
        laneBg.setFill(Color.web("#121212"));
        laneBg.setOpacity(0.85);

        root.getChildren().add(laneBg);

        // ================= JUDGMENT LINE =================

        // place it 120px above bottom dynamically
        double judgmentY = height - 120;

        judgmentLine = new Rectangle();
        judgmentLine.setX(laneX);
        judgmentLine.setY(judgmentY);
        judgmentLine.setWidth(laneWidth);
        judgmentLine.setHeight(8);
        judgmentLine.setFill(Color.WHITE);
        judgmentLine.setOpacity(1);

        // ================= COMBO =================

        comboText = new Text("Combo : 0");
        comboText.setFont(loadFont(40));
        comboText.setFill(Color.CYAN);

        multiplierText = new Text("x1.00");
        multiplierText.setFont(loadFont(30));
        multiplierText.setFill(Color.YELLOW);

        overlayLayer.getChildren().addAll(comboText, multiplierText);

        // Position BELOW judgment line
        double comboY = judgmentLine.getY() + 60;
        comboText.setLayoutY(comboY);
        multiplierText.setLayoutY(comboY + 40);

        // Center horizontally in lane (dynamic when text changes)
        double laneCenter = startX + noteSize;

        comboText.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            comboText.setLayoutX(laneCenter - newVal.getWidth() / 2);
        });

        multiplierText.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            multiplierText.setLayoutX(laneCenter - newVal.getWidth() / 2);
        });

        // Force correct centering on first frame
        Platform.runLater(() -> {
            comboText.setLayoutX(laneCenter - comboText.getLayoutBounds().getWidth() / 2);
            multiplierText.setLayoutX(laneCenter - multiplierText.getLayoutBounds().getWidth() / 2);
        });

        comboManager = new ComboManager(comboText, multiplierText);
        // ================= SCORE =================

        Text scoreText = new Text(100, 150, "Score: 0");
        scoreText.setFill(Color.RED);
        scoreText.setFont(loadFont(50));

        // ================= TIMER =================

        Text timerText = new Text(100, 220, "Time: 60");
        timerText.setFill(Color.YELLOW);
        timerText.setFont(loadFont(40));

        // ================= BACK BUTTON =================

        Button backBtn = new Button("Back");
        backBtn.setFont(loadFont(25));
        backBtn.setFocusTraversable(false);
        backBtn.setLayoutX(100);
        backBtn.setLayoutY(300);
        backBtn.setOnAction(e -> exitGame());

        overlayLayer.getChildren().addAll(
                judgmentLine,
                scoreText,
                timerText,
                backBtn
        );

        scene = new Scene(stackRoot);

        // ================= MANAGERS =================

        noteManager = new NoteManager(root, height);
        scoreManager = new ScoreManager(scoreText);
        timer = new GameTimer(60, timerText, this::handleGameOver);

        // ================= INPUT =================

        InputHandler.setup(
                scene,
                () -> handleKeyPress(0),
                () -> handleKeyPress(1),
                this::exitGame
        );

        // ================= GAME LOOP =================

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                fadeJudgmentLine();
                spawnLogic(now);

                int penalty = noteManager.updateNotes(10);

                if (penalty > 0) {

                    comboManager.resetCombo();
                    scoreManager.subtractScore(penalty);

                    missEffect.playRandomNoLane(
                            root,
                            scene.getWidth(),
                            scene.getHeight(),
                            startX,
                            noteSize * 2
                    );
                }
            }
        };
    }

    // ================= GAME CONTROL =================

    public void start() {
        gameLoop.start();
        timer.start();
    }

    private void handleGameOver() {
        stopAll();
        onBackToMenu.accept(scoreManager.getScore());
    }

    private void exitGame() {
        stopAll();
        onBackToMenu.accept(scoreManager.getScore());
    }

    private void stopAll() {
        gameLoop.stop();
        timer.stop();
    }

    // ================= GAME LOGIC =================

    private void spawnLogic(long now) {
        if (now - lastSpawn > 500_000_000) {
            int lane = (int) (Math.random() * 2);
            Image noteImg = (lane == 0) ? blue : red;
            noteManager.spawnNote(lane, noteImg, noteSize, startX);
            lastSpawn = now;
        }
    }

    private void handleKeyPress(int lane) {

        judgmentLine.setOpacity(1.0);

        boolean hit = noteManager.checkHit(lane, noteSize, 800);

        if (hit) {
            comboManager.addCombo();
            scoreManager.addScore(100, comboManager.getMultiplier());

            animateCombo();

            hitEffect.playRandomNoLane(
                    root,
                    scene.getWidth(),
                    scene.getHeight(),
                    startX,
                    noteSize * 2
            );
        }
    }

    private void fadeJudgmentLine() {
        if (judgmentLine.getOpacity() > 0.3) {
            judgmentLine.setOpacity(judgmentLine.getOpacity() - 0.05);
        }
    }

    private Font loadFont(int size) {
        return Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),
                size
        );
    }

    public Scene getScene() {
        return scene;
    }

    private void animateCombo() {

        javafx.animation.ScaleTransition scale =
                new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(120),
                        comboText
                );

        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
}