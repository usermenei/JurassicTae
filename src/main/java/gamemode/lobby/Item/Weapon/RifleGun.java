package gamemode.lobby.Item.Weapon;

import javafx.scene.image.Image;
import gamemode.lobby.Item.Base.Weapon;

public class RifleGun extends Weapon {
    Image image;
    public RifleGun(){
        super("Rifle Gun", "/gamemode/lobby/trap.png",20,100);
    }
    public Image getImage() {
        return image;
    }
}
