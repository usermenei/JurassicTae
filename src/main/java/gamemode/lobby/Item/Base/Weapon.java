package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;

/**
 * Abstract base class representing a weapon item in the game.
 *
 * <p>
 * A Weapon:
 * <ul>
 *     <li>Extends {@link Item}</li>
 *     <li>Implements {@link Buyable}</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class provides core weapon attributes such as
 * damage and purchase price. Concrete weapon types
 * (e.g., Sword, Bow, LegendaryWeapon) should extend this class.
 * </p>
 */
public abstract class Weapon extends Item implements Buyable {

    /** Damage value inflicted by this weapon */
    private final int damage;

    /** Purchase price of the weapon */
    private final int buyPrice;

    /**
     * Constructs a Weapon with name, image, buy price, and damage.
     *
     * @param name      the display name of the weapon
     * @param imgUrl    the resource path to the weapon image
     * @param buyPrice  the purchase price in in-game currency (must be non-negative)
     * @param damage    the damage value of the weapon (must be non-negative)
     *
     * @throws IllegalArgumentException if buyPrice or damage is negative
     */
    public Weapon(String name, String imgUrl, int buyPrice, int damage) {
        super(name, imgUrl);

        if (buyPrice < 0) {
            throw new IllegalArgumentException("Buy price must be non-negative.");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative.");
        }

        this.buyPrice = buyPrice;
        this.damage = damage;
    }

    /**
     * Returns the damage value of this weapon.
     *
     * @return damage value
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the purchase price of this weapon.
     *
     * @return buy price in in-game currency
     */
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }
}