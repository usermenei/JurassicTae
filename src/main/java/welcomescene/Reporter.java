package welcomescene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Represents an animated TV reporter sprite used in {@link NewsReportScene}.
 *
 * <p>A {@code Reporter} loads four sequentially numbered PNG frames from the
 * classpath and cycles through them using a {@link Timeline} to simulate a
 * talking animation. The animation plays indefinitely while the reporter is
 * speaking and stops when it is their co-anchor's turn.
 *
 * <p>Frame naming convention expected on the classpath:
 * <pre>
 *   {basePath}1.png
 *   {basePath}2.png
 *   {basePath}3.png
 *   {basePath}4.png
 * </pre>
 *
 * <p>Example usage:
 * <pre>{@code
 * Reporter reporter1 = new Reporter("/news/reporter1_", 0, 150);
 * reporter1.startTalking();  // begins the mouth animation
 * reporter1.stopTalking();   // freezes on the first frame
 * }</pre>
 *
 * @see NewsReportScene
 */
public class Reporter {

    /**
     * The {@link ImageView} that displays the reporter sprite on screen.
     * Its image is swapped each frame by {@link #speakingAnimation}.
     */
    private ImageView view;

    /**
     * A looping {@link Timeline} that cycles through the four animation
     * frames at 150 ms intervals to create the talking effect.
     */
    private Timeline speakingAnimation;

    /**
     * Constructs a {@code Reporter} by loading its sprite frames and building
     * the talking animation.
     *
     * <p>The constructor:
     * <ul>
     *   <li>Loads {@code {basePath}1.png} as the initial static image</li>
     *   <li>Loads all four frames ({@code {basePath}1.png} –
     *       {@code {basePath}4.png}) into an array</li>
     *   <li>Creates a {@link Timeline} that swaps frames every 150 ms,
     *       cycling indefinitely</li>
     *   <li>Positions the {@link ImageView} at the given {@code (x, y)}
     *       coordinates</li>
     * </ul>
     *
     * @param basePath classpath-relative prefix shared by all four frame images
     *                 (e.g. {@code "/news/reporter1_"})
     * @param x        the horizontal layout position of the sprite in pixels
     * @param y        the vertical layout position of the sprite in pixels
     */
    public Reporter(String basePath, double x, double y) {

        // Load the first frame as the default static image
        view = new ImageView(new Image(getClass().getResourceAsStream(basePath + "1.png")));
        view.setLayoutX(x);
        view.setLayoutY(y);

        // Pre-load all four animation frames
        Image[] frames = new Image[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new Image(getClass().getResourceAsStream(basePath + (i + 1) + ".png"));
        }

        // Build the talking animation — each frame shown for 150 ms
        speakingAnimation = new Timeline(
                new KeyFrame(Duration.millis(0),   e -> view.setImage(frames[0])),
                new KeyFrame(Duration.millis(150), e -> view.setImage(frames[1])),
                new KeyFrame(Duration.millis(300), e -> view.setImage(frames[2])),
                new KeyFrame(Duration.millis(450), e -> view.setImage(frames[3]))
        );

        speakingAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts the talking animation, cycling through all four frames
     * indefinitely until {@link #stopTalking()} is called.
     *
     * <p>Typically called by {@link NewsReportScene} when this reporter's
     * dialogue line is displayed.
     */
    public void startTalking() {
        speakingAnimation.play();
    }

    /**
     * Stops the talking animation, freezing the sprite on whichever frame
     * was last displayed.
     *
     * <p>Typically called by {@link NewsReportScene} when the other reporter
     * takes over speaking.
     */
    public void stopTalking() {
        speakingAnimation.stop();
    }

    /**
     * Returns the {@link ImageView} that renders this reporter's sprite.
     *
     * <p>Add this to a JavaFX layout container to make the reporter visible
     * on screen:
     * <pre>{@code
     * root.getChildren().add(reporter.getView());
     * }</pre>
     *
     * @return the {@link ImageView} displaying the reporter sprite
     */
    public ImageView getView() {
        return view;
    }
}