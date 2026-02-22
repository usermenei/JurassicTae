package gamemode.gym.core;

import gamemode.gym.system.GameEffect;
import gamemode.gym.ui.GameUI;
import gamemode.gym.note.NoteManager;
import gamemode.gym.system.ComboManager;
import gamemode.gym.system.ScoreManager;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

public class GameLoop extends AnimationTimer {

    private final GameUI ui;
    private final NoteManager noteManager;
    private final ComboManager comboManager;
    private final ScoreManager scoreManager;
    private final GameEffect missEffect;

    private final int noteSize;
    private final int startX;
    private final Image red;
    private final Image blue;

    private long lastSpawn = 0;

    public GameLoop(GameUI ui, NoteManager noteManager, ComboManager comboManager,
                    ScoreManager scoreManager, GameEffect missEffect,
                    int noteSize, int startX, Image red, Image blue) {
        this.ui = ui;
        this.noteManager = noteManager;
        this.comboManager = comboManager;
        this.scoreManager = scoreManager;
        this.missEffect = missEffect;
        this.noteSize = noteSize;
        this.startX = startX;
        this.red = red;
        this.blue = blue;
    }

    @Override
    public void handle(long now) {
        ui.fadeJudgmentLine();
        spawnLogic(now);

        int penalty = noteManager.updateNotes(10);

        if (penalty > 0) {
            comboManager.resetCombo();
            scoreManager.subtractScore(penalty);
            missEffect.playRandomNoLane(ui.getGameLayer(), ui.getScene().getWidth(), ui.getScene().getHeight(), startX, noteSize * 2);
        }
    }

    private void spawnLogic(long now) {
        if (now - lastSpawn > 500_000_000) {
            int lane = (int) (Math.random() * 2);
            Image noteImg = (lane == 0) ? blue : red;
            noteManager.spawnNote(lane, noteImg, noteSize, startX);
            lastSpawn = now;
        }
    }
}