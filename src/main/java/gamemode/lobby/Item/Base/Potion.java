package gamemode.lobby.Item.Base;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;

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
 * This class provides base pricing behavior for all potion types.
 * Concrete potion classes (e.g., HealthPotion, ManaPotion)
 * should extend this class and implement their specific effects.
 * </p>
 */
public abstract class Potion extends Item implements Buyable, Sellable {

    /** Purchase price of the potion */
    private final int buyPrice;

    /** Selling price of the potion */
    private final int sellPrice;

    /**
     * Constructs a Potion with name, image, and pricing information.
     *
     * @param name      the display name of the potion
     * @param imgUrl    the resource path to the potion image
     * @param buyPrice  the purchase price in in-game currency (should be non-negative)
     * @param sellPrice the selling price in in-game currency (should be non-negative)
     *
     * @throws IllegalArgumentException if buyPrice or sellPrice is negative
     */
    public Potion(String name, String imgUrl, int buyPrice, int sellPrice) {
        super(name, imgUrl);

        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Prices must be non-negative.");
        }

        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    /**
     * Returns the purchase price of this potion.
     *
     * @return buy price in in-game currency
     */
    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Returns the selling price of this potion.
     *
     * @return sell price in in-game currency
     */
    @Override
    public int getSellPrice() {
        return sellPrice;
    }
}