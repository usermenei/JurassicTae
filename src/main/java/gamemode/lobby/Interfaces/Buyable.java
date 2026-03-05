package gamemode.lobby.Interfaces;

/**
 * Represents an object that can be purchased in the game.
 *
 * <p>
 * Any class implementing this interface guarantees that
 * it provides a buy price value used in the shop or trading system.
 * </p>
 *
 * <p>
 * The returned price should represent the cost required
 * to purchase the item in in-game currency.
 * </p>
 */
public interface Buyable {

    /**
     * Returns the purchase price of this object.
     *
     * @return the buy price in in-game currency; should be non-negative
     */
    int getBuyPrice();

    /**
     * Returns a description of this object shown in the shop or trading system.
     *
     * @return a non-null string describing this purchasable object
     */
    String getDescription();
}