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

    private static final int ITEMS_PER_PAGE = 2;

    private final Button fightBtn;
    private final Button bagBtn;
    private final Button catchBtn;
    private final Button escapeBtn;

    private final GridPane buttons;
    private final VBox dynamicBox;
    private final StackPane centerStack;

    private final Label messageLabel;

    private int currentPage = 0;
    private List<Potion> currentPotions;

    public CommandBox(String promptText) {

        setPrefHeight(200);
        setMinHeight(200);
        setMaxHeight(200);

        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #2b2b2b;");

        Label promptLabel = new Label(promptText);
        promptLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

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

        dynamicBox = new VBox();
        dynamicBox.setSpacing(8);
        dynamicBox.setAlignment(Pos.CENTER_LEFT);
        dynamicBox.setVisible(false);

        centerStack = new StackPane(buttons, dynamicBox);
        centerStack.setAlignment(Pos.CENTER_LEFT);

        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: white;");

        getChildren().addAll(promptLabel, centerStack, messageLabel);
    }

    /* ===============================
       PAGINATED POTION MENU
       =============================== */

    public void showPotionMenu(List<Potion> potions, Consumer<Potion> onSelect) {

        this.currentPotions = potions;
        this.currentPage = 0;

        buttons.setVisible(false);
        dynamicBox.setVisible(true);

        renderPage(onSelect);
    }

    private void renderPage(Consumer<Potion> onSelect) {

        dynamicBox.getChildren().clear();

        int totalPages = (int) Math.ceil((double) currentPotions.size() / ITEMS_PER_PAGE);

        Label title = new Label("Choose Potion (Page " + (currentPage + 1) + "/" + totalPages + ")");
        title.setStyle("-fx-text-fill: white;");
        dynamicBox.getChildren().add(title);

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, currentPotions.size());

        for (int i = start; i < end; i++) {

            Potion potion = currentPotions.get(i);
            Button btn = createButton(potion.getName());

            btn.setOnAction(e -> {
                clearDynamicMenu();
                onSelect.accept(potion);
            });

            dynamicBox.getChildren().add(btn);
        }

        /* ===== Pagination Controls ===== */

        HBox nav = new HBox(10);
        nav.setAlignment(Pos.CENTER_LEFT);

        Button prev = createButton("◀ PREV");
        Button next = createButton("NEXT ▶");
        Button back = createButton("BACK");

        prev.setDisable(currentPage == 0);
        next.setDisable(currentPage >= totalPages - 1);

        prev.setOnAction(e -> {
            currentPage--;
            renderPage(onSelect);
        });

        next.setOnAction(e -> {
            currentPage++;
            renderPage(onSelect);
        });

        back.setOnAction(e -> clearDynamicMenu());

        nav.getChildren().addAll(prev, next, back);
        dynamicBox.getChildren().add(nav);
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
        b.setPrefSize(140, 40);
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