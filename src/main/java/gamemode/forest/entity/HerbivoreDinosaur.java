package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import java.util.Random;

public class HerbivoreDinosaur extends Dinosaur {

    private final Random random = new Random();
    private double directionX = 0;
    private double directionY = 0;

    public HerbivoreDinosaur(String name, int hp, int strength,
                             int expDrop, int requiredLevel,
                             double x, double y,
                             Rarity rarity,
                             String imagePath,double width,
                             double height) {

        super(name, hp, strength, expDrop,
                requiredLevel, x, y, rarity, imagePath,width, height,2);
    }

    @Override
    public void update(Player player) {

        // Random movement
        if (random.nextInt(60) == 0) { // change direction occasionally
            directionX = random.nextDouble() - 0.5;
            directionY = random.nextDouble() - 0.5;
        }

        x += directionX * speed;
        y += directionY * speed;
    }
}