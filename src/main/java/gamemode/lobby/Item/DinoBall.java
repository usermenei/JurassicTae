package gamemode.lobby.Item;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Item.Base.Item;
import javafx.scene.image.Image;

public class DinoBall extends Item implements Buyable {
    Image image;
    public DinoBall(){
        super("DinoBall", "/item/dinoball.PNG");
    }
    public Image getImage() {
        return image;
    }

    @Override
    public int getBuyPrice() {
        return 20;
    }
}
