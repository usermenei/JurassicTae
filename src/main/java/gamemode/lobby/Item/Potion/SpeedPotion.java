package gamemode.lobby.Item.Potion;

import gamemode.lobby.Player.Player;
import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Potion;

public class SpeedPotion extends Potion {
    private Image image;

    public SpeedPotion(){
        super("Speed Potion", "/item/speedpotion.png",20,10);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }
    public Image getImage(){return image;}
}
