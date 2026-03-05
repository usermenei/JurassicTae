package gamemode.lobby.Scene;

import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Potion.ExpPotion;
import gamemode.lobby.Item.Potion.HealPotion;
import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import gamemode.lobby.Item.Weapon.AnestheticDart;
import gamemode.lobby.Item.Weapon.ElectricGun;
import gamemode.lobby.Item.Base.DinoBall;
import gamemode.lobby.Item.Weapon.RifleGun;
import gamemode.lobby.logic.GameLogic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * ShopScene represents the shop interface in the lobby where players
 * can purchase and sell items.
 *
 * <p>The scene supports two modes:</p>
 * <ul>
 *     <li><b>Buy Mode</b> – Displays items available for purchase.</li>
 *     <li><b>Sell Mode</b> – Displays items from the player's inventory
 *     that can be sold.</li>
 * </ul>
 *
 * <p>The interface uses a grid layout to organize items into
 * four columns. Each item is represented by either a
 * {@link ButtonShop} or {@link ButtonSell} component.</p>
 *
 * <p>The scene also provides a toggle button allowing the user
 * to switch between Buy and Sell modes.</p>
 */
public class ShopScene extends StackPane {

    /** Grid used to display shop items */
    private GridPane gridPane;

    /** List of items available in the shop */
    private ArrayList<Item> items;

    /** Button used to toggle between Buy and Sell modes */
    private Button switchBtt;

    /** Pixel-style font used for UI elements */
    private Font font = Font.loadFont(
            getClass().getResourceAsStream("/fonts/pixel.ttf"), 18);

    /**
     * Constructs the shop scene UI.
     *
     * <p>This constructor initializes the layout, loads shop items,
     * and sets up the toggle button for switching between buy
     * and sell modes.</p>
     */
    public ShopScene() {

        items = new ArrayList<>();

        items.add(new AnestheticDart());
        items.add(new ElectricGun());
        items.add(new RifleGun());
        items.add(new DinoBall());

        items.add(new ExpPotion());
        items.add(new HealPotion());
        items.add(new SpeedPotion());
        items.add(new StrengthPotion());

        this.setPrefSize(700, 530);
        this.setMaxSize(700, 530);
        this.setAlignment(Pos.CENTER);

        VBox shopBox = new VBox(20);
        shopBox.setPrefSize(530, 500);
        shopBox.setAlignment(Pos.TOP_CENTER);

        shopBox.setStyle("""
            -fx-background-color: #2b2b2b;
            -fx-padding: 30;
            -fx-background-radius: 20;
        """);

        Label title = new Label("SHOP");

        title.setStyle("""
        -fx-background-color: #ffcc00;
        -fx-text-fill: black;
        -fx-font-family: 'Minecraft';
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-padding: 10 30 10 30;
        -fx-background-radius: 10;
        """);

        switchBtt = new Button("Sell");
        switchBtt.setFont(font);

        switchBtt.setStyle("""
        -fx-background-color: #444;
        -fx-text-fill: white;
        -fx-font-family: 'Minecraft';
        -fx-font-size: 18px;
        -fx-padding: 10 20 10 20;
        -fx-background-radius: 10;
        """);

        switchBtt.setOnMouseClicked(e -> {
            if (switchBtt.getText().equals("Buy")) {
                loadShop();
                switchBtt.setText("Sell");
            } else {
                loadSell();
                switchBtt.setText("Buy");
            }
        });

        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(title, switchBtt);

        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.TOP_LEFT);

        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.prefWidthProperty().bind(shopBox.widthProperty());

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }

        loadShop();

        shopBox.getChildren().addAll(titleBox, gridPane);

        Button exitBtn = new Button("X");

        exitBtn.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 18));

        exitBtn.setStyle("""
        -fx-background-color: red;
        -fx-text-fill: white;
        -fx-font-family: 'Minecraft';
        -fx-font-weight: bold;
        -fx-font-size: 18px;
        """);

        exitBtn.setOnAction(e -> this.setVisible(false));

        StackPane.setAlignment(shopBox, Pos.CENTER);
        StackPane.setAlignment(exitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(exitBtn, new Insets(10));

        this.getChildren().addAll(shopBox, exitBtn);
    }

    /**
     * Loads all items available for purchase into the grid layout.
     *
     * <p>This method clears the grid and inserts {@link ButtonShop}
     * elements representing items available in the shop.</p>
     */
    public void loadShop() {

        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : items) {

            ButtonShop btn = new ButtonShop(item);

            btn.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(btn, Priority.ALWAYS);

            gridPane.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Loads sellable items from the player's inventory.
     *
     * <p>Only potion items are currently allowed to be sold.</p>
     */
    public void loadSell() {

        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            if (!(item instanceof Potion)) continue;

            ButtonSell btn = new ButtonSell(item);

            btn.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(btn, Priority.ALWAYS);

            gridPane.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Returns the button used to toggle between buy and sell modes.
     *
     * @return the toggle button
     */
    public Button getSwitchBtt(){
        return switchBtt;
    }
}