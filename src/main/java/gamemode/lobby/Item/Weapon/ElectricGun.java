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

    /**
     * Constructs an ElectricGun with a predefined name, image path, buy price of 400,
     * damage of 290, and an auto-generated description of {@code "damage: 290"}.
     */
    public ElectricGun() {
        super("Electric Gun", "/item/ElectricGun.png", 400, 290);
    }
}