package welcomescene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Reporter {

    private ImageView view;
    private Timeline speakingAnimation;

    public Reporter(String basePath, double x, double y) {

        view = new ImageView(new Image(getClass().getResourceAsStream(basePath + "1.png")));
        view.setLayoutX(x);
        view.setLayoutY(y);

        Image[] frames = new Image[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new Image(getClass().getResourceAsStream(basePath + (i+1) + ".png"));
        }

        speakingAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), e -> view.setImage(frames[0])),
                new KeyFrame(Duration.millis(150), e -> view.setImage(frames[1])),
                new KeyFrame(Duration.millis(300), e -> view.setImage(frames[2])),
                new KeyFrame(Duration.millis(450), e -> view.setImage(frames[3]))
        );

        speakingAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    public void startTalking() {
        speakingAnimation.play();
    }

    public void stopTalking() {
        speakingAnimation.stop();
    }

    public ImageView getView() {
        return view;
    }
}