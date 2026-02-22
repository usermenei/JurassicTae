package gamemode.lobby.Item.Weapon;

import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Weapon;

public class Noose extends Weapon {
    Image image;
    public Noose(){
        super("Noose", "/gamemode/lobby/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return image;
    }
}
