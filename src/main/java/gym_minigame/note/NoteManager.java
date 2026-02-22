package gym_minigame.note;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    private final Pane root;
    private final int height;
    private final List<ImageView> notes = new ArrayList<>();

    public NoteManager(Pane root, int height) {
        this.root = root;
        this.height = height;
    }

    public void spawnNote(int lane, Image img, int noteSize, int startX) {

        ImageView note = new ImageView(img);
        note.setFitWidth(noteSize);
        note.setFitHeight(noteSize);
        note.setPreserveRatio(true);

        note.setX(startX + lane * noteSize);
        note.setY(-noteSize);
        note.setUserData(lane);

        notes.add(note);
        root.getChildren().add(note);
    }

    public int updateNotes(double speed) {

        List<ImageView> toRemove = new ArrayList<>();
        int penalty = 0;

        for (ImageView note : notes) {
            note.setY(note.getY() + speed);

            if (note.getY() > height) {
                toRemove.add(note);
                penalty += 30;
            }
        }

        notes.removeAll(toRemove);
        root.getChildren().removeAll(toRemove);

        return penalty;
    }

    public boolean checkHit(int lane, int noteSize, double judgmentY) {

        ImageView hitNote = null;

        for (ImageView note : notes) {
            int noteLane = (int) note.getUserData();

            if (noteLane == lane) {
                double noteBottom = note.getY() + noteSize;
                double distance = Math.abs(noteBottom - judgmentY);

                if (distance < 70) {
                    hitNote = note;
                    break;
                }
            }
        }

        if (hitNote != null) {
            root.getChildren().remove(hitNote);
            notes.remove(hitNote);
            return true;
        }

        return false;
    }
}