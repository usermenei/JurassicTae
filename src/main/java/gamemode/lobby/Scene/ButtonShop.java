package gamemode.lobby.Scene;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.LivingThing.Player;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.GameLogic;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ButtonShop extends Button {

    public ButtonShop(Item item) {

        this.setPrefSize(120, 150);

        // 🎨 กล่องเทาเข้มแบบเดียวกับปุ่มขาย
        this.setStyle("""
            -fx-background-color: #4a4a4a;
            -fx-background-radius: 12;
            -fx-border-color: #777;
            -fx-border-radius: 12;
        """);

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // 🖼 รูป
        Image img = item.getImg();

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);

        // 🏷 ชื่อ
        Label nameLabel = new Label(item.getName());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPadding(new Insets(3, 8, 3, 8));
        nameLabel.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(80, 80, 80),
                        new CornerRadii(6),
                        Insets.EMPTY
                )
        ));

        // 💥 Stat (เช่น Damage)
        Label statLabel = new Label("Damage: 1");
        statLabel.setTextFill(Color.LIGHTGRAY);
        statLabel.setStyle("-fx-font-size: 11px;");

        // 💰 ราคา
        Label priceLabel = new Label("Price: " +((Buyable)item).getBuyPrice() +"$");
        priceLabel.setTextFill(Color.GOLD);
        priceLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        box.getChildren().addAll(imageView, nameLabel, statLabel, priceLabel);

        this.setGraphic(box);

        // ✨ Hover effect
        this.setOnMouseEntered(e ->
                this.setStyle("""
                    -fx-background-color: #5a5a5a;
                    -fx-background-radius: 12;
                    -fx-border-color: gold;
                    -fx-border-radius: 12;
                """)
        );

        this.setOnMouseExited(e ->
                this.setStyle("""
                    -fx-background-color: #4a4a4a;
                    -fx-background-radius: 12;
                    -fx-border-color: #777;
                    -fx-border-radius: 12;
                """)
        );

        this.setOnAction(e -> {
            Player player = GameLogic.getInstance().getPlayer();
            player.buyItem(item);

            GameController.getInstance().reloadMoney();
            System.out.println("Buy " + item.getName());
        });
    }
}