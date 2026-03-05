package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import java.util.Random;

/**
 * A herbivorous dinosaur that wanders the forest passively.
 * <p>
 * Unlike carnivores, this dinosaur ignores the player entirely and simply
 * drifts in a random direction, occasionally changing course. It always
 * moves at a fixed speed of 2 units per tick.
 * </p>
 */
public class HerbivoreDinosaur extends Dinosaur {

    /** Random number generator used to pick movement directions. */
    private final Random random = new Random();

    /** The X component of the current movement direction (range: -0.5 to 0.5). */
    private double directionX = 0;

    /** The Y component of the current movement direction (range: -0.5 to 0.5). */
    private double directionY = 0;

    /**
     * Constructs a {@code HerbivoreDinosaur} with fully specified attributes.
     * Movement speed is fixed at 2 units per tick.
     *
     * @param name          the display name of the dinosaur
     * @param hp            the starting and maximum hit points
     * @param strength      the attack strength; clamped to a minimum of 0
     * @param expDrop       the experience rewarded on defeat; clamped to a minimum of 0
     * @param requiredLevel the minimum player level to encounter this dinosaur; clamped to a minimum of 1
     * @param x             the initial X position in the game world
     * @param y             the initial Y position in the game world
     * @param rarity        the rarity tier of this dinosaur
     * @param imagePath     the classpath-relative path to the sprite image
     * @param width         the width of the dinosaur's sprite
     * @param height        the height of the dinosaur's sprite
     * @param sellPrice     the gold value when sold by the player
     */
    public HerbivoreDinosaur(String name, int hp, int strength,
                             int expDrop, int requiredLevel,
                             double x, double y,
                             Rarity rarity,
                             String imagePath, double width,
                             double height, int sellPrice) {
        super(name, hp, strength, expDrop,
                requiredLevel, x, y, rarity, imagePath, width, height, 2, sellPrice);
    }

    /**
     * Updates this dinosaur's position for the current game tick.
     * <p>
     * The player is not used in this implementation — herbivores are
     * non-aggressive and move independently. On average, the direction
     * changes once every 60 ticks (~1 second).
     * </p>
     *
     * @param player the player entity (unused by this implementation)
     */
    @Override
    public void update(Player player) {
        if (random.nextInt(60) == 0) {
            directionX = random.nextDouble() - 0.5;
            directionY = random.nextDouble() - 0.5;
        }

        x += directionX * speed;
        y += directionY * speed;
    }
}