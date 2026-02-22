package spawnscreen.Item.Base;

import javafx.scene.image.Image;
import spawnscreen.Interfaces.Sellable;

public class TamedDinosaur extends Item implements Sellable {
    //field
    private Image image;
    private final int sellPrice;

    //constructor
    public TamedDinosaur(String name, String imgUrl, int sellPrice) {
        super(name, imgUrl);
        this.sellPrice = sellPrice;
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
