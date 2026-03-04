package gamemode.lobby.Item.Base;

import com.sun.webkit.dom.XPathResultImpl;
import gamemode.forest.entity.Dinosaur;
import javafx.scene.image.Image;
import gamemode.lobby.Interfaces.Sellable;

public class TamedDinosaur extends Item implements Sellable {
    //field
    private final int sellPrice;
    private Dinosaur dinosaur;

    //constructor
    public TamedDinosaur(Dinosaur dinosaur) {
        super("Tamed " + dinosaur.getName(), "/item/TamedDinosaur/Tamed"+dinosaur.getName()+".png");
        this.dinosaur = dinosaur;
        this.sellPrice = dinosaur.getSellPrice();
    }

    //getter
    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    public Dinosaur getDinosaur(){return dinosaur;}

}
