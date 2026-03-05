package gamemode.gym.note;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the lifecycle of all active {@link Note}s in the gym rhythm mini-game.
 * <p>
 * Responsibilities include spawning new notes onto the scene, moving all notes
 * downward each frame, removing notes that fall off screen (applying a miss penalty),
 * and checking whether a player key press successfully hits a note in a given lane.
 * </p>
 */
public class NoteManager {

    /** The pane that notes are added to and removed from for rendering. */
    private final Pane root;

    /** The screen height in pixels, used to detect when notes fall off screen. */
    private final int height;

    /** All notes currently active on screen. */
    private final List<Note> notes = new ArrayList<>();

    /**
     * Constructs a {@code NoteManager} for the given pane and screen height.
     *
     * @param root   the {@link Pane} that note image views are added to and removed from
     * @param height the screen height in pixels, used to cull off-screen notes
     */
    public NoteManager(Pane root, int height) {
        this.root = root;
        this.height = height;
    }

    /**
     * Spawns a new note in the given lane and adds it to the scene.
     * The note starts just above the top of the screen.
     *
     * @param lane     the lane index to spawn the note in (0 = left, 1 = right)
     * @param img      the image to display for the note
     * @param noteSize the width and height of the note in pixels
     * @param startX   the base X offset from which lane positions are calculated
     */
    public void spawnNote(int lane, Image img, int noteSize, int startX) {
        Note note = new Note(img, lane, noteSize, startX);
        notes.add(note);
        root.getChildren().add(note.getView());
    }

    /**
     * Moves all active notes downward by the given speed and removes any that
     * have fallen below the screen.
     * <p>
     * Each note that goes off screen contributes a penalty of 30 points to the
     * returned total, representing a missed note.
     * </p>
     *
     * @param speed the number of pixels to move each note downward this frame
     * @return the total miss penalty accumulated from all off-screen notes this frame
     */
    public int updateNotes(double speed) {

        List<Note> toRemove = new ArrayList<>();
        int penalty = 0;

        for (Note note : notes) {
            note.update(speed);

            if (note.isOutOfScreen(height)) {
                toRemove.add(note);
                penalty += 30;
            }
        }

        for (Note note : toRemove) {
            root.getChildren().remove(note.getView());
        }

        notes.removeAll(toRemove);

        return penalty;
    }

    /**
     * Checks whether a hittable note exists in the given lane and removes it if so.
     * <p>
     * Only the first matching note found is consumed per call. A note is considered
     * hittable if it is in the specified lane and its bottom edge is within 70 pixels
     * of {@code judgmentY}.
     * </p>
     *
     * @param lane       the lane index to check (0 = left, 1 = right)
     * @param judgmentY  the Y coordinate of the judgment line
     * @return {@code true} if a hittable note was found and removed; {@code false} otherwise
     */
    public boolean checkHit(int lane, double judgmentY) {

        Note hitNote = null;

        for (Note note : notes) {
            if (note.getLane() == lane && note.isHittable(judgmentY)) {
                hitNote = note;
                break;
            }
        }

        if (hitNote != null) {
            root.getChildren().remove(hitNote.getView());
            notes.remove(hitNote);
            return true;
        }

        return false;
    }
}