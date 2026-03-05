package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import java.util.Random;

/**
 * A carnivorous dinosaur that actively hunts the player when nearby,
 * and roams randomly when the player is out of range.
 * <p>
 * Behavior switches between two modes each {@link #update(Player)} tick:
 * </p>
 * <ul>
 *   <li><b>Aggro mode</b> – triggered when the player is within
 *       {@link #AGGRO_RANGE} units; the dinosaur moves directly toward
 *       the player's center.</li>
 *   <li><b>Roam mode</b> – when the player is out of range, the dinosaur
 *       drifts in a random direction, changing course every 1–3 seconds.</li>
 * </ul>
 */
public class CarnivoreDinosaur extends Dinosaur {

    /**
     * The distance in units within which this dinosaur detects and chases the player.
     */
    private static final double AGGRO_RANGE = 400;

    /** Random number generator used to pick roam directions and timings. */
    private final Random random = new Random();

    /** The X component of the current roam direction (range: -0.5 to 0.5). */
    private double roamDirX = 0;

    /** The Y component of the current roam direction (range: -0.5 to 0.5). */
    private double roamDirY = 0;

    /**
     * Countdown timer (in ticks) until the next roam direction change.
     * When it reaches 0, a new direction and duration are randomly chosen.
     */
    private int roamTimer = 0;

    /**
     * Constructs a {@code CarnivoreDinosaur} with fully specified attributes.
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
     * @param speed         the movement speed in units per update tick
     * @param sellPrice     the gold value when sold by the player
     */
    public CarnivoreDinosaur(String name, int hp, int strength,
                             int expDrop, int requiredLevel,
                             double x, double y,
                             Rarity rarity,
                             String imagePath,
                             double width,
                             double height,
                             double speed,
                             int sellPrice) {
        super(name, hp, strength, expDrop,
                requiredLevel, x, y,
                rarity, imagePath,
                width, height,
                speed, sellPrice);
    }

    /**
     * Constructs a default {@code CarnivoreDinosaur} preset as a Raptor.
     * <p>
     * Default stats: 120 HP, 30 strength, 100 EXP drop, required level 2,
     * spawned at the origin, {@link Rarity#COMMON}, speed 3.5, sell price 75.
     * </p>
     */
    public CarnivoreDinosaur() {
        super("Raptor",
                120, 30, 100, 2,
                0, 0,
                Dinosaur.Rarity.COMMON,
                "/images/dinosaur/raptor.gif",
                300, 300, 2, 75);
    }

    /**
     * Updates this dinosaur's position for the current game tick.
     * <p>
     * If the player is within {@link #AGGRO_RANGE} units, the dinosaur moves
     * toward the player's center at full speed. Otherwise, it roams in a
     * random direction at half speed, picking a new direction every 60–180 ticks.
     * </p>
     *
     * @param player the player entity used to determine aggro and target position
     */
    @Override
    public void update(Player player) {

        double playerCenterX = (player.getX() + player.getWidth() / 2.0)-100;
        double playerCenterY = (player.getY() + player.getHeight() / 2.0)-100;

        double dinoCenterX = x + width / 2.0;
        double dinoCenterY = y + height / 2.0;

        double dx = playerCenterX - dinoCenterX;
        double dy = playerCenterY - dinoCenterY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < AGGRO_RANGE) {

            // Chase player
            moveToward(playerCenterX, playerCenterY);

        } else {

            roamTimer--;

            if (roamTimer <= 0) {
                roamDirX = random.nextDouble() * 2 - 1;
                roamDirY = random.nextDouble() * 2 - 1;
                roamTimer = 60 + random.nextInt(120);
            }

            x += roamDirX * speed * 0.5;
            y += roamDirY * speed * 0.5;
        }
    }
}