package gamemode.gym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gamemode.forest.*;
import gamemode.forest.entity.*;

public class testForest {
    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already started
        }
    }

    WorldManager world;
    Player player;

    @BeforeEach
    void setup(){
        player = new Player(0,0);
        world = new WorldManager(player);
    }

    @Test
    void testRemoveDinosaur() {
        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor",100,100);
        dino.setChunk(0,0);

        world.getDinosaurs().add(dino);

        world.removeDinosaur(dino);

        assertFalse(world.getDinosaurs().contains(dino));
    }

    @Test
    void testBattleTrigger() {
        player.setX(100);
        player.setY(100);

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor",100,100);
        dino.setChunk(0,0);

        world.getDinosaurs().add(dino);

        final boolean[] triggered = {false};

        world.setOnBattleTriggered(d -> triggered[0] = true);

        world.update();

        assertTrue(triggered[0]);
    }

    @Test
    void testEndBattleRemovesDinosaur() {
        player.setX(100);
        player.setY(100);

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor",100,100);
        dino.setChunk(0,0);

        world.getDinosaurs().add(dino);

        world.setOnBattleTriggered(d -> {});
        world.update();

        world.endBattle();

        assertFalse(world.getDinosaurs().contains(dino));
    }

    @Test
    void testPlayerPushedAfterBattle() {
        player.setX(100);
        player.setY(100);

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor",100,100);
        dino.setChunk(0,0);

        world.getDinosaurs().add(dino);

        world.setOnBattleTriggered(d -> {});
        world.update();

        world.endBattle();

        assertNotEquals(100, player.getX());
    }

    @Test
    void testShutdown() {
        world.shutdown();

        assertTrue(true);
    }
}
