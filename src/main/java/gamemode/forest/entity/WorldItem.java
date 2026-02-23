package gamemode.forest.entity;

import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import gamemode.lobby.Item.Weapon.AnestheticDart;
import gamemode.lobby.Item.Weapon.ElectricGun;
import gamemode.lobby.Item.Weapon.Noose;
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
        this.image = loadImage(item);
    }
    private Item generateRandomItem() {

        int r = (int)(Math.random() * 6);

        switch (r) {
            case 0: return new HealPotion();
            case 1: return new SpeedPotion();
            case 2: return new StrengthPotion();
            case 3: return new AnestheticDart();
            case 4: return new ElectricGun();
            case 5: return new Noose();
            default: return new HealPotion();
        }
    }

    private Image loadImage(Item item) {

        if (item instanceof HealPotion)
            return ((HealPotion) item).getImage();

        if (item instanceof SpeedPotion)
            return ((SpeedPotion) item).getImage();

        if (item instanceof StrengthPotion)
            return ((StrengthPotion) item).getImage();

        if (item instanceof AnestheticDart)
            return ((AnestheticDart) item).getImage();

        if (item instanceof ElectricGun)
            return ((ElectricGun) item).getImage();

        if (item instanceof Noose)
            return ((Noose) item).getImage();

        return null;
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
    public Image getImage(){ return image; }
    public Item getItem(){ return item; }
}