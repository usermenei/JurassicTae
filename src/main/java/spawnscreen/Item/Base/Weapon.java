package spawnscreen.Item.Base;

import javafx.scene.image.Image;
import spawnscreen.Interfaces.Buyable;
import spawnscreen.Interfaces.Useable;
import spawnscreen.LivingThing.Player;

public abstract class Weapon extends Item implements Useable, Buyable {
    private final int buyPrice;

    public Weapon(String name, String imgUrl, int buyPrice) {
        super(name, imgUrl);
        this.buyPrice = buyPrice;
    }

    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public void use(Player player) {
    }

    public abstract Image getImage();
}
