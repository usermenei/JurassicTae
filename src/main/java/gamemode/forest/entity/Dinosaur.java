package gamemode.forest.entity;

import gamemode.lobby.Player.Player;

public class Dinosaur {
    private String name;
    private int hp;
    private int strength;
    private int expDrop;
    private int requiredLevel;

    private double x, y;
    private static final double SPEED = 2;

    // ✅ Lifetime system
    private final long spawnTime;
    private static final long LIFETIME = 60_000; // 1 minute in milliseconds

    // ✅ Chunk tracking (for unloading)
    private int chunkX;
    private int chunkY;

    public Dinosaur(String name, int hp, int strength,
                    int expDrop, int requiredLevel,
                    double x, double y) {
        setName(name);
        setHp(hp);
        setStrength(strength);
        setExpDrop(expDrop);
        setRequiredLevel(requiredLevel);
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
    }

    public void update(Player player) {

        double dx = player.getX() - x;
        double dy = player.getY() - y;

        double distSq = dx * dx + dy * dy;

        if (distSq > 1) {
            double inv = 1 / Math.sqrt(distSq);
            x += dx * inv * SPEED;
            y += dy * inv * SPEED;
        }
    }

    // ✅ Expiration check (1 minute)
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > LIFETIME;
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
}