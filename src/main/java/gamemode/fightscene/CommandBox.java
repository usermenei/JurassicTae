package gamemode.fightscene;

import gamemode.lobby.Item.Base.Potion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class CommandBox extends VBox {

    private final Button fightBtn;
    private final Button bagBtn;
    private final Button catchBtn;
    private final Button escapeBtn;

    private final GridPane buttons;
    private final VBox dynamicBox;
    private final StackPane centerStack;

    private final Label messageLabel;

    public CommandBox(String promptText) {

        setPrefHeight(200);
        setMinHeight(200);
        setMaxHeight(200);

        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #2b2b2b;");

        /* ===== PROMPT ===== */

        Label promptLabel = new Label(promptText);
        promptLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        /* ===== BUTTON GRID ===== */

        buttons = new GridPane();
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

        /* ===== DYNAMIC MENU ===== */

        dynamicBox = new VBox();
        dynamicBox.setSpacing(10);
        dynamicBox.setAlignment(Pos.CENTER_LEFT);
        dynamicBox.setVisible(false);

        /* ===== STACK ===== */

        centerStack = new StackPane(buttons, dynamicBox);
        centerStack.setAlignment(Pos.CENTER_LEFT);

        /* ===== MESSAGE ===== */

        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        /* ===== ADD EVERYTHING ===== */

        getChildren().addAll(promptLabel, centerStack, messageLabel);
    }

    /* ===============================
       POTION MENU
       =============================== */

    public void showPotionMenu(List<Potion> potions, Consumer<Potion> onSelect) {

        buttons.setVisible(false);
        dynamicBox.setVisible(true);
        dynamicBox.getChildren().clear();

        Label title = new Label("Choose Potion:");
        title.setStyle("-fx-text-fill: white;");
        dynamicBox.getChildren().add(title);

        for (Potion potion : potions) {

            Button btn = createButton(potion.getName());

            btn.setOnAction(e -> {
                clearDynamicMenu();
                onSelect.accept(potion);
            });

            dynamicBox.getChildren().add(btn);
        }

        Button back = createButton("BACK");
        back.setOnAction(e -> clearDynamicMenu());

        dynamicBox.getChildren().add(back);
    }

    public void clearDynamicMenu() {
        dynamicBox.setVisible(false);
        buttons.setVisible(true);
    }

    /* ===============================
       BUTTON STYLE
       =============================== */

    private Button createButton(String text) {
        Button b = new Button(text);
        b.setPrefSize(140, 45);

        b.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-text-fill: white;
            -fx-border-color: #9e9e9e;
            -fx-border-width: 2;
            -fx-background-radius: 6;
            -fx-border-radius: 6;
        """);

        return b;
    }

    /* ===============================
       GETTERS
       =============================== */

    public Button getFightButton() { return fightBtn; }
    public Button getBagButton() { return bagBtn; }
    public Button getCatchButton() { return catchBtn; }
    public Button getEscapeButton() { return escapeBtn; }

    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }
}