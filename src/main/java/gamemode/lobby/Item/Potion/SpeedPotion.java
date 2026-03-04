package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Speed Potion item.
 *
 * <p>
 * SpeedPotion temporarily increases player movement speed.
 * It has a fixed buy price and sell price.
 * </p>
 */
public class SpeedPotion extends Potion {

    /**
     * Constructs a SpeedPotion with predefined
     * name, image path, buy price, and sell price.
     */
    public SpeedPotion() {
        super("Speed Potion", "/item/speedpotion.png", 50, 10);
    }
}