package spawnscreen.Item.Potion;

import javafx.scene.image.Image;
import spawnscreen.Item.Base.Potion;

public  class HealPotion extends Potion {
    private Image image;
    public HealPotion(){
        super("Heal Potion","/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return  image;
    }
}
