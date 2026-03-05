package gamemode.lobby.Item.Potion;

import gamemode.lobby.Item.Base.Potion;

/**
 * Represents a Speed Potion item.
 *
 * <p>
 * SpeedPotion temporarily increases the player's movement speed when used.
 * It has a fixed buy price of 50 and sell price of 10.
 * </p>
 */
public class SpeedPotion extends Potion {

    /**
     * Constructs a SpeedPotion with a predefined name, image path, buy price of 50,
     * sell price of 10, and a default description of {@code "Boost Speed 10 Mins"}.
     */
    public SpeedPotion() {
        super("Speed Potion", "/item/speedpotion.png", 50, 10, "Boost Speed 10 Mins");
    }
}