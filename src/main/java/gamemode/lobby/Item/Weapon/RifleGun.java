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

    /**
     * Constructs a RifleGun with a predefined name, image path, buy price of 150,
     * damage of 100, and an auto-generated description of {@code "damage: 100"}.
     */
    public RifleGun() {
        super("Rifle Gun", "/item/RifleGun.png", 150, 100);
    }
}