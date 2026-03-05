package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import javafx.scene.image.Image;

/**
 * Abstract base class representing a dinosaur entity in the forest game mode.
 * <p>
 * Each dinosaur has combat stats, positional data, a rarity classification,
 * and a limited lifetime after spawning. Subclasses must implement their own
 * AI behavior via {@link #update(Player)}.
 * </p>
 */
public abstract class Dinosaur {

    /** The display name of this dinosaur. */
    protected String name;

    /** The maximum hit points of this dinosaur. */
    protected int maxHp;

    /** The current hit points of this dinosaur. */
    protected int hp;

    /** The attack strength used when dealing damage to a player. */
    protected int strength;

    /** The amount of experience dropped when this dinosaur is defeated. */
    protected int expDrop;

    /** The minimum player level required to encounter this dinosaur. */
    protected int requiredLevel;

    /** The X coordinate of this dinosaur in the game world. */
    protected double x;

    /** The Y coordinate of this dinosaur in the game world. */
    protected double y;

    /** The movement speed of this dinosaur in units per update tick. */
    protected double speed;

    /** The system timestamp (in ms) when this dinosaur was spawned. */
    protected final long spawnTime;

    /** The duration in milliseconds before a dinosaur expires after spawning. */
    protected static final long LIFETIME = 60_000;

    /** The X index of the chunk this dinosaur currently occupies. */
    protected int chunkX;

    /** The Y index of the chunk this dinosaur currently occupies. */
    protected int chunkY;

    /** The width of this dinosaur's sprite, used for rendering and collision. */
    protected double width;

    /** The height of this dinosaur's sprite, used for rendering and collision. */
    protected double height;

    /** The gold value awarded to the player when this dinosaur is sold. */
    protected int sellPrice;

    /** The sprite image used to render this dinosaur. */
    protected Image sprite;

    /**
     * Defines the rarity tier of a dinosaur, which affects how frequently it spawns.
     */
    public enum Rarity {
        /** Spawns frequently. */
        COMMON,
        /** Spawns occasionally. */
        UNCOMMON,
        /** Spawns very rarely. */
        RARE
    }

    /** The rarity classification of this dinosaur. */
    protected Rarity rarity;

    /**
     * Constructs a new {@code Dinosaur} with the given attributes.
     *
     * @param name          the display name of the dinosaur
     * @param hp            the starting (and maximum) hit points
     * @param strength      the attack strength; clamped to a minimum of 0
     * @param expDrop       the experience rewarded on defeat; clamped to a minimum of 0
     * @param requiredLevel the minimum player level to encounter this dinosaur; clamped to a minimum of 1
     * @param x             the initial X position in the game world
     * @param y             the initial Y position in the game world
     * @param rarity        the rarity tier of this dinosaur
     * @param imagePath     the resource path to the sprite image
     * @param width         the width of the dinosaur's sprite
     * @param height        the height of the dinosaur's sprite
     * @param speed         the movement speed in units per update tick
     * @param sellPrice     the gold value when sold by the player
     */
    public Dinosaur(String name, int hp, int strength,
                    int expDrop, int requiredLevel,
                    double x, double y,
                    Rarity rarity,
                    String imagePath,
                    double width,
                    double height,
                    double speed,
                    int sellPrice) {

        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.strength = Math.max(0, strength);
        this.expDrop = Math.max(0, expDrop);
        this.requiredLevel = Math.max(1, requiredLevel);
        this.x = x;
        this.y = y;
        this.rarity = rarity;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.spawnTime = System.currentTimeMillis();
        this.sellPrice = sellPrice;
        loadImage(imagePath);
    }

    /**
     * Updates this dinosaur's behavior for the current game tick.
     * <p>
     * Each subclass implements its own AI logic here, such as chasing,
     * patrolling, or attacking the player.
     * </p>
     *
     * @param player the player entity to interact with
     */
    public abstract void update(Player player);

    /**
     * Returns whether this dinosaur has exceeded its maximum lifetime.
     *
     * @return {@code true} if the dinosaur has been alive longer than {@link #LIFETIME}
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > LIFETIME;
    }

    /**
     * Moves this dinosaur one step toward the given target coordinates.
     * <p>
     * Movement is normalized so the dinosaur always travels at exactly
     * {@link #speed} units per call, regardless of distance.
     * Does nothing if the dinosaur is already at the target position.
     * </p>
     *
     * @param targetX the target X coordinate
     * @param targetY the target Y coordinate
     */
    protected void moveToward(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distSq = dx * dx + dy * dy;

        if (distSq > 1) {
            double inv = 1 / Math.sqrt(distSq);
            x += dx * inv * speed;
            y += dy * inv * speed;
        }
    }

