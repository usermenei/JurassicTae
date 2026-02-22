package gamemode.gym.note;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Note {

    private final ImageView view;
    private final int lane;
    private final int size;

    public Note(Image img, int lane, int size, int startX) {
        this.lane = lane;
        this.size = size;

        view = new ImageView(img);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);

        view.setX(startX + lane * size);
        view.setY(-size);
    }

    public void update(double speed) {
        view.setY(view.getY() + speed);
    }

    public boolean isOutOfScreen(int height) {
        return view.getY() > height;
    }

    public boolean isHittable(double judgmentY) {
        double noteBottom = view.getY() + size;
        double distance = Math.abs(noteBottom - judgmentY);
        return distance < 70;
    }

    public ImageView getView() {
        return view;
    }

    public int getLane() {
        return lane;
    }
}