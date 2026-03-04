package gamemode.forest.entity;

import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import gamemode.lobby.Item.Weapon.AnestheticDart;
import gamemode.lobby.Item.DinoBall;
import gamemode.lobby.Item.Weapon.ElectricGun;
import javafx.scene.image.Image;
import gamemode.lobby.Item.Potion.HealPotion;
import gamemode.lobby.Item.Base.Item;

public class WorldItem {

    private double x, y;
    private Item item;
    private Image image;

    public WorldItem(double x, double y) {

        this.x = x;
        this.y = y;

        this.item = generateRandomItem();
        this.image = item.getImg();
    }
    private Item generateRandomItem() {

        int r = (int)(Math.random() * 4);

        switch (r) {
            case 0: return new HealPotion();
            case 1: return new SpeedPotion();
            case 2: return new StrengthPotion();
            case 3: return new DinoBall();
            default: return new HealPotion();
        }
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
    public Image getImage(){ return image; }
    public Item getItem(){ return item; }
}