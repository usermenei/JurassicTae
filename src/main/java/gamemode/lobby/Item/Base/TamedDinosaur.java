package gamemode.lobby.Item.Base;

import gamemode.forest.entity.Dinosaur;
import javafx.scene.image.Image;
import gamemode.lobby.Interfaces.Sellable;

public class TamedDinosaur extends Item implements Sellable {
    //field
    private Image image;
    private final int sellPrice;

    //constructor
    public TamedDinosaur(Dinosaur dinosaur) {
        super("Tamed " + dinosaur.getName(), "/gamemode/lobby/trap.png");
        this.sellPrice = dinosaur.getSellPrice();
    }

    //getter
    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    public Image getImage(){
        return image;
    }

}
