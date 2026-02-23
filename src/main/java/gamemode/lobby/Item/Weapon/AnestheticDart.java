package gamemode.lobby.Item.Weapon;

import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Weapon;

public class AnestheticDart extends Weapon {
    Image image;
    public AnestheticDart(){
        super("AnestheticDart", "/gamemode/lobby/trap.png",20,10);
    }

    public Image getImage() {
        return image;
    }
}
