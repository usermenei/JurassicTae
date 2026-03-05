package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents an Anesthetic Dart weapon.
 *
 * <p>
 * AnestheticDart is a low-damage weapon typically used for tranquilizing
 * dinosaurs in the field. It has a buy price of 50 and a damage value of 10.
 * </p>
 */
public class AnestheticDart extends Weapon {

    /** A short description of this weapon's stats, shown in the shop or inventory. */
    private String description;

    /**
     * Constructs an AnestheticDart with a predefined name, image path, buy price of 50,
     * damage of 10, and a default description of {@code "Damage: 10"}.
     */
    public AnestheticDart() {
        super("AnestheticDart", "/item/AnestheticDart.png", 50, 10);
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