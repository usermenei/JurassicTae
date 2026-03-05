package gamemode.gym.system;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Plays a temporary visual effect (GIF or image) at a random position on screen,
 * avoiding the active note lane area.
 * <p>
 * Effect images are loaded once per resource path and cached statically, so
 * multiple {@code GameEffect} instances sharing the same path reuse the same
 * {@link Image} object. Each call to {@link #playRandomNoLane} creates a new
 * {@link ImageView}, adds it to the given pane, and removes it automatically
 * after {@code durationSeconds}.
 * </p>
 */
public class GameEffect {

    /**
     * Shared image cache keyed by resource path.
     * Ensures each effect image is loaded from disk only once.
     */
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    /** The effect image to display, retrieved from or stored in {@link #IMAGE_CACHE}. */
    private final Image effectImage;

    /** The width of the effect image view in pixels (height is scaled to preserve ratio). */
    private final double size;

    /** How long the effect remains visible on screen, in seconds. */
    private final double durationSeconds;

    /**
     * Constructs a {@code GameEffect} for the given image resource.
     * <p>
     * If the image at {@code resourcePath} has not been loaded before, it is
     * loaded and stored in the static cache. Subsequent instances using the
     * same path reuse the cached image.
     * </p>
     *
     * @param resourcePath    the classpath-relative path to the effect image or GIF
     * @param size            the display width of the effect in pixels
     * @param durationSeconds how many seconds the effect stays visible before being removed
     */
    public GameEffect(String resourcePath, double size, double durationSeconds) {
        this.size = size;
        this.durationSeconds = durationSeconds;

        if (!IMAGE_CACHE.containsKey(resourcePath)) {
            IMAGE_CACHE.put(
                    resourcePath,
                    new Image(GameEffect.class.getResourceAsStream(resourcePath))
            );
        }

        this.effectImage = IMAGE_CACHE.get(resourcePath);
    }

    /**
     * Plays the effect at a random position on screen, excluding the note lane area.
     * <p>
     * The effect is placed at a uniformly random X position that does not overlap
     * the lane region ({@code laneStartX} to {@code laneStartX + laneWidth}), and
     * at a uniformly random Y position within the scene height. It is automatically
     * removed from the pane after {@link #durationSeconds} seconds.
     * </p>
     *
     * @param root       the {@link Pane} to add the effect view to
     * @param sceneWidth the total width of the scene in pixels, used to bound random X
     * @param sceneHeight the total height of the scene in pixels, used to bound random Y
     * @param laneStartX the X coordinate where the note lane begins, used to define the exclusion zone
     * @param laneWidth  the width of the note lane exclusion zone in pixels
     */
    public void playRandomNoLane(
            Pane root,
            double sceneWidth,
            double sceneHeight,
            double laneStartX,
            double laneWidth
    ) {
        ImageView effectView = new ImageView(effectImage);
        effectView.setFitWidth(size);
        effectView.setPreserveRatio(true);

        double laneLeft  = laneStartX;
        double laneRight = laneStartX + laneWidth;

        double randomX;
        do {
            randomX = Math.random() * (sceneWidth - size);
        } while (randomX > laneLeft - size && randomX < laneRight);

        double randomY = Math.random() * (sceneHeight - size);

        effectView.setLayoutX(randomX);
        effectView.setLayoutY(randomY);

        root.getChildren().add(effectView);

        PauseTransition delay = new PauseTransition(Duration.seconds(durationSeconds));
        delay.setOnFinished(e -> root.getChildren().remove(effectView));
        delay.play();
    }
}