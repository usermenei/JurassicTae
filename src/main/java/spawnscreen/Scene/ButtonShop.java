package spawnscreen.Scene;
import spawnscreen.Item.Base.Item;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.image.Image;


public class ButtonShop extends Button {
    public ButtonShop(Item item){
        Image img = new Image(getClass().getResource(item.getImgUrl()).toExternalForm());

        this.setPrefSize(100, 100);
        this.setMinSize(100, 100);
        this.setMaxSize(100, 100);

        BackgroundSize size = new BackgroundSize(100,100,false,false,false,false);
        this.setBackground(new Background(new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size)));

        this.setOnMouseClicked(e -> {
            System.out.println("Buy" + item.getName());
        });

    }
}
