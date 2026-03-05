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

    /** A short description of the potion's effect, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs an ExpPotion with a predefined name, image path, buy price of 40,
     * sell price of 10, and a default description of {@code "Boost EXP 10 Mins"}.
     */
    public ExpPotion() {
        super("Exp Potion", "/item/exppotion.png", 40, 10);
        setDescription("Boost EXP 10 Mins");
    }

    /**
     * Returns the description of this potion shown in the shop or inventory.
     *
     * @return a non-null string describing this potion's effect
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this potion.
     *
     * @param description the new description string; should not be {@code null}
     */
    public void setDescription(String description) {
        this.description = description;
    }
}