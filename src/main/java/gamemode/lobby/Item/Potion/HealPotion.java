package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Healing Potion item.
 *
 * <p>
 * HealPotion restores player health when used.
 * It has a fixed buy price and sell price.
 * </p>
 */
public class HealPotion extends Potion {

    /**
     * Constructs a HealPotion with predefined
     * name, image path, buy price, and sell price.
     */
    public HealPotion() {
        super("Heal Potion", "/item/healpotion.png", 50, 10);
    }
}