package gamemode.forest.entity;

import gamemode.lobby.Player.Player;

/**
 * A large, powerful dinosaur that relentlessly pursues the player at all times.
 * <p>
 * Unlike {@link CarnivoreDinosaur}, which only chases within aggro range,
 * a {@code MegaDinosaur} always moves toward the player's center regardless
 * of distance. It moves at a fixed speed of 1.5 units per tick, trading
 * speed for raw power via higher HP and strength.
 * </p>
 */
public class MegaDinosaur extends Dinosaur {

    /**
     * Constructs a {@code MegaDinosaur} with fully specified attributes.
     * Movement speed is fixed at 1.5 units per tick.
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
    public MegaDinosaur(String name, int hp, int strength,
                        int expDrop, int requiredLevel,
                        double x, double y,
                        Rarity rarity,
                        String imagePath, double width,
                        double height, int sellPrice) {
        super(name, hp, strength, expDrop,
                requiredLevel, x, y, rarity, imagePath, width, height, 1.5, sellPrice);
    }

    /**
     * Updates this dinosaur's position for the current game tick.
     * <p>
     * Unconditionally moves toward the player's center coordinates,
     * with no aggro range check or idle roaming behaviour.
     * </p>
     *
     * @param player the player entity to pursue
     */
    @Override
    public void update(Player player) {
        double playerCenterX = player.getX() + player.getWidth() / 2.0;
        double playerCenterY = player.getY() + player.getHeight() / 2.0;
        moveToward(playerCenterX, playerCenterY);
    }
}