package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents an Experience Potion item.
 *
 * <p>
 * ExpPotion increases the player's experience points when used.
 * It has a fixed buy price and sell price.
 * </p>
 */
public class ExpPotion extends Potion {

    /**
     * Constructs an ExpPotion with predefined
     * name, image path, buy price, and sell price.
     */
    public ExpPotion() {
        super("Exp Potion", "/item/exppotion.png", 40, 10);
    }
}