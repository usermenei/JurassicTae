package spawnscreen.Scene;

import spawnscreen.Item.Base.Item;
import spawnscreen.Item.Base.TamedDinosaur;
import spawnscreen.logic.GameLogic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SellScene extends StackPane {
    private GridPane gridPane;

    public SellScene() {

        this.setPrefSize(700, 530);
        this.setMaxSize(700,530);
        this.setStyle("""
            -fx-padding: 30;
            -fx-background-radius: 20;
        """);

        // 🔲 กล่องพื้นหลังหลัก
        VBox shopBox = new VBox(20);
        shopBox.setAlignment(Pos.CENTER);
        shopBox.setPrefSize(500, 350);

        shopBox.setStyle("""
            -fx-background-color: #2b2b2b;
            -fx-padding: 30;
            -fx-background-radius: 20;
        """);

        // 🏷 Title
        Label title = new Label("KHAO KHEOW ZOO");
        title.setStyle("""
            -fx-background-color: #ffcc00;
            -fx-text-fill: black;
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-padding: 10 30 10 30;
            -fx-background-radius: 10;
        """);

        // 📦 Grid สำหรับไอเท็ม
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.TOP_CENTER);

        int col = 0;
        int row = 0;

        //GameLogic.getInstance().getPlayer().sortInventory();
        loadItems();

        // 🧾 ScrollPane ครอบ GridPane
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

        // ❌ Exit Button
        Button exitBtn = new Button("X");
        exitBtn.setStyle("""
            -fx-background-color: red;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        exitBtn.setOnAction(e -> this.setVisible(false));

        // 📌 จัดตำแหน่ง
        StackPane.setAlignment(exitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(exitBtn, new Insets(10));

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(20, 0, 0, 0));

        this.getChildren().addAll(shopBox, title, exitBtn);
    }

    public void refresh(){
        loadItems(); // โหลด item ใหม่
    }

    private void loadItems(){

        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            ButtonSell btn = new ButtonSell(item);
            gridPane.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}