    /**
     * Loads the sprite image from the given resource path.
     * Logs a message to stdout if the image cannot be loaded.
     *
     * @param path the classpath-relative resource path to the image file
     */
    private void loadImage(String path) {
        try {
            sprite = new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Failed to load image: " + path);
        }
    }

    /**
     * Sets the chunk coordinates that this dinosaur currently occupies.
     *
     * @param chunkX the X index of the chunk
     * @param chunkY the Y index of the chunk
     */
    public void setChunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    /**
     * Returns the X index of the chunk this dinosaur occupies.
     *
     * @return the chunk X index
     */
    public int getChunkX() { return chunkX; }

    /**
     * Returns the Y index of the chunk this dinosaur occupies.
     *
     * @return the chunk Y index
     */
    public int getChunkY() { return chunkY; }

    /**
     * Returns the X position of this dinosaur in the game world.
     *
     * @return the X coordinate
     */
    public double getX() { return x; }

    /**
     * Returns the Y position of this dinosaur in the game world.
     *
     * @return the Y coordinate
     */
    public double getY() { return y; }

    /**
     * Returns the sprite image used to render this dinosaur.
     *
     * @return the sprite {@link Image}
     */
    public Image getSprite() { return sprite; }

    /**
     * Returns the display name of this dinosaur.
     *
     * @return the dinosaur's name
     */
    public String getName() { return name; }

    /**
     * Sets the display name of this dinosaur.
     * Defaults to {@code "Dinosaur"} if {@code null} is provided.
     *
     * @param name the new name, or {@code null} to use the default
     */
    public void setName(String name) {
        this.name = name == null ? "Dinosaur" : name;
    }
    /**
     * Sets the X coordinate of this dinosaur in the game world.
     *
     * @param x the new X position
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the Y coordinate of this dinosaur in the game world.
     *
     * @param y the new Y position
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the current hit points of this dinosaur.
     *
     * @return current HP (always &gt;= 0)
     */
    public int getHp() { return hp; }

    /**
     * Sets the current hit points, clamped to a minimum of 0.
     *
     * @param hp the new HP value
     */
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    /**
     * Returns the maximum hit points of this dinosaur.
     *
     * @return max HP
     */
    public int getMaxHp() { return maxHp; }

    /**
     * Returns the attack strength of this dinosaur.
     *
     * @return strength value (always &gt;= 0)
     */
    public int getStrength() { return strength; }

    /**
     * Sets the attack strength, clamped to a minimum of 0.
     *
     * @param strength the new strength value
     */
    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }

    /**
     * Returns the experience points dropped when this dinosaur is defeated.
     *
     * @return exp drop amount (always &gt;= 0)
     */
    public int getExpDrop() { return expDrop; }

    /**
     * Sets the experience drop amount, clamped to a minimum of 0.
     *
     * @param expDrop the new exp drop value
     */
    public void setExpDrop(int expDrop) {
        this.expDrop = Math.max(0, expDrop);
    }

    /**
     * Returns the minimum player level required to encounter this dinosaur.
     *
     * @return required level (always &gt;= 1)
     */
    public int getRequiredLevel() { return requiredLevel; }

    /**
     * Sets the required player level, clamped to a minimum of 1.
     *
     * @param requiredLevel the new required level
     */
    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(1, requiredLevel);
    }

    /**
     * Reduces this dinosaur's HP by the given damage amount.
     * HP is clamped to 0 and cannot go negative.
     *
     * @param damage the amount of damage to apply (should be &gt;= 0)
     */
    public void takeDamage(int damage) {
        setHp(getHp() - damage);
    }

    /**
     * Returns the width of this dinosaur's sprite.
     *
     * @return sprite width in pixels
     */
    public double getWidth() { return width; }

    /**
     * Returns the height of this dinosaur's sprite.
     *
     * @return sprite height in pixels
     */
    public double getHeight() { return height; }

    /**
     * Returns the gold value awarded when this dinosaur is sold.
     *
     * @return sell price in gold
     */
    public int getSellPrice() { return sellPrice; }

    /**
     * Returns the rarity tier of this dinosaur.
     *
     * @return the {@link Rarity} classification
     */
    public Rarity getRarity() { return rarity; }

    public void setWidth(int width){this.width = width;}
}