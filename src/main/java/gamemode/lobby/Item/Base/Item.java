package gamemode.lobby.Item.Base;

import javafx.scene.image.Image;

public abstract class Item {
    //field
    private final String name;
    private final String imgUrl;
    //constructor
    public Item(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    //getter
    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public abstract Image getImage();
}
