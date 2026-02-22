package gamemode.gym_minigame.note;

import gamemode.gym_minigame.system.GameEffect;
import gamemode.gym_minigame.ui.GameUI;
import gamemode.gym_minigame.system.ComboManager;
import gamemode.gym_minigame.system.ScoreManager;

public class HitController {

    private final GameUI ui;
    private final NoteManager noteManager;
    private final ComboManager comboManager;
    private final ScoreManager scoreManager;
    private final GameEffect hitEffect;

    private final int noteSize;
    private final int startX;

    public HitController(GameUI ui, NoteManager noteManager, ComboManager comboManager,
                         ScoreManager scoreManager, GameEffect hitEffect,
                         int noteSize, int startX) {
        this.ui = ui;
        this.noteManager = noteManager;
        this.comboManager = comboManager;
        this.scoreManager = scoreManager;
        this.hitEffect = hitEffect;
        this.noteSize = noteSize;
        this.startX = startX;
    }

    public void handleKeyPress(int lane) {
        ui.showJudgmentHit();
        boolean hit = noteManager.checkHit(lane, 800);

        if (hit) {
            comboManager.addCombo();
            scoreManager.addScore(100, comboManager.getMultiplier());
            ui.animateCombo();

            hitEffect.playRandomNoLane(ui.getGameLayer(), ui.getScene().getWidth(), ui.getScene().getHeight(), startX, noteSize * 2);
        }
    }
}