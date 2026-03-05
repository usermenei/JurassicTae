package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Strength Potion item.
 *
 * <p>
 * StrengthPotion temporarily doubles the player's attack power when used.
 * It has a fixed buy price of 100 and sell price of 10.
 * </p>
 */
public class StrengthPotion extends Potion {

    /**
     * Constructs a StrengthPotion with a predefined name, image path, buy price of 100,
     * sell price of 10, and a default description of {@code "Double Strength"}.
     */
    public StrengthPotion() {
        super("Strength Potion", "/item/strengthpotion.png", 100, 10, "Double Strength");
    }
}