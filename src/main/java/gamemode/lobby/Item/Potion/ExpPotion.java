package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents an Experience Potion item.
 *
 * <p>
 * ExpPotion increases the player's experience points when used.
 * It has a fixed buy price of 40 and sell price of 10.
 * </p>
 */
public class ExpPotion extends Potion {

    /**
     * Constructs an ExpPotion with a predefined name, image path, buy price of 40,
     * sell price of 10, and a default description of {@code "Boost EXP 10 Mins"}.
     */
    public ExpPotion() {
        super("Exp Potion", "/item/exppotion.png", 40, 10, "Boost EXP 10 Mins");
    }
}