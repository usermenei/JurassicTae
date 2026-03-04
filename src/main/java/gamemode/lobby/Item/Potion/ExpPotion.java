package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Player.Player;
import javafx.scene.image.Image;

public class ExpPotion extends Potion {
    private Image image;
    public ExpPotion(){
        super("Exp Potion", "/item/exppotion.png",20,10);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return  image;
    }

}
