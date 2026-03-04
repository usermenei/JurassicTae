package gamemode.lobby.Item.Weapon;

import gamemode.lobby.Item.Base.Weapon;

/**
 * Represents an Anesthetic Dart weapon.
 *
 * <p>
 * AnestheticDart is a low-damage weapon typically used
 * for tranquilizing dinosaurs. It has a moderate buy price
 * and low damage value.
 * </p>
 */
public class AnestheticDart extends Weapon {

    /**
     * Constructs an AnestheticDart with predefined
     * name, image path, buy price, and damage value.
     */
    public AnestheticDart() {
        super("AnestheticDart", "/item/AnestheticDart.png", 50, 10);
    }
}