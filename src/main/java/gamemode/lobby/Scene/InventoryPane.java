package gamemode.lobby.Scene;

import gamemode.lobby.Player.Player;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import gamemode.lobby.Item.Base.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.Base.Weapon;
import gamemode.lobby.logic.GameLogic;
import javafx.scene.image.Image;

public class InventoryPane extends StackPane {
    private GridPane grid;
    private Player player;
    public InventoryPane() {

        player = GameLogic.getInstance().getPlayer();

        this.setPrefSize(700, 530);
        this.setMaxSize(700, 530);
        this.setStyle("""
        -fx-padding: 30;
        -fx-background-radius: 20;
    """);

        // 🔲 กล่องหลักเหมือน SellScene
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPrefSize(500, 350);

        mainBox.setStyle("""
        -fx-background-color: #2b2b2b;
        -fx-padding: 30;
        -fx-background-radius: 20;
    """);

        // 🏷 Title เหมือน SellScene
        Label title = new Label("INVENTORY");
        title.setStyle("""
        -fx-background-color: #ffcc00;
        -fx-text-fill: black;
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-padding: 10 30 10 30;
        -fx-background-radius: 10;
    """);

        // 📦 Grid
        grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_CENTER);

        loadItems();

        // 🧾 ScrollPane แบบเดียวกับ SellScene
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

        // ❌ Exit Button
        Button exitBtn = new Button("X");
        exitBtn.setStyle("""
        -fx-background-color: red;
        -fx-text-fill: white;
        -fx-font-weight: bold;
    """);

        exitBtn.setOnAction(e -> this.setVisible(false));

        StackPane.setAlignment(exitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(exitBtn, new Insets(10));

        this.getChildren().addAll(mainBox, exitBtn);
    }

//    public void loadItems() {
//        grid.getChildren().clear();
//
//        int col = 0;
//        int row = 0;
//
//        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {
//
//
//            VBox cell = new VBox(5);
//            cell.setPrefSize(120, 120);
//            cell.setAlignment(Pos.CENTER);
//
//            // 🎨 พื้นหลังเทาอ่อน
//            cell.setStyle("""
//            -fx-background-color: #3a3a3a;
//            -fx-background-radius: 12;
//            -fx-border-color: #555;
//            -fx-border-radius: 12;
//            -fx-padding: 10;
//            """);
//
//            // 🖼 รูปภาพ
//            Image img = item.getImg();
//
//            ImageView imageView = new ImageView(img);
//            imageView.setFitWidth(70);
//            imageView.setFitHeight(70);
//            imageView.setPreserveRatio(true);
//
//            // 🏷 ชื่อ item
//            Label nameLabel = new Label(item.getName());
//            nameLabel.setStyle("""
//                    -fx-text-fill: white;
//                    -fx-font-size: 12px;
//                    -fx-font-weight: bold;
//                    """);
//
//            cell.getChildren().addAll(imageView, nameLabel);
//
//            grid.add(cell, col, row);
//
//            col++;
//            if (col == 4) {
//                col = 0;
//                row++;
//            }
//
//            cell.setOnMouseEntered(e ->
//                    cell.setStyle("""
//            -fx-background-color: #444;
//            -fx-background-radius: 12;
//            -fx-border-color: gold;
//            -fx-border-radius: 12;
//            -fx-padding: 10;
//            """)
//            );
//
//            cell.setOnMouseExited(e ->
//                    cell.setStyle("""
//        -fx-background-color: #3a3a3a;
//        -fx-background-radius: 12;
//        -fx-border-color: #777;
//        -fx-border-radius: 12;
//        -fx-padding: 10;
//    """)
//            );
//        }
//    }

    public void loadItems() {

        grid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Item item : GameLogic.getInstance().getPlayer().getInventory()) {

            // =========================
            // 🔲 CELL (StackPane)
            // =========================
            StackPane cell = new StackPane();
            cell.setPrefSize(120, 120);
            cell.setAlignment(Pos.CENTER);

            cell.setStyle("""
            -fx-background-color: #3a3a3a;
            -fx-background-radius: 12;
            -fx-border-color: #555;
            -fx-border-radius: 12;
            -fx-padding: 10;
        """);

            // =========================
            // 📦 CONTENT (Image + Name)
            // =========================
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
                -fx-font-weight: bold;
            """);

            content.getChildren().addAll(imageView, nameLabel);

            // ใส่ content ลง cell ก่อน
            cell.getChildren().add(content);

            // =========================
            // 🧪 ถ้าเป็น Potion → สร้างปุ่ม Use
            // =========================
            Button useButton = null;

            if (item instanceof Potion) {

                useButton = new Button("Use");
                useButton.setVisible(false);

                useButton.setStyle("""
                -fx-background-color: gold;
                -fx-text-fill: black;
                -fx-font-weight: bold;
            """);

                Button finalUseButton = useButton;

                useButton.setOnAction(e -> {
                    player.usePotion((Potion) item);

                    loadItems(); // refresh inventory UI
                });

                cell.getChildren().add(useButton);
            }

            // =========================
            // 🖱 Hover Effect
            // =========================
            Button finalUseButton1 = useButton;

            cell.setOnMouseEntered(e -> {

                cell.setStyle("""
                -fx-background-color: #444;
                -fx-background-radius: 12;
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
                -fx-padding: 10;
            """);

                if (finalUseButton1 != null) {
                    finalUseButton1.setVisible(false);
                }
            });

            // =========================
            // 📍 Add to Grid
            // =========================
            grid.add(cell, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}
