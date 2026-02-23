package gamemode.lobby.Item.Base;

import javafx.scene.image.Image;

public abstract class Item {
    //field
    private final String name;
    private final String imgUrl;
    private Image image;
    //constructor
    public Item(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.image = new Image(getClass().getResource(imgUrl).toExternalForm());
    }

    //getter
    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Image getImg(){return image;};

}
