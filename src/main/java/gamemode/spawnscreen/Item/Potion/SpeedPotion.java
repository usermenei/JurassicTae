package gamemode.spawnscreen.Item.Potion;

import javafx.scene.image.Image;
import gamemode.spawnscreen.Item.Base.Potion;

public class SpeedPotion extends Potion {
    private Image image;

    public SpeedPotion(){
        super("Speed Potion","/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){return image;}
}
