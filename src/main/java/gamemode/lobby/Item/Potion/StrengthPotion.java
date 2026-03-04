package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Strength Potion item.
 *
 * <p>
 * StrengthPotion temporarily increases player attack power.
 * It has a fixed buy price and sell price.
 * </p>
 */
public class StrengthPotion extends Potion {

    /**
     * Constructs a StrengthPotion with predefined
     * name, image path, buy price, and sell price.
     */
    public StrengthPotion() {
        super("Strength Potion", "/item/strengthpotion.png", 100, 10);
    }
}