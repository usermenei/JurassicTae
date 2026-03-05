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

    /** A short description of the potion's effect, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs a HealPotion with a predefined name, image path, buy price of 50,
     * sell price of 10, and a default description of {@code "Restore Max HP"}.
     */
    public HealPotion() {
        super("Heal Potion", "/item/healpotion.png", 50, 10);
        setDescription("Restore Max HP");
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