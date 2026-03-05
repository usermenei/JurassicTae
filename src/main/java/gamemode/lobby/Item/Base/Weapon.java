package gamemode.lobby.Item.Base;

import gamemode.lobby.Item.Interfaces.Buyable;

/**
 * Abstract base class representing a weapon item in the game.
 *
 * <p><b>Description:</b></p>
 * A Weapon:
 * <ul>
 *     <li>Extends {@link Item}</li>
 *     <li>Implements {@link Buyable}</li>
 * </ul>
 *
 * <p>
 * This class provides core weapon attributes such as damage and purchase price.
 * Concrete weapon types (e.g., {@link gamemode.lobby.Item.Weapon.RifleGun},
 * {@link gamemode.lobby.Item.Weapon.ElectricGun}) should extend this class.
 * </p>
 *
 * @see Buyable
 * @see Item
 */
public abstract class Weapon extends Item implements Buyable {

    /** The damage value inflicted by this weapon in combat. Always &gt;= 0. */
    private final int damage;

    /** The purchase price of this weapon in in-game currency. Always &gt;= 0. */
    private final int buyPrice;

    /**
     * A short description of this weapon's stats, automatically set to
     * {@code "damage: <value>"} on construction.
     */
    private final String description;

    /**
     * Constructs a Weapon with name, image, buy price, and damage.
     * The description is automatically set to {@code "damage: <value>"}.
     *
     * @param name     the display name of the weapon
     * @param imgUrl   the resource path to the weapon image
     * @param buyPrice the purchase price in in-game currency; must be &gt;= 0
     * @param damage   the damage value of the weapon; must be &gt;= 0
     * @throws IllegalArgumentException if {@code buyPrice} or {@code damage} is negative
     */
    public Weapon(String name, String imgUrl, int buyPrice, int damage) {
        super(name, imgUrl);

        if (buyPrice < 0) {
            throw new IllegalArgumentException("Buy price must be non-negative.");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative.");
        }

        this.buyPrice    = buyPrice;
        this.damage      = damage;
        this.description = "damage: " + this.getDamage();
    }

    /**
     * Returns the damage value of this weapon.
     *
     * @return the damage value (always &gt;= 0)
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the purchase price of this weapon.
     *
     * @return the buy price in in-game currency (always &gt;= 0)
     */
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Returns the description of this weapon shown in the shop or inventory.
     * The description is automatically formatted as {@code "damage: <value>"}.
     *
     * @return a non-null string describing this weapon's damage stat
     */
    @Override
    public String getDescription() {
        return description;
    }
}