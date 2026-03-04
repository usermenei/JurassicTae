package gamemode.lobby.Interfaces;

/**
 * Represents an object that can be sold by the player in the game.
 *
 * <p>
 * Any class implementing this interface guarantees that
 * it provides a sell price value used in the shop or trading system.
 * </p>
 *
 * <p>
 * The returned price represents the amount of in-game currency
 * the player receives when selling the object.
 * </p>
 */
public interface Sellable {

    /**
     * Returns the selling price of this object.
     *
     * @return the sell price in in-game currency.
     *         This value should be non-negative.
     */
    int getSellPrice();
}