package gamemode.forest.entity;

import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import gamemode.lobby.Item.Weapon.AnestheticDart;
import gamemode.lobby.Item.DinoBall;
import gamemode.lobby.Item.Weapon.ElectricGun;
import javafx.scene.image.Image;
import gamemode.lobby.Item.Potion.HealPotion;
import gamemode.lobby.Item.Base.Item;

/**
 * Represents a collectible item spawned at a fixed position in the game world.
 * <p>
 * When created, a {@code WorldItem} randomly selects one of the available
 * item types and holds its position and sprite until the player picks it up.
 * </p>
 */
public class WorldItem {

    /** The X coordinate of this item in the game world. */
    private double x;

    /** The Y coordinate of this item in the game world. */
    private double y;

    /** The item instance that will be given to the player on pickup. */
    private Item item;

    /** The sprite image used to render this item in the world. */
    private Image image;

    /**
     * Constructs a {@code WorldItem} at the given position with a randomly selected item.
     *
     * @param x the X coordinate in the game world
     * @param y the Y coordinate in the game world
     */
    public WorldItem(double x, double y) {
        this.x = x;
        this.y = y;
        this.item = generateRandomItem();
        this.image = item.getImg();
    }

    /**
     * Randomly selects and returns one of the available item types.
     * <p>
     * Each item has an equal 25% chance of being chosen from:
     * {@link HealPotion}, {@link SpeedPotion}, {@link StrengthPotion}, and {@link DinoBall}.
     * Defaults to {@link HealPotion} if the random result is out of range.
     * </p>
     *
     * @return a new randomly selected {@link Item} instance
     */
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

    /**
     * Returns the X coordinate of this item in the game world.
     *
     * @return the X coordinate
     */
    public double getX() { return x; }

    /**
     * Returns the Y coordinate of this item in the game world.
     *
     * @return the Y coordinate
     */
    public double getY() { return y; }

    /**
     * Returns the sprite image used to render this item in the world.
     *
     * @return the item's {@link Image}
     */
    public Image getImage() { return image; }

    /**
     * Returns the item that will be given to the player on pickup.
     *
     * @return the underlying {@link Item} instance
     */
    public Item getItem() { return item; }
}