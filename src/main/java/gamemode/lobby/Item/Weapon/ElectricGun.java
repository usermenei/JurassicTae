package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents an Electric Gun weapon.
 *
 * <p>
 * ElectricGun is a high-damage weapon designed for powerful combat scenarios.
 * It has a buy price of 400 and a damage value of 290.
 * </p>
 */
public class ElectricGun extends Weapon {

    /** A short description of this weapon's stats, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs an ElectricGun with a predefined name, image path, buy price of 400,
     * damage of 290, and a default description of {@code "Damage: 290"}.
     */
    public ElectricGun() {
        super("Electric Gun", "/item/ElectricGun.png", 400, 290);
        setDescription("Damage: " + this.getDamage());
    }

    /**
     * Returns the description of this weapon shown in the shop or inventory.
     *
     * @return a non-null string describing this weapon's stats
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this weapon.
     *
     * @param description the new description string; should not be {@code null}
     */
    public void setDescription(String description) {
        this.description = description;
    }
}