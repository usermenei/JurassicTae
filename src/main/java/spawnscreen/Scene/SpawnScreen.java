package spawnscreen.Scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import spawnscreen.logic.GameLogic;

public class SpawnScreen extends StackPane {

    private SellScene sellScene;
    private ShopScene shopScene;
    private InventoryPane inventoryPane;
    private SpawnCanvas spawnCanvas;
    private Label moneyLabel;

    public SpawnScreen(){

        setPrefSize(1422,800);
        setMaxSize(1422,800);
        setMinSize(1422,800);

        Image ImgBck  = new Image(getClass().getResource("/spawnscreen/background.png").toExternalForm());
        BackgroundSize size = new BackgroundSize(1440,800,false,false,false,false);

        //setBackground(new Background(new BackgroundImage(ImgBck,null,null,null,size)));

        //money box
        moneyLabel = new Label("Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $");
        moneyLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: black;"
        );

        StackPane moneyBox = new StackPane(moneyLabel);
        moneyBox.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 5 12 5 12;"
        );
        moneyBox.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;"
        );
        moneyBox.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        //************************************

        spawnCanvas = new SpawnCanvas();
        sellScene = new SellScene();
        shopScene = new ShopScene();
        inventoryPane = new InventoryPane();

        getChildren().addAll(spawnCanvas,sellScene,shopScene,inventoryPane);

        StackPane.setAlignment(sellScene, Pos.CENTER);
        StackPane.setAlignment(shopScene, Pos.CENTER);
        StackPane.setAlignment(inventoryPane, Pos.CENTER);

        //Inventory
        Button inventoryBtn = new Button("Inventory");
        inventoryBtn.setOnMouseClicked(e -> {
            inventoryPane.loadItems();
            inventoryPane.setVisible(true);
        });
        StackPane.setAlignment(inventoryBtn,Pos.TOP_RIGHT);
        StackPane.setMargin(inventoryBtn, new Insets(20));
        //**************************************************

        //group
        HBox topRightBox = new HBox(10);
        topRightBox.getChildren().addAll(moneyBox, inventoryBtn);
        topRightBox.setAlignment(Pos.CENTER_RIGHT);

        topRightBox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);

        StackPane.setAlignment(topRightBox, Pos.TOP_RIGHT);
        StackPane.setMargin(topRightBox, new Insets(10, 10, 0, 0));

        getChildren().add(topRightBox);
        //************************************

        sellScene.setVisible(false);
        shopScene.setVisible(false);
        inventoryPane.setVisible(false);

    }

    public void showSellScene(){
        sellScene.refresh();
        sellScene.setVisible(true);
        sellScene.toFront();
    }

    public void hideSellScene(){
        sellScene.setVisible(false);
    }

    public void showShopScene(){
        shopScene.setVisible(true);
        shopScene.toFront();
    }

    public void hideShopScene(){
        shopScene.setVisible(false);
    }

    public void showInventory(){
        setInventoryPane(new InventoryPane());
        inventoryPane.setVisible(true);
        inventoryPane.toFront();
    }

    public void hideInventory(){
        inventoryPane.setVisible(false);
    }

    public void setInventoryPane(InventoryPane inventoryPane){
        GameLogic.getInstance().getPlayer().sortInventory();
        if (this.inventoryPane != null) {
            this.getChildren().remove(this.inventoryPane);
        }
        this.inventoryPane = inventoryPane;
        getChildren().add(inventoryPane);
        hideInventory();
    }

    public SpawnCanvas getSpawnCanvas(){
        return spawnCanvas;
    }
    public SellScene getSellScene(){return sellScene;}
    public InventoryPane getInventoryPane(){return inventoryPane;}
    public Label getMoneyLabel(){return moneyLabel;}
}
