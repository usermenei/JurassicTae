package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents a Rifle Gun weapon.
 *
 * <p>
 * RifleGun is a mid-range weapon with balanced
 * cost and damage output.
 * </p>
 */
public class RifleGun extends Weapon {

    /**
     * Constructs a RifleGun with predefined
     * name, image path, buy price, and damage value.
     */
    public RifleGun() {
        super("Rifle Gun", "/item/RifleGun.png", 150, 100);
    }
}