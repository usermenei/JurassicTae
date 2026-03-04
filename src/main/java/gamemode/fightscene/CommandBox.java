package gamemode.fightscene;

import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.Weapon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class CommandBox extends StackPane {

    private static final int ITEMS_PER_PAGE = 2;

    private final GridPane mainButtons;
    private final VBox dynamicMenu;
    private final Label messageLabel;

    private final Button fightBtn;
    private final Button bagBtn;
    private final Button catchBtn;
    private final Button escapeBtn;

    private int currentPage = 0;
    private List<Potion> currentPotions;
    private List<Weapon> currentWeapons;

    private final String playerName;
    private Font pixelFont;

    public CommandBox(String playerName) {

        this.playerName = playerName;

        setMinHeight(220);
        setPrefHeight(220);
        setMaxHeight(220);
        setPadding(new Insets(12));
        setStyle("""
            -fx-background-color: #2c3e50;
            -fx-border-color: #d4af37;
            -fx-border-width: 5;
        """);

        loadFont();

        // ===== MESSAGE LABEL =====
        messageLabel = new Label("What will " + playerName + " do?");
        messageLabel.setFont(pixelFont);
        messageLabel.setStyle("-fx-text-fill: white;");
        messageLabel.setAlignment(Pos.CENTER_LEFT);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setPadding(new Insets(0, 0, 6, 0));

        // ===== MAIN BUTTONS (2x2 grid) =====
        mainButtons = new GridPane();
        mainButtons.setHgap(16);
        mainButtons.setVgap(12);
        mainButtons.setAlignment(Pos.CENTER);

        fightBtn  = createButton("FIGHT");
        bagBtn    = createButton("BAG");
        catchBtn  = createButton("CATCH");
        escapeBtn = createButton("ESCAPE");

        mainButtons.add(fightBtn,  0, 0);
        mainButtons.add(bagBtn,    1, 0);
        mainButtons.add(catchBtn,  0, 1);
        mainButtons.add(escapeBtn, 1, 1);

        // ===== DYNAMIC MENU =====
        dynamicMenu = new VBox(10);
        dynamicMenu.setAlignment(Pos.CENTER);
        dynamicMenu.setVisible(false);
        dynamicMenu.setManaged(false); // won't take space when hidden

        // ===== CONTAINER =====
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.setFillWidth(true);
        container.getChildren().addAll(messageLabel, mainButtons, dynamicMenu);

        // Make container fill the StackPane
        StackPane.setAlignment(container, Pos.TOP_LEFT);
        getChildren().add(container);

        // ===== BUTTON ACTIONS =====
        fightBtn.setOnAction(e -> setMessage("Choose a weapon."));
        bagBtn.setOnAction(e -> setMessage("Open your bag."));
        catchBtn.setOnAction(e -> setMessage("Try to catch it!"));
        escapeBtn.setOnAction(e -> setMessage("Attempting to escape..."));
    }

    // ===================================================

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/pixel.ttf");
            pixelFont = (is != null) ? Font.loadFont(is, 18) : null;
        } catch (Exception ignored) {}
        if (pixelFont == null) {
            pixelFont = Font.font("Monospaced", 18);
        }
    }

    // ================= WEAPON MENU =================

    public void showWeaponMenu(List<Weapon> weapons, Consumer<Weapon> onSelect) {
        this.currentWeapons = weapons;
        this.currentPage = 0;
        mainButtons.setVisible(false);
        mainButtons.setManaged(false);
        dynamicMenu.setVisible(true);
        dynamicMenu.setManaged(true);
        renderWeaponPage(onSelect);
    }

    private void renderWeaponPage(Consumer<Weapon> onSelect) {
        dynamicMenu.getChildren().clear();

        int totalItems  = (currentWeapons == null ? 0 : currentWeapons.size()) + 1;
        int totalPages  = Math.max(1, (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE));

        setMessage("Choose Weapon (" + (currentPage + 1) + "/" + totalPages + ")");

        int start = currentPage * ITEMS_PER_PAGE;
        int end   = Math.min(start + ITEMS_PER_PAGE, totalItems);

        for (int i = start; i < end; i++) {
            Button btn;
            if (i == 0) {
                btn = createButton("Bare Hand");
                btn.setOnAction(e -> { clearMenu(); onSelect.accept(null); });
            } else {
                Weapon w = currentWeapons.get(i - 1);
                btn = createButton(w.getName());
                btn.setOnAction(e -> { clearMenu(); onSelect.accept(w); });
            }
            dynamicMenu.getChildren().add(btn);
        }

        dynamicMenu.getChildren().add(createNav(totalPages, () -> renderWeaponPage(onSelect)));
    }

    // ================= POTION MENU =================

    public void showPotionMenu(List<Potion> potions, Consumer<Potion> onSelect) {
        this.currentPotions = potions;
        this.currentPage = 0;
        mainButtons.setVisible(false);
        mainButtons.setManaged(false);
        dynamicMenu.setVisible(true);
        dynamicMenu.setManaged(true);
        renderPotionPage(onSelect);
    }

    private void renderPotionPage(Consumer<Potion> onSelect) {
        dynamicMenu.getChildren().clear();

        if (currentPotions == null || currentPotions.isEmpty()) {
            setMessage("No potions available.");
            Button back = createButton("BACK");
            back.setOnAction(e -> clearMenu());
            dynamicMenu.getChildren().add(back);
            return;
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) currentPotions.size() / ITEMS_PER_PAGE));
        setMessage("Choose Potion (" + (currentPage + 1) + "/" + totalPages + ")");

        int start = currentPage * ITEMS_PER_PAGE;
        int end   = Math.min(start + ITEMS_PER_PAGE, currentPotions.size());

        for (int i = start; i < end; i++) {
            Potion p = currentPotions.get(i);
            Button btn = createButton(p.getName());
            btn.setOnAction(e -> { clearMenu(); onSelect.accept(p); });
            dynamicMenu.getChildren().add(btn);
        }

        dynamicMenu.getChildren().add(createNav(totalPages, () -> renderPotionPage(onSelect)));
    }

    // ================= NAVIGATION =================

    private HBox createNav(int totalPages, Runnable refresh) {
        HBox nav = new HBox(12);
        nav.setAlignment(Pos.CENTER);

        Button prev = createButton("PREV");
        Button next = createButton("NEXT");
        Button back = createButton("BACK");

        prev.setDisable(currentPage == 0);
        next.setDisable(currentPage >= totalPages - 1);

        prev.setOnAction(e -> { currentPage--; refresh.run(); });
        next.setOnAction(e -> { currentPage++; refresh.run(); });
        back.setOnAction(e -> clearMenu());

        nav.getChildren().addAll(prev, next, back);
        return nav;
    }

    // ================= UTIL =================

    private Button createButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(160);  // was 220 — too wide, caused overflow
        b.setPrefHeight(42);
        b.setFont(pixelFont);
        b.setStyle("""
            -fx-background-color: #34495e;
            -fx-text-fill: white;
            -fx-border-color: #d4af37;
            -fx-border-width: 2;
            -fx-cursor: hand;
        """);
        b.setOnMouseEntered(e -> b.setStyle("""
            -fx-background-color: #4a6278;
            -fx-text-fill: #d4af37;
            -fx-border-color: #d4af37;
            -fx-border-width: 2;
            -fx-cursor: hand;
        """));
        b.setOnMouseExited(e -> b.setStyle("""
            -fx-background-color: #34495e;
            -fx-text-fill: white;
            -fx-border-color: #d4af37;
            -fx-border-width: 2;
            -fx-cursor: hand;
        """));
        return b;
    }

    private void clearMenu() {
        dynamicMenu.setVisible(false);
        dynamicMenu.setManaged(false);
        mainButtons.setVisible(true);
        mainButtons.setManaged(true);
        setMessage("What will " + playerName + " do?");
    }

    public void setMessage(String msg) { messageLabel.setText(msg); }

    public Button getFightButton()  { return fightBtn;  }
    public Button getBagButton()    { return bagBtn;    }
    public Button getCatchButton()  { return catchBtn;  }
    public Button getEscapeButton() { return escapeBtn; }
}