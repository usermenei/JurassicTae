package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents a Rifle Gun weapon.
 *
 * <p>
 * RifleGun is a mid-range weapon with balanced cost and damage output.
 * It has a buy price of 150 and a damage value of 100.
 * </p>
 */
public class RifleGun extends Weapon {

    /** A short description of this weapon's stats, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs a RifleGun with a predefined name, image path, buy price of 150,
     * damage of 100, and a default description of {@code "Damage: 100"}.
     */
    public RifleGun() {
        super("Rifle Gun", "/item/RifleGun.png", 150, 100);
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