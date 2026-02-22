package spawnscreen.Scene;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import spawnscreen.Item.Base.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import spawnscreen.Item.Base.Potion;
import spawnscreen.Item.Base.TamedDinosaur;
import spawnscreen.Item.Base.Weapon;
import spawnscreen.logic.GameController;
import spawnscreen.logic.GameLogic;
import javafx.scene.image.Image;

public class InventoryPane extends StackPane {
    private GridPane grid;
    public InventoryPane() {

        this.setPrefSize(600, 400);
        this.setMaxSize(600, 400);
        this.setStyle("""
            -fx-padding: 30;
            -fx-background-radius: 20;""");

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        box.setPrefSize(500, 400);
        box.setStyle("""
            -fx-background-color: #2b2b2b;
            -fx-background-radius: 20;
            -fx-padding: 20;
        """);

        Label title = new Label("INVENTORY");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 22px;
            -fx-font-weight: bold;
        """);

        // Grid สำหรับไอเท็ม
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);

        loadItems();

        // ScrollPane ครอบ grid
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);

        scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
        """);

        box.getChildren().addAll(title, scrollPane);

        // 🔴 Exit Button
        Button exitBtn = new Button("X");
        exitBtn.setStyle("""
            -fx-background-color: red;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """);

        exitBtn.setOnAction(e -> this.setVisible(false));
        StackPane.setAlignment(exitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(exitBtn, new Insets(10));


        this.getChildren().addAll(box,exitBtn);
    }

    public void loadItems() {
        grid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            VBox cell = new VBox(5);
            cell.setPrefSize(120, 120);
            cell.setAlignment(Pos.CENTER);

            // 🎨 พื้นหลังเทาอ่อน
            cell.setStyle("""
                    -fx-background-color: #3a3a3a;
                    -fx-background-radius: 12;
                    -fx-border-color: #555;
                    -fx-border-radius: 12;
                    """);

            // 🖼 รูปภาพ
            Image img = item.getImage();

            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            imageView.setPreserveRatio(true);

            // 🏷 ชื่อ item
            Label nameLabel = new Label(item.getName());
            nameLabel.setStyle("""
                    -fx-text-fill: white;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    """);

            if (item instanceof Weapon) {
                nameLabel.setBackground(new Background(
                        new BackgroundFill(
                                Color.RED,
                                new CornerRadii(5),
                                Insets.EMPTY
                        )
                ));
            } else if (item instanceof Potion) {
                nameLabel.setBackground(new Background(
                        new BackgroundFill(
                                Color.YELLOW,
                                new CornerRadii(5),
                                Insets.EMPTY
                        )
                ));
                nameLabel.setStyle("""
                        -fx-text-fill: black;
                        -fx-font-size: 12px;
                        -fx-font-weight: bold;
                        """);
            } else if (item instanceof TamedDinosaur) {
                nameLabel.setBackground(new Background(
                        new BackgroundFill(
                                Color.GREEN,
                                new CornerRadii(5),
                                Insets.EMPTY
                        )
                ));
                nameLabel.setStyle("""
                        -fx-text-fill: black;
                        -fx-font-size: 12px;
                        -fx-font-weight: bold;
                        """);
            }

            cell.getChildren().addAll(imageView, nameLabel);

            grid.add(cell, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}
