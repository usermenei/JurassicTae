package spawnscreen.Item.Weapon;

import javafx.scene.image.Image;
import spawnscreen.Item.Base.Weapon;

public class ElectricGun extends Weapon {
    Image image;
    public ElectricGun(){
        super("Electric Gun","/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return image;
    }
}
