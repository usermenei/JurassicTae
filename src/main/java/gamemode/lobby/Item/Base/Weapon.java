package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;

public abstract class Weapon extends Item implements Buyable {
    private int damage;
    private final int buyPrice;

    public Weapon(String name, String imgUrl, int buyPrice,int damage) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getBuyPrice(){return buyPrice;}
}
