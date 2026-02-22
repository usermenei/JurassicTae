package gym_minigame.utility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameTimer {

    private int time;
    private final Text timerText;
    private final Runnable onFinish;
    private Timeline timeline;

    public GameTimer(int seconds, Text timerText, Runnable onFinish) {
        this.time = seconds;
        this.timerText = timerText;
        this.onFinish = onFinish;
        updateText();
    }

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

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void updateText() {
        timerText.setText("Time: " + time);
    }
}