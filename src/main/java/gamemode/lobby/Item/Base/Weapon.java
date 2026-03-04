package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Useable;
import gamemode.lobby.Player.Player;

public abstract class Weapon extends Item implements Useable, Buyable {
    private int damage;
    private final int buyPrice;

    public Weapon(String name, String imgUrl, int buyPrice,int damage) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
        this.damage = damage;
    }

    @Override
    public void use(Player player) {
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getBuyPrice(){return buyPrice;}
}
