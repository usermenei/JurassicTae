package gamemode.gym.core;

import gamemode.gym.system.GameEffect;
import gamemode.gym.ui.GameUI;
import gamemode.gym.note.NoteManager;
import gamemode.gym.system.ComboManager;
import gamemode.gym.system.ScoreManager;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

/**
 * The {@code GameLoop} class represents the main game update loop for the gym rhythm mini-game.
 *
 * <p>This class extends {@link AnimationTimer} and is responsible for running the
 * real-time game logic on every frame. It handles note spawning, note movement updates,
 * score penalties for missed notes, combo resets, and visual effects.
 *
 * <p>The loop continuously performs the following operations:
 * <ul>
 *     <li>Updates UI animations (such as fading the judgment line)</li>
 *     <li>Spawns new notes at fixed time intervals</li>
 *     <li>Updates existing notes through the {@link NoteManager}</li>
 *     <li>Applies penalties when notes are missed</li>
 *     <li>Triggers visual miss effects</li>
 * </ul>
 *
 * <p>This class acts as the central controller connecting UI, note logic,
 * score tracking, combo management, and visual effects.
 */
public class GameLoop extends AnimationTimer {

    /** Reference to the game's UI system. */
    private final GameUI ui;

    /** Manages note spawning, movement, and removal. */
    private final NoteManager noteManager;

    /** Handles combo tracking and resets. */
    private final ComboManager comboManager;

    /** Handles score calculation and penalties. */
    private final ScoreManager scoreManager;

    /** Visual effect triggered when a note is missed. */
    private final GameEffect missEffect;

    /** Size of each note in pixels. */
    private final int noteSize;

    /** Starting X position where notes appear. */
    private final int startX;

    /** Image used for red notes. */
    private final Image red;

    /** Image used for blue notes. */
    private final Image blue;

    /** Timestamp of the last spawned note. */
    private long lastSpawn = 0;

    /**
     * Constructs the game loop responsible for updating the rhythm game state.
     *
     * @param ui the game UI controller
     * @param noteManager manager responsible for notes
     * @param comboManager manager responsible for combo tracking
     * @param scoreManager manager responsible for scoring
     * @param missEffect visual effect played when a note is missed
     * @param noteSize the size of each note in pixels
     * @param startX the horizontal spawn position of notes
     * @param red the image used for red notes
     * @param blue the image used for blue notes
     */
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

    /**
     * Called automatically every frame by {@link AnimationTimer}.
     *
     * <p>This method updates the game's runtime logic including:
     * <ul>
     *     <li>UI animations</li>
     *     <li>Note spawning logic</li>
     *     <li>Note movement updates</li>
     *     <li>Handling missed notes</li>
     * </ul>
     *
     * @param now the current timestamp in nanoseconds
     */
    @Override
    public void handle(long now) {
        ui.fadeJudgmentLine();
        spawnLogic(now);

        int penalty = noteManager.updateNotes(10);

        if (penalty > 0) {
            comboManager.resetCombo();
            scoreManager.subtractScore(penalty);
            missEffect.playRandomNoLane(
                    ui.getGameLayer(),
                    ui.getScene().getWidth(),
                    ui.getScene().getHeight(),
                    startX,
                    noteSize * 2
            );
        }
    }

    /**
     * Controls the logic for spawning new notes.
     *
     * <p>Notes are generated at a fixed interval of approximately
     * 500 milliseconds. Each spawned note randomly selects a lane
     * and assigns the corresponding note image.
     *
     * @param now the current timestamp in nanoseconds
     */
    private void spawnLogic(long now) {
        if (now - lastSpawn > 500_000_000) {
            int lane = (int) (Math.random() * 2);
            Image noteImg = (lane == 0) ? blue : red;
            noteManager.spawnNote(lane, noteImg, noteSize, startX);
            lastSpawn = now;
        }
    }
}