package gamemode.lobby.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * <h2>AnimationPlayer</h2>
 *
 * <p>
 * Handles frame-based sprite animation for player rendering.
 * This class manages switching between animation frames
 * based on a specified time delay.
 * </p>
 *
 * <p>
 * It supports looping and non-looping animations,
 * as well as horizontal flipping for character direction.
 * </p>
 *
 * <p>
 * Typically used in the lobby or exploration scenes
 * to animate player movement.
 * </p>
 *
 * @author Pongtawan
 * @version 1.0
 * @since 2026
 */
public class AnimationPlayer {

    private Image[] frames;
    private int currentFrame = 0;

    private long lastFrameTime = 0;
    private long frameDelay;

    private boolean loop = true;

    /**
     * Constructs a new AnimationPlayer.
     *
     * @param frames array of animation frames
     * @param frameDelay delay between frame transitions (in milliseconds)
     */
    public AnimationPlayer(Image[] frames, long frameDelay) {
        this.frames = frames;
        this.frameDelay = frameDelay;
    }

    /**
     * Updates the animation frame based on elapsed time.
     *
     * <p>
     * If the time since the last frame update exceeds
     * the configured frame delay, the animation advances
     * to the next frame.
     * </p>
     *
     * <p>
     * If looping is enabled, the animation restarts
     * from the first frame when it reaches the end.
     * Otherwise, it stays on the last frame.
     * </p>
     */
    public void update() {
        long now = System.currentTimeMillis();

        if (now - lastFrameTime > frameDelay) {
            currentFrame++;

            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                }
            }

            lastFrameTime = now;
        }
    }

    /**
     * Renders the current animation frame on the screen.
     *
     * @param gc graphics context used for drawing
     * @param x x-coordinate of the sprite
     * @param y y-coordinate of the sprite
     * @param width width of the rendered sprite
     * @param height height of the rendered sprite
     * @param facingRight true if the sprite faces right,
     *                    false if it should be flipped horizontally
     */
    public void render(GraphicsContext gc, double x, double y,
                       double width, double height, boolean facingRight) {

        if (facingRight) {
            gc.drawImage(frames[currentFrame], x, y, width, height);
        } else {
            // Flip horizontally using negative width
            gc.drawImage(frames[currentFrame], x + width, y, -width, height);
        }
    }

    /**
     * Resets the animation to the first frame.
     */
    public void reset() {
        currentFrame = 0;
    }

    /**
     * Enables or disables animation looping.
     *
     * @param loop true to enable looping,
     *             false to stop at the last frame
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}