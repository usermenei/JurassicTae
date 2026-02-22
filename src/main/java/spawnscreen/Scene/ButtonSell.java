package spawnscreen.Scene;

import spawnscreen.Item.Base.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import spawnscreen.logic.GameController;
import spawnscreen.logic.GameLogic;

public class ButtonSell extends Button {

    public ButtonSell(Item item) {

        this.setPrefSize(120, 130);

        // 🎨 กล่องเทาอ่อน
        this.setStyle("""
            -fx-background-color: #4a4a4a;
            -fx-background-radius: 12;
            -fx-border-color: #777;
            -fx-border-radius: 12;
        """);

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // 🖼 รูป
        Image img = new Image(
                getClass().getResource(item.getImgUrl()).toExternalForm()
        );

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);

        // 🏷 ชื่อ item
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

        box.getChildren().addAll(imageView, nameLabel);

        this.setGraphic(box);

        // 🔥 Hover effect
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

        // 🪙 เมื่อกดขาย
        this.setOnAction(e -> {
            GameLogic.getInstance().getPlayer().sellItem(item);
            GameController.getInstance().reloadSellScene();
            GameController.getInstance().reloadMoney();
            System.out.println("Sell" + item.getName());
        });
    }
}