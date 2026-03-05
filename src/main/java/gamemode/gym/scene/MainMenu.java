package gamemode.gym.scene;

import gamemode.lobby.Player.Player;
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

/**
 * The main menu scene for the gym rhythm mini-game.
 * <p>
 * Displays the game title, the player's previous score, key binding instructions,
 * the entry fee, and START / EXIT buttons. Pressing START deducts {@link #GYM_FEE}
 * from the player's money and invokes the start callback. If the player cannot
 * afford the fee, a temporary insufficient-funds popup is shown instead.
 * </p>
 */
public class MainMenu {

    /** The gold cost deducted from the player's balance when starting a gym session. */
    private static final int GYM_FEE = 500;

    /** The JavaFX scene containing the full menu layout. */
    private final Scene scene;

    /**
     * Constructs the gym {@code MainMenu} scene.
     * <p>
     * Builds and lays out all UI elements: animated background, title, previous
     * score display, key binding hint, fee label, and START / EXIT buttons.
     * </p>
     *
     * @param width     the width of the scene in pixels
     * @param height    the height of the scene in pixels
     * @param prevScore the player's score from their last gym session, displayed on screen
     * @param onStart   callback invoked when the player successfully pays the fee and starts
     * @param onExit    callback invoked when the player presses EXIT
     */
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
            Player player = GameLogic.getInstance().getPlayer();

            if (player.getMoney() < GYM_FEE) {
                showInsufficientFunds(root);
            } else {
                GameLogic.getInstance().getPlayer()
                        .setMoney(player.getMoney() - GYM_FEE);
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

    /**
     * Displays a temporary insufficient-funds popup over the menu.
     * <p>
     * The popup shows how much additional gold the player needs and is
     * automatically dismissed after 2 seconds via a {@link PauseTransition}.
     * </p>
     *
     * @param root the root {@link StackPane} to add the popup overlay to
     */
    private void showInsufficientFunds(StackPane root) {

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

        StackPane overlay = new StackPane(popup);
        overlay.setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 0, 0.55), null, null)
        ));

        root.getChildren().add(overlay);

        PauseTransition dismiss = new PauseTransition(Duration.seconds(2));
        dismiss.setOnFinished(e -> root.getChildren().remove(overlay));
        dismiss.play();
    }

    /**
     * Creates a styled menu button with the given label and click action.
     * <p>
     * All buttons share the same font, dimensions, and white background style.
     * </p>
     *
     * @param text   the label displayed on the button
     * @param action the {@link Runnable} invoked when the button is clicked
     * @return a configured {@link Button} instance
     */
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

    /**
     * Returns the JavaFX {@link Scene} for this main menu.
     *
     * @return the scene instance
     */
    public Scene getScene() {
        return scene;
    }
}