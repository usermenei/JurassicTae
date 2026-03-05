package gamemode.forest.entity;

import gamemode.lobby.Player.Player;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarnivoreDinosaurTest {

    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }

    /**
     * Simple fake Player used only for testing.
     */
    static class TestPlayer extends Player {

        private double x;
        private double y;
        private double width = 100;
        private double height = 100;

        public TestPlayer(double x, double y) {
            super(0,0); // pass null because GameController is not needed for this test
            this.x = x;
            this.y = y;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }
    }

    /**
     * Test: Dinosaur moves when player is within aggro range.
     */
    @Test
    void testUpdate_PlayerOffsetMinus100_AffectsAggro() {

        CarnivoreDinosaur dino = new CarnivoreDinosaur();
        Player player = new Player(0,0);

        dino.setX(0);
        dino.setY(0);

        player.setX(350);
        player.setY(0);

        double beforeX = dino.getX();

        dino.update(player);

        double afterX = dino.getX();

        assertNotEquals(beforeX, afterX,
                "Dinosaur should move because -100 offset brings player into aggro range");
    }

    private double distance(CarnivoreDinosaur d, Player p) {
        double dx = p.getX() - d.getX();
        double dy = p.getY() - d.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }
    /**
     * Test: Dinosaur roams when player is far away.
     */
    @Test
    void testUpdate_PlayerOutOfRange() {

        CarnivoreDinosaur dino = new CarnivoreDinosaur();
        TestPlayer player = new TestPlayer(2000, 2000);

        dino.setX(0);
        dino.setY(0);

        double beforeX = dino.getX();
        double beforeY = dino.getY();

        dino.update(player);

        double afterX = dino.getX();
        double afterY = dino.getY();

        assertTrue(afterX != beforeX || afterY != beforeY,
                "Dinosaur should roam when player is out of aggro range");
    }

    /**
     * Test default constructor values.
     */
    @Test
    void testDefaultConstructor() {

        CarnivoreDinosaur dino = new CarnivoreDinosaur();

        assertEquals("Raptor", dino.getName());
        assertEquals(120, dino.getHp());
        assertEquals(30, dino.getStrength());
        assertEquals(100, dino.getExpDrop());
        assertEquals(2, dino.getRequiredLevel());
    }
}