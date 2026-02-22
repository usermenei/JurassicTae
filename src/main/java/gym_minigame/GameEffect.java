package gym_minigame;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class GameEffect {

    // 🔥 Image cache (โหลดครั้งเดียวต่อ resource)
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    private final Image effectImage;
    private final double size;
    private final double durationSeconds;

    public GameEffect(String resourcePath, double size, double durationSeconds) {
        this.size = size;
        this.durationSeconds = durationSeconds;

        // โหลดครั้งเดียว ถ้ามีแล้วใช้ของเดิม
        if (!IMAGE_CACHE.containsKey(resourcePath)) {
            IMAGE_CACHE.put(
                    resourcePath,
                    new Image(GameEffect.class.getResourceAsStream(resourcePath))
            );
        }

        this.effectImage = IMAGE_CACHE.get(resourcePath);
    }

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

        double laneLeft = laneStartX;
        double laneRight = laneStartX + laneWidth;

        double randomX;

        do {
            randomX = Math.random() * (sceneWidth - size);
        }
        while (randomX > laneLeft - size && randomX < laneRight);

        double randomY = Math.random() * (sceneHeight - size);

        effectView.setLayoutX(randomX);
        effectView.setLayoutY(randomY);

        root.getChildren().add(effectView);

        PauseTransition delay =
                new PauseTransition(Duration.seconds(durationSeconds));

        delay.setOnFinished(e -> root.getChildren().remove(effectView));
        delay.play();
    }
}