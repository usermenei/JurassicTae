package gamemode.lobby.Item.Potion;

import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Potion;

public class StrengthPotion extends Potion {
    private Image image;
    public StrengthPotion(){
        super("Strength Potion", "/gamemode/lobby/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){return image;}
}
