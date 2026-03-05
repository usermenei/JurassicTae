package gamemode.lobby.Item.Base;

import gamemode.lobby.Item.Interfaces.Buyable;
import gamemode.lobby.Item.Interfaces.Sellable;

/**
 * Abstract base class representing a potion item in the game.
 *
 * <p><b>Description:</b></p>
 * A Potion is:
 * <ul>
 *     <li>An {@link Item}</li>
 *     <li>{@link Buyable}</li>
 *     <li>{@link Sellable}</li>
 * </ul>
 *
 * <p>
 * This class provides base pricing behaviour for all potion types.
 * Concrete potion classes (e.g., {@link gamemode.lobby.Item.Potion.HealPotion},
 * {@link gamemode.lobby.Item.Potion.SpeedPotion}) should extend this class
 * and implement their specific effects.
 * </p>
 *
 * @see Buyable
 * @see Sellable
 * @see Item
 */
public abstract class Potion extends Item implements Buyable, Sellable {

    /** The purchase price of this potion in in-game currency. Always &gt;= 0. */
    private final int buyPrice;

    /** The selling price of this potion in in-game currency. Always &gt;= 0. */
    private final int sellPrice;

    /** A short description of this potion's effect, shown in the shop or inventory. */
    private final String description;

    /**
     * Constructs a Potion with name, image, pricing, and description.
     *
     * @param name        the display name of the potion
     * @param imgUrl      the resource path to the potion image
     * @param buyPrice    the purchase price in in-game currency; must be &gt;= 0
     * @param sellPrice   the selling price in in-game currency; must be &gt;= 0
     * @param description a short description of the potion's effect
     * @throws IllegalArgumentException if {@code buyPrice} or {@code sellPrice} is negative
     */
    public Potion(String name, String imgUrl, int buyPrice, int sellPrice, String description) {
        super(name, imgUrl);

        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Prices must be non-negative.");
        }

        this.buyPrice    = buyPrice;
        this.sellPrice   = sellPrice;
        this.description = description;
    }

    /**
     * Returns the purchase price of this potion.
     *
     * @return the buy price in in-game currency (always &gt;= 0)
     */
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Returns the selling price of this potion.
     *
     * @return the sell price in in-game currency (always &gt;= 0)
     */
    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Returns the description of this potion shown in the shop or inventory.
     *
     * @return a non-null string describing this potion's effect
     */
    @Override
    public String getDescription() {
        return description;
    }
}