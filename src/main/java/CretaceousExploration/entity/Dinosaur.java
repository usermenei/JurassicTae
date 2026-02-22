package CretaceousExploration.entity;

import spawnscreen.LivingThing.Player;

public class Dinosaur {

    private double x, y;
    private static final double SPEED = 2;

    public Dinosaur(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(Player player) {

        double dx = player.getX() - x;
        double dy = player.getY() - y;

        double distSq = dx*dx + dy*dy;

        if(distSq > 1) {
            double inv = 1 / Math.sqrt(distSq);
            x += dx * inv * SPEED;
            y += dy * inv * SPEED;
        }
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
}