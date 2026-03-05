package gamemode.lobby.Scene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.logic.GameLogic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;

/**
 * A JavaFX {@link StackPane} that represents the sell/catalog screen in the shop UI.
 *
 * <p>This scene has two modes toggled by the "Catalog / Sell" button:
 * <ul>
 *   <li><b>Sell mode</b> — displays all {@link TamedDinosaur} items in the player's
 *       inventory as {@link ButtonSell} cards. The player can click a card to sell it.</li>
 *   <li><b>Catalog mode</b> — displays every {@link Dinosaur} that has been returned
 *       to the catalog via {@link #addCatalog(Dinosaur)}, shown as read-only info cards.</li>
 * </ul>
 *
 * <h2>Layout structure</h2>
 * <pre>
 * StackPane (SellScene)
 * └── VBox (shopBox)
 *     ├── HBox (header)  — title label + toggle button
 *     └── ScrollPane
 *         └── GridPane   — item / catalog cards (4 columns)
 * └── Button (exitBtn)   — top-right, hides the scene
 * </pre>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * SellScene scene = new SellScene();
 * scene.addCatalog(someDinosaur); // called when a TamedDinosaur is sold
 * scene.refresh();                // called to reload the sell grid
 * }</pre>
 */
public class SellScene extends StackPane {

    /** Grid that holds item sell buttons or catalog cards. */
    private GridPane gridPane;

    /** Toggle button that switches between Sell and Catalog modes. */
    private Button switchBtt;

    /**
     * Dinosaurs that have been sold and are now displayed in the catalog.
     * Populated via {@link #addCatalog(Dinosaur)}.
     */
    private ArrayList<Dinosaur> catalog;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs the {@code SellScene}, building the full UI layout:
     * <ol>
     *   <li>Initialises the empty catalog list.</li>
     *   <li>Creates the outer {@link StackPane} (700 × 530 px) and inner {@code VBox}.</li>
     *   <li>Adds a title label and a "Catalog / Sell" toggle button in a header row.</li>
     *   <li>Creates a 4-column {@link GridPane} inside a {@link ScrollPane}.</li>
     *   <li>Calls {@link #loadItems()} to populate the grid with the player's
     *       current {@link TamedDinosaur} inventory on first display.</li>
     *   <li>Adds a red "X" exit button anchored to the top-right corner that
     *       hides this scene when clicked.</li>
     * </ol>
     */
    public SellScene() {
        catalog = new ArrayList<>();

        this.setPrefSize(700, 530);
        this.setMaxSize(700, 530);
        this.setStyle("""
            -fx-padding: 30;
            -fx-background-radius: 20;
        """);

        VBox shopBox = new VBox(20);
        shopBox.setAlignment(Pos.CENTER);
        shopBox.setPrefSize(500, 350);
        shopBox.setStyle("""
            -fx-background-color: #2b2b2b;
            -fx-padding: 30;
            -fx-background-radius: 20;
        """);

        // Title
        Label title = new Label("KHAO KHEOW ZOO");
        title.setStyle("""
            -fx-background-color: #ffcc00;
            -fx-text-fill: black;
            -fx-font-family: 'Minecraft';
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-padding: 10 30 10 30;
            -fx-background-radius: 10;
        """);

        // Toggle button
        switchBtt = new Button("Catalog");
        switchBtt.setStyle("""
            -fx-background-color: #444;
            -fx-text-fill: white;
            -fx-font-family: 'Minecraft';
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-padding: 10 20 10 20;
            -fx-background-radius: 10;
        """);

        switchBtt.setOnMouseClicked(e -> {
            if (switchBtt.getText().equals("Catalog")) {
                loadCatalog();
                switchBtt.setText("Sell");
            } else {
                loadItems();
                switchBtt.setText("Catalog");
            }
        });

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setSpacing(40);
        header.setPadding(new Insets(0, 0, 10, 0));

        Region spacerLeft  = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft,  Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        header.getChildren().addAll(spacerLeft, title, switchBtt, spacerRight);

        // Grid
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.TOP_CENTER);

        loadItems();

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
            -fx-padding: 0;
        """);

        shopBox.getChildren().add(scrollPane);

        // Exit button
        Button exitBtn = new Button("X");
        exitBtn.setStyle("""
            -fx-background-color: red;
            -fx-text-fill: white;
            -fx-font-family: 'Minecraft';
            -fx-font-weight: bold;
            -fx-font-size: 18px;
        """);
        exitBtn.setOnAction(e -> this.setVisible(false));
        StackPane.setAlignment(exitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(exitBtn, new Insets(10));

        shopBox.getChildren().add(0, header);
        this.getChildren().addAll(shopBox, exitBtn);
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Refreshes the sell grid by reloading the player's current inventory.
     *
     * <p>Call this after the player's inventory has changed (e.g. after an item
     * is sold) to ensure the grid shows up-to-date content.</p>
     */
    public void refresh() {
        loadItems();
    }

    /**
     * Adds a {@link Dinosaur} to the internal catalog list.
     *
     * <p>Called automatically by {@link ButtonSell} when a {@link TamedDinosaur}
     * is sold, so the underlying dinosaur becomes visible in Catalog mode.</p>
     *
     * @param dinosaur the {@link Dinosaur} to add; must not be {@code null}
     */
    public void addCatalog(Dinosaur dinosaur) {
        catalog.add(dinosaur);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Clears the grid and repopulates it with {@link ButtonSell} cards for every
     * {@link TamedDinosaur} in the player's inventory.
     *
     * <p>Items that are not instances of {@link TamedDinosaur} are skipped.
     * Cards are arranged in rows of 4 columns.</p>
     */
    private void loadItems() {
        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {
            if (!(item instanceof TamedDinosaur)) continue;

            ButtonSell btn = new ButtonSell(item);
            gridPane.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Clears the grid and repopulates it with read-only info cards for every
     * {@link Dinosaur} currently in {@link #catalog}.
     *
     * <p>Each card shows the dinosaur's sprite and name. Hovering applies a
     * gold border highlight. Cards are arranged in rows of 4 columns.</p>
     */
    private void loadCatalog() {
        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Dinosaur dinosaur : catalog) {
            VBox content = new VBox(5);
            content.setAlignment(Pos.CENTER);

            Image     img       = dinosaur.getSprite();
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            imageView.setPreserveRatio(true);

            Label nameLabel = new Label(dinosaur.getName());
            nameLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
            """);

            content.setStyle("""
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 12;
                -fx-border-color: #777;
                -fx-border-radius: 12;
                -fx-padding: 10;
            """);

            content.setOnMouseEntered(e -> content.setStyle("""
                -fx-background-color: #444;
                -fx-background-radius: 12;
                -fx-border-color: gold;
                -fx-border-radius: 12;
                -fx-padding: 10;
            """));

            content.setOnMouseExited(e -> content.setStyle("""
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 12;
                -fx-border-color: #777;
                -fx-border-radius: 12;
                -fx-padding: 10;
            """));

            content.getChildren().addAll(imageView, nameLabel);
            gridPane.add(content, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}