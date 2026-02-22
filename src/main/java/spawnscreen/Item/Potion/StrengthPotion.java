package spawnscreen.Item.Potion;

import javafx.scene.image.Image;
import spawnscreen.Item.Base.Potion;

public class StrengthPotion extends Potion {
    private Image image;
    public StrengthPotion(){
        super("Strength Potion","/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){return image;}
}
