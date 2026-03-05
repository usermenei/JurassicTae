package gamemode.gym.core;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The {@code GameTimer} class manages a countdown timer for the gym mini-game.
 *
 * <p>This timer decreases the remaining time every second and updates a
 * {@link Text} UI element to display the current time. When the countdown
 * reaches zero, a specified callback function is executed.
 *
 * <p>The timer uses a {@link Timeline} with a {@link KeyFrame} that triggers
 * once per second to update the countdown.
 *
 * <p>Typical usage:
 * <pre>
 * GameTimer timer = new GameTimer(30, timerText, () -> endGame());
 * timer.start();
 * </pre>
 *
 * The timer will automatically stop and execute the {@code onFinish}
 * action when the countdown reaches zero.
 */
public class GameTimer {

    /** Remaining time in seconds. */
    private int time;

    /** Text element used to display the remaining time on the UI. */
    private final Text timerText;

    /** Callback executed when the timer reaches zero. */
    private final Runnable onFinish;

    /** Timeline responsible for triggering the countdown updates. */
    private Timeline timeline;

    /**
     * Constructs a new {@code GameTimer}.
     *
     * @param seconds the starting time in seconds
     * @param timerText the UI text element used to display the time
     * @param onFinish a callback function that executes when the timer ends
     */
    public GameTimer(int seconds, Text timerText, Runnable onFinish) {
        this.time = seconds;
        this.timerText = timerText;
        this.onFinish = onFinish;
        updateText();
    }

    /**
     * Starts the countdown timer.
     *
     * <p>The timer decreases the remaining time every second.
     * When the remaining time reaches zero, the timer stops
     * and executes the {@code onFinish} callback.
     */
    public void start() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    time--;
                    updateText();

                    if (time <= 0) {
                        timeline.stop();
                        onFinish.run();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the timer if it is currently running.
     *
     * <p>This method safely stops the {@link Timeline} to prevent
     * further countdown updates.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Updates the UI text element with the current remaining time.
     *
     * <p>The displayed format is:
     * <pre>
     * Time: X
     * </pre>
     * where {@code X} represents the remaining seconds.
     */
    private void updateText() {
        timerText.setText("Time: " + time);
    }
}