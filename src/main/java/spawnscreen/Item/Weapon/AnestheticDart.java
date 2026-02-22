package spawnscreen.Item.Weapon;

import javafx.scene.image.Image;
import spawnscreen.Item.Base.Weapon;

public class AnestheticDart extends Weapon {
    Image image;
    public AnestheticDart(){
        super("AnestheticDart", "/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return image;
    }
}
