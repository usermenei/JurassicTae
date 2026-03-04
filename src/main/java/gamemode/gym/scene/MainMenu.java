package gamemode.gym.scene;

import gamemode.lobby.logic.GameLogic;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.util.Objects;

public class MainMenu {

    private static final int GYM_FEE = 500;

    private final Scene scene;

    public MainMenu(int width, int height, int prevScore,
                    Runnable onStart, Runnable onExit) {

        StackPane root = new StackPane();

        // ================= BACKGROUND =================
        Image bgImage = new Image(
                Objects.requireNonNull(
                        getClass().getResource("/gamemode/gym/gymmenu.gif")
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

        StackPane titleWrapper = new StackPane(title);
        titleWrapper.setPadding(new Insets(30, 200, 30, 200));
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

        // ================= FEE =================
        Text feeText = new Text("Fee: " + GYM_FEE + " $");
        feeText.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 28));
        feeText.setFill(Color.DARKRED);
        feeText.setEffect(new DropShadow(10, Color.BLACK));

        // ================= BUTTONS =================
        Button startBtn = createButton("START GAME", () -> {
            int playerMoney = GameLogic.getInstance().getPlayer().getMoney();

            if (playerMoney < GYM_FEE) {
                showInsufficientFunds(root);
            } else {
                GameLogic.getInstance().getPlayer()
                        .setMoney(playerMoney - GYM_FEE);
                onStart.run();
            }
        });

        Button exitBtn = createButton("EXIT", onExit);

        centerBox.getChildren().addAll(
                titleWrapper,
                lastScore,
                instruction,
                feeText,
                startBtn,
                exitBtn
        );

        root.getChildren().addAll(bgView, centerBox);

        scene = new Scene(root, width, height);
    }

    // ================= INSUFFICIENT FUNDS POPUP =================

    private void showInsufficientFunds(StackPane root) {

        // Popup box
        VBox popup = new VBox(16);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(30, 50, 30, 50));
        popup.setMaxWidth(480);
        popup.setMaxHeight(200);
        popup.setBackground(new Background(
                new BackgroundFill(Color.rgb(20, 20, 20, 0.95),
                        new CornerRadii(16), null)
        ));
        popup.setStyle("""
            -fx-border-color: #f44336;
            -fx-border-width: 3;
            -fx-border-radius: 16;
        """);

        Text msg = new Text("Insufficient Funds!");
        msg.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 32));
        msg.setFill(Color.web("#f44336"));
        msg.setEffect(new DropShadow(8, Color.BLACK));

        int needed = GYM_FEE - GameLogic.getInstance().getPlayer().getMoney();
        Text sub = new Text("You need " + needed + " $ more.");
        sub.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 20));
        sub.setFill(Color.LIGHTGRAY);

        popup.getChildren().addAll(msg, sub);

        // Dim overlay
        StackPane overlay = new StackPane(popup);
        overlay.setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 0, 0.55), null, null)
        ));

        root.getChildren().add(overlay);

        // Auto-dismiss after 2 seconds
        PauseTransition dismiss = new PauseTransition(Duration.seconds(2));
        dismiss.setOnFinished(e -> root.getChildren().remove(overlay));
        dismiss.play();
    }

    // ================= BUTTON FACTORY =================

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