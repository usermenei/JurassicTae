package gamemode.gym.note;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    private final Pane root;
    private final int height;
    private final List<Note> notes = new ArrayList<>();

    public NoteManager(Pane root, int height) {
        this.root = root;
        this.height = height;
    }

    public void spawnNote(int lane, Image img, int noteSize, int startX) {

        Note note = new Note(img, lane, noteSize, startX);

        notes.add(note);
        root.getChildren().add(note.getView());
    }

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