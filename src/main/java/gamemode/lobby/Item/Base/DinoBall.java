package gamemode.lobby.Item.Base;

import gamemode.lobby.Item.Interfaces.Buyable;

/**
 * Represents a DinoBall item used for capturing dinosaurs.
 *
 * <p>
 * DinoBall is a buyable item available in the shop.
 * It has a fixed purchase price and predefined image resource.
 * </p>
 */
public class DinoBall extends Item implements Buyable {

    /** The fixed cost to purchase a DinoBall in the shop. */
    private static final int BUY_PRICE = 20;

    /** A short description of the DinoBall's purpose, shown in the shop. */
    private String description;

    /**
     * Constructs a DinoBall with a predefined name, image path,
     * and default description of {@code "Catch Dinosaur"}.
     */
    public DinoBall() {
        super("DinoBall", "/item/dinoball.png");
        setDescription("Catch Dinosaur");
    }

    /**
     * Returns the buy price of the DinoBall.
     *
     * @return the buy price in in-game currency
     */
    @Override
    public int getBuyPrice() {
        return BUY_PRICE;
    }

    /**
     * Returns the description of this DinoBall shown in the shop.
     *
     * @return a non-null string describing this item's purpose
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this DinoBall.
     *
     * @param description the new description string; should not be {@code null}
     */
    public void setDescription(String description) {
        this.description = description;
    }
}