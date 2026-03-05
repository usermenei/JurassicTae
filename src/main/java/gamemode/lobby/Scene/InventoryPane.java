package gamemode.lobby.Scene;

import gamemode.lobby.Player.Player;
import javafx.scene.image.ImageView;
import gamemode.lobby.Item.Base.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.logic.GameLogic;
import javafx.scene.image.Image;

/**
 * InventoryPane represents the graphical user interface for displaying
 * the player's inventory in the lobby scene.
 *
 * <p>This component renders a scrollable grid of items owned by the player.
 * Each item is displayed with its icon and name. If the item is a Potion,
 * a "Use" button will appear when hovering over the item.</p>
 *
 * <p>Main features:</p>
 * <ul>
 *     <li>Displays inventory items in a 4-column grid layout</li>
 *     <li>Supports hover effects for visual feedback</li>
 *     <li>Allows potion usage directly from the inventory</li>
 *     <li>Refreshes automatically after potion consumption</li>
 * </ul>
 *
 * <p>This class interacts with {@link GameLogic} to retrieve the current
 * player instance and inventory items.</p>
 *
 * @author
 */
public class InventoryPane extends StackPane {

    /** Grid layout used to display inventory items */
    private GridPane grid;

    /** Player instance retrieved from GameLogic */
    private Player player;

    /**
     * Constructs the InventoryPane UI.
     *
     * <p>The constructor initializes the main layout, including:
     * the title label, scrollable inventory grid, and exit button.</p>
     */
    public InventoryPane() {

        player = GameLogic.getInstance().getPlayer();

        this.setPrefSize(700, 530);
        this.setMaxSize(700, 530);

        this.setStyle("""
        -fx-padding: 30;
        -fx-background-radius: 20;
    """);

        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPrefSize(500, 350);

        mainBox.setStyle("""
        -fx-background-color: #2b2b2b;
        -fx-padding: 30;
        -fx-background-radius: 20;
    """);

        Label title = new Label("INVENTORY");
        title.setStyle("""
        -fx-background-color: #ffcc00;
        -fx-text-fill: black;
        -fx-font-family: 'Minecraft';
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-padding: 10 30 10 30;
        -fx-background-radius: 10;
        """);

        grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_CENTER);

        loadItems();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle("""
        -fx-background: transparent;
        -fx-background-color: transparent;
        -fx-padding: 0;
    """);

        mainBox.getChildren().addAll(title, scrollPane);

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

        this.getChildren().addAll(mainBox, exitBtn);
    }

    /**
     * Loads and displays all items from the player's inventory.
     *
     * <p>This method dynamically creates UI cells for each item
     * and inserts them into the grid layout.</p>
     *
     * <p>Each cell includes:</p>
     * <ul>
     *     <li>Item icon</li>
     *     <li>Item name</li>
     *     <li>"Use" button for potion items</li>
     * </ul>
     *
     * <p>The method also applies hover effects to highlight
     * items and reveal potion usage controls.</p>
     */
    public void loadItems() {

        grid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            StackPane cell = new StackPane();
            cell.setPrefSize(120, 120);
            cell.setAlignment(Pos.CENTER);

            cell.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-background-radius: 12;
            -fx-border-color: #555;
            -fx-font-family: 'Minecraft';
            -fx-border-radius: 12;
            -fx-padding: 10;
        """);

            VBox content = new VBox(5);
            content.setAlignment(Pos.CENTER);

            Image img = item.getImg();
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            imageView.setPreserveRatio(true);

            Label nameLabel = new Label(item.getName());

            nameLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-family: 'Minecraft';
                -fx-font-weight: bold;
            """);

            content.getChildren().addAll(imageView, nameLabel);

            cell.getChildren().add(content);

            Button useButton = null;

            if (item instanceof Potion) {

                useButton = new Button("Use");
                useButton.setVisible(false);

                useButton.setStyle("""
                -fx-background-color: gold;
                -fx-font-family: 'Minecraft';
                -fx-text-fill: black;
                -fx-font-weight: bold;
            """);

                Button finalUseButton = useButton;

                useButton.setOnAction(e -> {
                    player.usePotion((Potion) item);
                    loadItems();
                });

                cell.getChildren().add(useButton);
            }

            Button finalUseButton1 = useButton;

            cell.setOnMouseEntered(e -> {

                cell.setStyle("""
                -fx-background-color: #444;
                -fx-background-radius: 12;
                -fx-font-family: 'Minecraft';
                -fx-border-color: gold;
                -fx-border-radius: 12;
                -fx-padding: 10;
            """);

                if (finalUseButton1 != null) {
                    finalUseButton1.setVisible(true);
                }
            });

            cell.setOnMouseExited(e -> {

                cell.setStyle("""
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 12;
                -fx-border-color: #777;
                -fx-border-radius: 12;
                -fx-font-family: 'Minecraft';
                -fx-padding: 10;
            """);

                if (finalUseButton1 != null) {
                    finalUseButton1.setVisible(false);
                }
            });

            grid.add(cell, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}