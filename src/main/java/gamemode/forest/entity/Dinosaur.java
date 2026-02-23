package gamemode.forest.entity;

import gamemode.lobby.LivingThing.Player;

public class Dinosaur {

    private double x, y;
    private static final double SPEED = 2;

    // ✅ Lifetime system
    private final long spawnTime;
    private static final long LIFETIME = 60_000; // 1 minute in milliseconds

    // ✅ Chunk tracking (for unloading)
    private int chunkX;
    private int chunkY;

    public Dinosaur(double x, double y) {
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
}