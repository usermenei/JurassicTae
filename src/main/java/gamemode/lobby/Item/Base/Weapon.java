package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Useable;
import gamemode.lobby.Player.Player;

public abstract class Weapon extends Item implements Useable, Buyable {
    private int dmg;
    private final int buyPrice;

    public Weapon(String name, String imgUrl, int buyPrice,int dmg) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
        this.dmg = dmg;
    }

    @Override
    public void use(Player player) {
    }

    public int getDmg(){
        return dmg;
    }

    @Override
    public int getBuyPrice(){return buyPrice;}
}
