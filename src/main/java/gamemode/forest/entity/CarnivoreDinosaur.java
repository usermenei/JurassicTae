package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import java.util.Random;

public class CarnivoreDinosaur extends Dinosaur {

    private static final double AGGRO_RANGE = 400;

    private final Random random = new Random();

    private double roamDirX = 0;
    private double roamDirY = 0;
    private int roamTimer = 0;

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
                speed,sellPrice);
    }

    public CarnivoreDinosaur() {
        super("Raptor",
                120, 30, 100, 2,
                0, 0,
                Dinosaur.Rarity.COMMON,
                "/images/dinosaur/raptor.gif"
                ,300,300,3.5,75);
    }

    @Override
    public void update(Player player) {

        double dx = player.getX() - x;
        double dy = player.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // ========================
        // 🔴 AGGRO MODE
        // ========================
        if (distance < AGGRO_RANGE) {

            moveToward(player.getX(), player.getY());

        } else {

            // ========================
            // 🟢 ROAM MODE
            // ========================

            roamTimer--;

            if (roamTimer <= 0) {
                roamDirX = random.nextDouble() - 0.5;
                roamDirY = random.nextDouble() - 0.5;
                roamTimer = 60 + random.nextInt(120); // change direction every ~1-3 sec
            }

            x += roamDirX * speed * 0.5;
            y += roamDirY * speed * 0.5;
        }
    }
}