package gamemode.forest.fightscene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CommandBox extends VBox {

    private final Label promptLabel;   // left
    private final Label messageLabel;  // right

    private final Button fightBtn;
    private final Button bagBtn;
    private final Button catchBtn;
    private final Button escapeBtn;

    public CommandBox(String promptText) {

        setMinHeight(180);
        setPadding(new Insets(20));
        setSpacing(12);
        setStyle("-fx-background-color: #2b2b2b;");

        /* ===== TOP TEXT ROW ===== */
        promptLabel = new Label(promptText);
        promptLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        VBox leftCol = new VBox();
        leftCol.setAlignment(Pos.CENTER_LEFT);
        leftCol.setSpacing(10);

        /* ===== BUTTONS ===== */
        GridPane buttons = new GridPane();
        buttons.setHgap(15);
        buttons.setVgap(15);

        fightBtn = createButton("FIGHT");
        bagBtn = createButton("BAG");
        catchBtn = createButton("CATCH");
        escapeBtn = createButton("ESCAPE");

        buttons.add(fightBtn, 0, 0);
        buttons.add(bagBtn, 1, 0);
        buttons.add(catchBtn, 0, 1);
        buttons.add(escapeBtn, 1, 1);

        leftCol.getChildren().addAll(promptLabel, buttons);

        HBox panel = new HBox();
        panel.setSpacing(10);

        // push messageLabel to the right
        Region spacer = new Region();
        spacer.setPrefWidth(100);
        HBox.setHgrow(spacer, Priority.NEVER);

        panel.getChildren().addAll(leftCol, spacer, messageLabel);

        getChildren().add(panel);
    }

    private Button createButton(String text) {
        Button b = new Button(text);
        b.setPrefSize(140, 45);

        String normal = """
            -fx-background-color: #3a3a3a;
            -fx-text-fill: white;
            -fx-border-color: #9e9e9e;
            -fx-border-width: 2;
            -fx-background-radius: 6;
            -fx-border-radius: 6;
        """;

        String hover = """
            -fx-background-color: #555555;
            -fx-text-fill: white;
            -fx-border-color: white;
            -fx-border-width: 2;
            -fx-background-radius: 6;
            -fx-border-radius: 6;
        """;

        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(normal));

        return b;
    }

    /* ===== getters ===== */
    public Button getFightButton() { return fightBtn; }
    public Button getBagButton() { return bagBtn; }
    public Button getCatchButton() { return catchBtn; }
    public Button getEscapeButton() { return escapeBtn; }

    /* ===== message control ===== */
    public void setMessage(String text) {
        messageLabel.setText(text);
    }

    public void clearMessage() {
        messageLabel.setText("");
    }
}