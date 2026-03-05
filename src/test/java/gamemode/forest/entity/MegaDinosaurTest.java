package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MegaDinosaurTest {

    @Test
    void testUpdateMovesTowardPlayer() {

        // Create MegaDinosaur far from player
        MegaDinosaur dino = new MegaDinosaur(
                "Mega", 100, 20, 50, 1,
                0, 0,
                Dinosaur.Rarity.RARE,
                "/item/test.png",
                50, 50,
                100
        );

        // Create player
        Player player = new Player(100,100);

        // Set player position
        player.setX(100);
        player.setY(100);

        double beforeX = dino.getX();
        double beforeY = dino.getY();

        // Call update
        dino.update(player);

        double afterX = dino.getX();
        double afterY = dino.getY();

        // Dinosaur should move closer to player
        assertTrue(afterX != beforeX || afterY != beforeY);
    }


    @Test
    void testUpdateMovesCorrectDirection() {

        MegaDinosaur dino = new MegaDinosaur(
                "Mega", 100, 20, 50, 1,
                0, 0,
                Dinosaur.Rarity.RARE,
                "/item/test.png",
                50, 50,
                100
        );

        Player player = new Player(200,200);
        player.setX(200);
        player.setY(200);

        dino.update(player);

        // Because player is at positive direction,
        // dino should move in positive X and Y
        assertTrue(dino.getX() > 0);
        assertTrue(dino.getY() > 0);
    }
}