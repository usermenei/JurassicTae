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

public class SellScene extends StackPane {
    private GridPane gridPane;
    private Button switchBtt;
    private ArrayList<Dinosaur> catalog;

    public SellScene() {
        catalog = new ArrayList<>();

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

        // 🔵 Buy / Sell Toggle
        switchBtt = new Button("Catalog");
        switchBtt.setStyle("""
            -fx-background-color: #444;
            -fx-text-fill: white;
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

        Region spacerLeft = new Region();
        Region spacerRight = new Region();

        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        header.getChildren().addAll(spacerLeft, title, switchBtt, spacerRight);

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

        shopBox.getChildren().add(0, header);
        this.getChildren().addAll(shopBox, exitBtn);
    }

    public void refresh(){
        loadItems(); // โหลด item ใหม่
    }

    private void loadItems(){

        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            if(!(item instanceof TamedDinosaur))continue;

            ButtonSell btn = new ButtonSell(item);
            gridPane.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    private void loadCatalog(){

        gridPane.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Dinosaur dinosaur : catalog) {

            // =========================
            // 📦 CONTENT (Image + Name)
            // =========================
            VBox content = new VBox(5);
            content.setAlignment(Pos.CENTER);

            Image img = dinosaur.getSprite();
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

            content.setOnMouseEntered(e -> {

                content.setStyle("""
                -fx-background-color: #444;
                -fx-background-radius: 12;
                -fx-border-color: gold;
                -fx-border-radius: 12;
                -fx-padding: 10;
                """);
            });

            content.setOnMouseExited(e -> {
                content.setStyle("""
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 12;
                -fx-border-color: #777;
                -fx-border-radius: 12;
                -fx-padding: 10;
                """);
            });

            content.getChildren().addAll(imageView, nameLabel);

            // ใส่ content ลง cell ก่อน
            gridPane.add(content, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    public void addCatalog(Dinosaur dinosaur){
        catalog.add(dinosaur);
    }
}