package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents an Electric Gun weapon.
 *
 * <p>
 * ElectricGun is a high-damage weapon with a high purchase cost.
 * It is designed for powerful combat scenarios.
 * </p>
 */
public class ElectricGun extends Weapon {

    /**
     * Constructs an ElectricGun with predefined
     * name, image path, buy price, and damage value.
     */
    public ElectricGun() {
        super("Electric Gun", "/item/ElectricGun.png", 400, 290);
    }
}