package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;
import javafx.scene.image.Image;

public class ExpPotion extends Potion {
    private Image image;
    public ExpPotion(){
        super("Heal Potion", "/gamemode/lobby/trap.png",20);
        image = new Image(getClass().getResource(getImgUrl()).toExternalForm());
    }

    public Image getImage(){
        return  image;
    }
}
