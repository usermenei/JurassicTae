package gamemode.gym_minigame.scene;

import gamemode.gym_minigame.ui.GameUI;
import gamemode.gym_minigame.input.InputHandler;
import gamemode.gym_minigame.core.GameLoop;
import gamemode.gym_minigame.note.HitController;
import gamemode.gym_minigame.note.NoteManager;
import gamemode.gym_minigame.system.ComboManager;
import gamemode.gym_minigame.core.GameTimer;
import gamemode.gym_minigame.system.GameEffect;
import gamemode.gym_minigame.system.ScoreManager;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class GameScene {

    private final GameUI ui;
    private final GameLoop gameLoop;
    private final GameTimer timer;
    private final ScoreManager scoreManager;
    private final Consumer<Integer> onBackToMenu;

    public GameScene(int width, int height, int noteSize, int startX,
                     Image red, Image blue, Image backgroundImage,
                     Consumer<Integer> onBack) {

        this.onBackToMenu = onBack;

        // 1. Initialize Visuals
        this.ui = new GameUI(width, height, noteSize, startX, backgroundImage, this::exitGame);
        GameEffect hitEffect = new GameEffect("/hit.gif", 150, 3);
        GameEffect missEffect = new GameEffect("/miss.gif", 150, 3);

        // 2. Initialize State Managers
        NoteManager noteManager = new NoteManager(ui.getGameLayer(), height);
        this.scoreManager = new ScoreManager(ui.getScoreText());
        ComboManager comboManager = new ComboManager(ui.getComboText(), ui.getMultiplierText());
        this.timer = new GameTimer(60, ui.getTimerText(), this::handleGameOver);

        // 3. Initialize Controllers & Loops
        HitController hitController = new HitController(ui, noteManager, comboManager, scoreManager, hitEffect, noteSize, startX);
        this.gameLoop = new GameLoop(ui, noteManager, comboManager, scoreManager, missEffect, noteSize, startX, red, blue);

        // 4. Wire Inputs
        InputHandler.setup(
                ui.getScene(),
                () -> hitController.handleKeyPress(0),
                () -> hitController.handleKeyPress(1),
                this::exitGame
        );
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

    public Scene getScene() {
        return ui.getScene();
    }
}