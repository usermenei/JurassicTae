package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import javafx.scene.image.Image;

public abstract class Dinosaur {

    protected String name;
    protected int hp;
    protected int strength;
    protected int expDrop;
    protected int requiredLevel;

    protected double x, y;
    protected double speed;

    protected final long spawnTime;
    protected static final long LIFETIME = 60_000;

    protected int chunkX;
    protected int chunkY;
    protected double width;
    protected double height;

    public Image getSprite() {
        return sprite;
    }

    protected Image sprite;   // ✅ Image

    // Rarity system
    public enum Rarity {
        COMMON,     // Generate often
        UNCOMMON,   // Not very often
        RARE        // Very rare
    }

    protected Rarity rarity;

    public Dinosaur(String name, int hp, int strength,
                    int expDrop, int requiredLevel,
                    double x, double y,
                    Rarity rarity,
                    String imagePath,
                    double width,
                    double height,
                    double speed) {

        this.name = name;
        this.hp = Math.max(0, hp);
        this.strength = Math.max(0, strength);
        this.expDrop = Math.max(0, expDrop);
        this.requiredLevel = Math.max(1, requiredLevel);
        this.x = x;
        this.y = y;
        this.rarity = rarity;

        this.width = width;
        this.height = height;
        this.speed = speed;  // ✅ store speed

        this.spawnTime = System.currentTimeMillis();

        loadImage(imagePath);
    }

    // Each type will implement its own AI
    public abstract void update(Player player);

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > LIFETIME;
    }

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
    private void loadImage(String path) {
        try {
            sprite = new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Failed to load image: " + path);
        }
    }

    // ✅ Chunk setters/getters
    public void setChunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    // Position getters
    public double getX() { return x; }
    public double getY() { return y; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "Dinosaur" : name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }

    public int getExpDrop() {
        return expDrop;
    }

    public void setExpDrop(int expDrop) {
        this.expDrop = Math.max(0, expDrop);
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(1, requiredLevel);
    }

    public void takeDamage(int damage) {
        setHp(getHp()-damage);
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
}