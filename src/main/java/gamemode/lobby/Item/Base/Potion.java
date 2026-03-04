package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;

public abstract class Potion extends Item implements Buyable, Sellable {
    //field
    private final int buyPrice;
    private final int sellPrice;
    //constructor
    public Potion(String name, String imgUrl, int buyPrice,int sellPrice) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    //getter
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public int getSellPrice(){
        return sellPrice;
    }
}
