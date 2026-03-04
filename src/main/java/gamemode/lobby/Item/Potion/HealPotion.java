package gamemode.lobby.Item.Potion;

import gamemode.lobby.Player.Player;
import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Potion;

public  class HealPotion extends Potion {
    private Image image;
    public HealPotion(){
        super("Heal Potion", "/item/healpotion.png",20,10);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return  image;
    }
}
