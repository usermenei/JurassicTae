package gamemode.lobby.Scene;

import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Potion.ExpPotion;
import gamemode.lobby.Item.Potion.HealPotion;
import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import gamemode.lobby.Item.Weapon.AnestheticDart;
import gamemode.lobby.Item.Weapon.ElectricGun;
import gamemode.lobby.Item.DinoBall;
import gamemode.lobby.Item.Weapon.RifleGun;
import gamemode.lobby.logic.GameLogic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ShopScene extends StackPane {

    private GridPane gridPane;
    private ArrayList<Item> items;
    private Button switchBtt;
    private Font font = Font.loadFont(
            getClass().getResourceAsStream("/fonts/pixel.ttf"), 18);

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

        // 🔶 Title
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

        // 🔵 Buy / Sell Toggle
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
        //
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(title, switchBtt);

        // 🔲 Grid
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.TOP_LEFT);

        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.prefWidthProperty().bind(shopBox.widthProperty());

        // 🔥 4 Columns Fixed Layout
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }

        loadShop();

        shopBox.getChildren().addAll(titleBox, gridPane);

        // ❌ Exit Button
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

    public Button getSwitchBtt(){
        return switchBtt;
    }
}