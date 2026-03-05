package gamemode.lobby.Item.Base;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Interfaces.Sellable;

/**
 * Represents a tamed dinosaur that can be treated as a sellable item.
 *
 * <p>
 * A TamedDinosaur wraps a {@link Dinosaur} entity and allows it to be
 * stored in the inventory and sold in the shop system.
 * </p>
 *
 * <p>
 * The sell price is derived from the original dinosaur's sell value
 * at the time of taming.
 * </p>
 */
public class TamedDinosaur extends Item implements Sellable {

    /** Selling price of the tamed dinosaur */
    private final int sellPrice;

    /** The wrapped dinosaur entity */
    private final Dinosaur dinosaur;

    /**
     * Constructs a TamedDinosaur from an existing Dinosaur.
     *
     * @param dinosaur the dinosaur to be tamed and wrapped as an item
     * @throws IllegalArgumentException if dinosaur is null
     */
    public TamedDinosaur(Dinosaur dinosaur) {
        super(
                "Tamed " + validateDinosaur(dinosaur).getName(),
                "/item/TamedDinosaur/Tamed" + dinosaur.getName() + ".png"
        );

        this.dinosaur = dinosaur;
        this.sellPrice = dinosaur.getSellPrice();
    }

    /**
     * Returns the sell price of this tamed dinosaur.
     *
     * @return sell price in in-game currency
     */
    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Returns the wrapped dinosaur entity.
     *
     * @return Dinosaur object
     */
    public Dinosaur getDinosaur() {
        return dinosaur;
    }

    /**
     * Validates that the dinosaur is not null.
     */
    private static Dinosaur validateDinosaur(Dinosaur dinosaur) {
        if (dinosaur == null) {
            throw new IllegalArgumentException("Dinosaur cannot be null.");
        }
        return dinosaur;
    }
}