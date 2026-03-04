package gamemode.lobby.Item;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Item.Base.Item;

/**
 * Represents a DinoBall item used for capturing dinosaurs.
 *
 * <p>
 * DinoBall is a buyable item available in the shop.
 * It has a fixed purchase price and predefined image resource.
 * </p>
 */
public class DinoBall extends Item implements Buyable {

    /** Fixed buy price of the DinoBall */
    private static final int BUY_PRICE = 20;

    /**
     * Constructs a DinoBall item with predefined
     * name and image path.
     */
    public DinoBall() {
        super("DinoBall", "/item/dinoball.PNG");
    }

    /**
     * Returns the buy price of the DinoBall.
     *
     * @return buy price in in-game currency
     */
    @Override
    public int getBuyPrice() {
        return BUY_PRICE;
    }
}