package gamemode.lobby.Scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import gamemode.lobby.logic.GameLogic;
import javafx.scene.text.Font;
/**
 * SpawnScreen represents the main lobby screen where the player spawns.
 *
 * <p>This screen acts as the central hub of the game where the player can:</p>
 * <ul>
 *     <li>View player money</li>
 *     <li>View player level and experience bar</li>
 *     <li>Open the inventory</li>
 *     <li>Open the shop</li>
 *     <li>Open the sell menu</li>
 * </ul>
 *
 * <p>The screen uses a {@link StackPane} layout and layers multiple UI components
 * including {@link SpawnCanvas}, {@link SellScene}, {@link ShopScene}, and
 * {@link InventoryPane}.</p>
 *
 * <p>It interacts with the {@link GameLogic} singleton to retrieve
 * player information such as money, level, and experience.</p>
 *
 * <p>Keyboard shortcut:</p>
 * <ul>
 *     <li>TAB → Toggle inventory</li>
 * </ul>
 *
 * @author
 */
public class SpawnScreen extends StackPane {

    private SellScene sellScene;
    private ShopScene shopScene;
    private InventoryPane inventoryPane;
    private SpawnCanvas spawnCanvas;
    private Label moneyLabel;
    private Label levelLabel;
    private ProgressBar expBar;
    /**
     * Constructs the SpawnScreen UI.
     *
     * <p>This initializes:</p>
     * <ul>
     *     <li>Background image</li>
     *     <li>Money display</li>
     *     <li>Level display</li>
     *     <li>Experience progress bar</li>
     *     <li>Inventory button</li>
     *     <li>SpawnCanvas</li>
     *     <li>ShopScene</li>
     *     <li>SellScene</li>
     * </ul>
     *
     * <p>All scenes are layered using StackPane.</p>
     */
    public SpawnScreen(){

        setPrefSize(1422,800);
        setMaxSize(1422,800);
        setMinSize(1422,800);

        Image ImgBck  = new Image(getClass().getResource("/gamemode/lobby/background.jpg").toExternalForm());
        BackgroundSize size = new BackgroundSize(1440,800,false,false,false,false);

        setBackground(new Background(new BackgroundImage(
                ImgBck,
                javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                javafx.scene.layout.BackgroundPosition.CENTER,
                size
        )));

        //money box
        moneyLabel = new Label("Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $");
        Font font = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),16);
        moneyLabel.setFont(font);

        StackPane moneyBox = new StackPane(moneyLabel);
        moneyBox.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 10 25 10 25;"
        );
        moneyBox.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        //************************************

        // level box
        levelLabel = new Label("Level : " + GameLogic.getInstance().getPlayer().getLevel());
        levelLabel.setFont(font);

        expBar = new ProgressBar();
        expBar.setPrefWidth(120);
        updateExpBar();

        HBox levelBox = new HBox(5);
        levelBox.setAlignment(Pos.CENTER);
        levelBox.getChildren().addAll(levelLabel, expBar);

        levelBox.setStyle(
                "-fx-background-color: #87CEFA;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 10 25 10 25;"
        );

        //************************************************************

        spawnCanvas = new SpawnCanvas();
        sellScene = new SellScene();
        shopScene = new ShopScene();
        inventoryPane = new InventoryPane();

        getChildren().addAll(spawnCanvas,sellScene,shopScene,inventoryPane);

        StackPane.setAlignment(sellScene, Pos.CENTER);
        StackPane.setAlignment(shopScene, Pos.CENTER);
        StackPane.setAlignment(inventoryPane, Pos.CENTER);

        //Inventory
        Button inventoryBtn = new Button("INVENTORY");
        inventoryBtn.setStyle("""
        -fx-background-color: #7a7a7a;
        -fx-text-fill: white;
        -fx-font-family: 'Minecraft';
        -fx-font-weight: bold;
        -fx-padding: 10 25 10 25;
        -fx-border-color: #3c3c3c;
        -fx-border-width: 3;
        -fx-font-size: 18px;
        """);
        inventoryBtn.setOnMouseClicked(e -> {
            inventoryPane.loadItems();
            inventoryPane.setVisible(true);
        });
        StackPane.setAlignment(inventoryBtn,Pos.TOP_RIGHT);
        StackPane.setMargin(inventoryBtn, new Insets(20));
        //**************************************************

        //group
        HBox topRightBox = new HBox(10);
        topRightBox.getChildren().addAll(levelBox,moneyBox, inventoryBtn);
        topRightBox.setAlignment(Pos.CENTER_RIGHT);

        topRightBox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);

        StackPane.setAlignment(topRightBox, Pos.TOP_RIGHT);
        StackPane.setMargin(topRightBox, new Insets(10, 10, 0, 0));

        getChildren().add(topRightBox);
        //************************************

        sellScene.setVisible(false);
        shopScene.setVisible(false);
        inventoryPane.setVisible(false);

        setOnKeyReleased(e -> {

            if (e.getCode() == KeyCode.TAB) {
                toggleInventory();
            }
        });

    }
    /**
     * Displays the selling interface.
     *
     * <p>The SellScene will refresh its contents before appearing.</p>
     */
    public void showSellScene(){
        sellScene.refresh();
        sellScene.setVisible(true);
        sellScene.toFront();
    }
    /**
     * Displays the shop interface.
     *
     * <p>This loads shop items and brings the shop scene to the front.</p>
     */
    public void showShopScene(){
        shopScene.loadShop();
        shopScene.getSwitchBtt().setText("Sell");
        shopScene.setVisible(true);
        shopScene.toFront();
    }
    /**
     * Returns the spawn canvas.
     *
     * @return the SpawnCanvas instance
     */
    public SpawnCanvas getSpawnCanvas(){
        return spawnCanvas;
    }
    /**
     * Returns the sell scene.
     *
     * @return SellScene instance
     */
    public SellScene getSellScene(){return sellScene;}
    /**
     * Returns the inventory pane.
     *
     * @return InventoryPane instance
     */
    public InventoryPane getInventoryPane(){return inventoryPane;}
    /**
     * Returns the label displaying player money.
     *
     * @return money label
     */
    public Label getMoneyLabel(){return moneyLabel;}
    /**
     * Returns the shop scene.
     *
     * @return ShopScene instance
     */
    public ShopScene getShopScene(){return shopScene;}
    /**
     * Returns the shop scene.
     *
     * @return ShopScene instance
     */
    private void toggleInventory() {
        boolean isOpen = inventoryPane.isVisible();

        if (!isOpen) {
            inventoryPane.loadItems();
        }

        inventoryPane.setVisible(!isOpen);
    }
    /**
     * Updates the level display label using the player's current level.
     */
    public void updateLevel() {
        levelLabel.setText("Level : " +
                GameLogic.getInstance().getPlayer().getLevel());
    }
    /**
     * Updates the experience progress bar.
     *
     * <p>The progress is calculated as:</p>
     *
     * <pre>
     * currentExp / expToNextLevel
     * </pre>
     */
    public void updateExpBar() {
        double progress =
                (double) GameLogic.getInstance().getPlayer().getExp()
                        / GameLogic.getInstance().getPlayer().getExpToNextLevel();

        expBar.setProgress(progress);
    }
    /**
     * Updates the money display label with the player's current balance.
     */
    public void updateMoney(){
        moneyLabel.setText("Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $");
    }
}
