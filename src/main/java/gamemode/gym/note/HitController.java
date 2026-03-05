package gamemode.gym.note;

import gamemode.gym.system.GameEffect;
import gamemode.gym.ui.GameUI;
import gamemode.gym.system.ComboManager;
import gamemode.gym.system.ScoreManager;

/**
 * Handles player key press events during the gym rhythm mini-game.
 * <p>
 * When a lane key is pressed, this controller checks whether a note in that
 * lane is within hit range of the judgment line. A successful hit increments
 * the combo, adds to the score, animates the combo display, and plays a
 * visual hit effect. Both hits and misses show a judgment indicator on the UI.
 * </p>
 */
public class HitController {

    /** The game UI used to show judgment feedback and animate the combo display. */
    private final GameUI ui;

    /** Manages active notes and determines whether a hit was successful. */
    private final NoteManager noteManager;

    /** Tracks the current combo count and score multiplier. */
    private final ComboManager comboManager;

    /** Tracks and updates the player's score. */
    private final ScoreManager scoreManager;

    /** Plays a visual particle or animation effect on a successful hit. */
    private final GameEffect hitEffect;

    /** The size of each note in pixels, used to position the hit effect. */
    private final int noteSize;

    /** The base X offset from which lane positions are calculated. */
    private final int startX;

    /**
     * Constructs a {@code HitController} with all required game subsystem references.
     *
     * @param ui           the {@link GameUI} for displaying judgment and combo animations
     * @param noteManager  the {@link NoteManager} to query for hittable notes
     * @param comboManager the {@link ComboManager} to update on a successful hit
     * @param scoreManager the {@link ScoreManager} to update on a successful hit
     * @param hitEffect    the {@link GameEffect} to play on a successful hit
     * @param noteSize     the width and height of each note in pixels
     * @param startX       the base X offset used to position the hit effect
     */
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

    /**
     * Processes a key press for the given lane.
     * <p>
     * Always shows a judgment indicator on the UI. If a note in the specified
     * lane is within 70 pixels of the judgment line at Y=800, the hit is
     * registered: the combo and score are updated, the combo display is animated,
     * and a random hit effect is played on the game layer.
     * </p>
     *
     * @param lane the lane index that was pressed (0 = left, 1 = right)
     */
    public void handleKeyPress(int lane) {
        ui.showJudgmentHit();
        boolean hit = noteManager.checkHit(lane, 800);

        if (hit) {
            comboManager.addCombo();
            scoreManager.addScore(100, comboManager.getMultiplier());
            ui.animateCombo();
            hitEffect.playRandomNoLane(
                    ui.getGameLayer(),
                    ui.getScene().getWidth(),
                    ui.getScene().getHeight(),
                    startX,
                    noteSize * 2
            );
        }
    }
}