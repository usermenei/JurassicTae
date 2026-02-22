package gamemode.forest.entity;

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

        this.item = new HealPotion();
        this.image = ((HealPotion)item).getImage();
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
    public Image getImage(){ return image; }
    public Item getItem(){ return item; }
}