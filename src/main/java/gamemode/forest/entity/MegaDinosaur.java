package gamemode.forest.entity;

import gamemode.lobby.Player.Player;

public class MegaDinosaur extends Dinosaur {

    public MegaDinosaur(String name, int hp, int strength,
                        int expDrop, int requiredLevel,
                        double x, double y,
                        Rarity rarity,
                        String imagePath,double width,
                        double height) {

        super(name, hp, strength, expDrop,
                requiredLevel, x, y, rarity, imagePath, width,height,1.5);
    }

    @Override
    public void update(Player player) {
        moveToward(player.getX(), player.getY());
    }
}