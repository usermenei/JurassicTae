package gamemode.lobby.Item.Weapon;

import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Weapon;

public class ElectricGun extends Weapon {
    Image image;
    public ElectricGun(){
        super("Electric Gun", "/item/ElectricGun.png",20,200);
    }
    public Image getImage() {
        return image;
    }
}
