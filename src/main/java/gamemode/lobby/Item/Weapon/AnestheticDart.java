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

    /**
     * Constructs an AnestheticDart with a predefined name, image path, buy price of 50,
     * damage of 10, and an auto-generated description of {@code "damage: 10"}.
     */
    public AnestheticDart() {
        super("AnestheticDart", "/item/AnestheticDart.png", 50, 10);
    }
}