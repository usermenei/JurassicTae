package gamemode.lobby.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimationPlayer {

    private Image[] frames;
    private int currentFrame = 0;

    private long lastFrameTime = 0;
    private long frameDelay;

    private boolean loop = true;

    public AnimationPlayer(Image[] frames, long frameDelay) {
        this.frames = frames;
        this.frameDelay = frameDelay;
    }

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

    public void render(GraphicsContext gc, double x, double y,
                       double width, double height, boolean facingRight) {

        if (facingRight) {
            gc.drawImage(frames[currentFrame], x, y, width, height);
        } else {
            // Flip horizontally using negative width
            gc.drawImage(frames[currentFrame], x + width, y, -width, height);
        }
    }

    public void reset() {
        currentFrame = 0;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}