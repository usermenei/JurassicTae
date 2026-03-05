package gamemode.gym.note;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a single falling note in the gym rhythm mini-game.
 * <p>
 * Each note is assigned to a lane and falls downward each frame via {@link #update(double)}.
 * A note can be hit when its bottom edge is within a fixed tolerance of the judgment line,
 * and is discarded once it falls below the visible screen area.
 * </p>
 */
public class Note {

    /** The {@link ImageView} used to render this note on the canvas. */
    private final ImageView view;

    /** The lane index (0 or 1) this note belongs to. */
    private final int lane;

    /** The width and height of this note in pixels. */
    private final int size;

    /**
     * Constructs a {@code Note} and positions it just above the top of the screen.
     * <p>
     * The note's X position is derived from {@code startX + lane * size}, placing
     * each lane side by side. The note starts at {@code Y = -size} so it enters
     * the screen smoothly from the top.
     * </p>
     *
     * @param img    the image to display for this note
     * @param lane   the lane index this note belongs to (0 = left, 1 = right)
     * @param size   the width and height of the note in pixels
     * @param startX the base X offset from which lane positions are calculated
     */
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

    /**
     * Moves this note downward by the given speed for one frame.
     *
     * @param speed the number of pixels to move down this tick
     */
    public void update(double speed) {
        view.setY(view.getY() + speed);
    }

    /**
     * Returns whether this note has fallen below the visible screen area.
     *
     * @param height the height of the screen in pixels
     * @return {@code true} if the note's top edge is below the screen bottom
     */
    public boolean isOutOfScreen(int height) {
        return view.getY() > height;
    }

    /**
     * Returns whether this note is close enough to the judgment line to be hit.
     * <p>
     * A note is considered hittable when the distance between its bottom edge
     * and {@code judgmentY} is less than 70 pixels.
     * </p>
     *
     * @param judgmentY the Y coordinate of the judgment line
     * @return {@code true} if the note's bottom edge is within 70 pixels of the judgment line
     */
    public boolean isHittable(double judgmentY) {
        double noteBottom = view.getY() + size;
        double distance = Math.abs(noteBottom - judgmentY);
        return distance < 70;
    }

    /**
     * Returns the {@link ImageView} used to render this note.
     *
     * @return the note's image view
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Returns the lane index this note belongs to.
     *
     * @return the lane index (0 = left, 1 = right)
     */
    public int getLane() {
        return lane;
    }
}