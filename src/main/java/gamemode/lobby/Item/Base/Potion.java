package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Useable;
import gamemode.lobby.LivingThing.Player;

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
