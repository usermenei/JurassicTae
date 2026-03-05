package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Healing Potion item.
 *
 * <p>
 * HealPotion restores the player's health when used.
 * It has a fixed buy price of 50 and sell price of 10.
 * </p>
 */
public class HealPotion extends Potion {

    /**
     * Constructs a HealPotion with a predefined name, image path, buy price of 50,
     * sell price of 10, and a default description of {@code "Restore Max HP"}.
     */
    public HealPotion() {
        super("Heal Potion", "/item/healpotion.png", 50, 10, "Restore Max HP");
    }
}