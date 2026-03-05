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

    /** A short description of the potion's effect, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs a StrengthPotion with a predefined name, image path, buy price of 100,
     * sell price of 10, and a default description of {@code "Double Strength"}.
     */
    public StrengthPotion() {
        super("Strength Potion", "/item/strengthpotion.png", 100, 10);
        setDescription("Double Strength");
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