package gym_minigame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;

public class MainMenu {

    private final Scene scene;

    public MainMenu(int width, int height, int prevScore,
                    Runnable onStart, Runnable onExit) {

        StackPane root = new StackPane();

        // ================= BACKGROUND =================
        Image bgImage = new Image(
                Objects.requireNonNull(
                        getClass().getResource("/gymmenu.gif")
                ).toExternalForm()
        );

        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(width);
        bgView.setFitHeight(height);
        bgView.setPreserveRatio(false);

        // ================= CENTER LAYOUT =================
        VBox centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);

        // ================= TITLE =================
        Text title = new Text("JurassicTae_Gym");
        title.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 90));
        title.setFill(Color.WHITE);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);
        title.setEffect(glow);

        // Black horizontal bar behind title
        StackPane titleWrapper = new StackPane(title);
        titleWrapper.setPadding(new Insets(30, 200, 30, 200)); // extend horizontal
        titleWrapper.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(0, 0, 0, 0.75),
                        new CornerRadii(25),
                        null
                )
        ));

        // ================= PREVIOUS SCORE =================
        Text lastScore = new Text("Previous Score: " + prevScore);
        lastScore.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 35));
        lastScore.setFill(Color.CYAN);

        lastScore.setEffect(new DropShadow(10, Color.BLACK));

        // ================= INSTRUCTION =================
        Text instruction = new Text("Press D / F to Play");
        instruction.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 28));
        instruction.setFill(Color.LIGHTGREEN);

        instruction.setEffect(new DropShadow(10, Color.BLACK));

        // ================= BUTTONS =================
        Button startBtn = createButton("START GAME", onStart);
        Button exitBtn = createButton("EXIT", onExit);

        centerBox.getChildren().addAll(
                titleWrapper,
                lastScore,
                instruction,
                startBtn,
                exitBtn
        );

        root.getChildren().addAll(bgView, centerBox);

        scene = new Scene(root, width, height);
    }

    private Button createButton(String text, Runnable action) {
        Button btn = new Button(text);

        btn.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 30));

        btn.setPrefWidth(320);
        btn.setPrefHeight(60);

        btn.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(10), null)
        ));

        btn.setStyle("-fx-text-fill: black;");

        btn.setOnAction(e -> action.run());

        return btn;
    }

    public Scene getScene() {
        return scene;
    }
}