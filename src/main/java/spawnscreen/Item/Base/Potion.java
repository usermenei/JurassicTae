package spawnscreen.Item.Base;

import spawnscreen.Interfaces.Buyable;
import spawnscreen.Interfaces.Useable;
import spawnscreen.LivingThing.Player;

public abstract class Potion extends Item implements Useable, Buyable {
    //field
    private final int buyPrice;

    //constructor
    public Potion(String name, String imgUrl, int buyPrice) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
    }

    //getter
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    //method
    @Override
    public void use(Player player) {

    }
}
