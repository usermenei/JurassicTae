package gamemode.forest.util;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.DinosaurFactory;
import gamemode.forest.entity.WorldItem;
import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Logic tests for WorldManager without mocking frameworks.
 */
public class WorldManagerTest {

    private Player player;
    private WorldManager worldManager;

    @BeforeEach
    void setup() {
        player = new Player(100,100);
        player.setX(100);
        player.setY(100);

        worldManager = new WorldManager(player);
    }

    /**
     * Test that loaded chunks map is initially empty.
     */
    @Test
    void testLoadedChunksInitiallyEmpty() {
        Map<WorldPoint, Chunk> chunks = worldManager.getLoadedChunks();
        assertNotNull(chunks);
        assertEquals(0, chunks.size());
    }

    /**
     * Test dinosaur list initially empty.
     */
    @Test
    void testDinosaursInitiallyEmpty() {
        assertNotNull(worldManager.getDinosaurs());
        assertTrue(worldManager.getDinosaurs().isEmpty());
    }

    /**
     * Test isNearPlayer when item is within pickup range.
     */
    @Test
    void testIsNearPlayerTrue() {

        WorldItem item = new WorldItem(110, 110);

        boolean result = worldManager.isNearPlayer(item);

        assertTrue(result);
    }

    /**
     * Test isNearPlayer when item is far away.
     */
    @Test
    void testIsNearPlayerFalse() {

        WorldItem item = new WorldItem(1000, 1000);

        boolean result = worldManager.isNearPlayer(item);

        assertFalse(result);
    }

    /**
     * Test collision detection when player overlaps dinosaur.
     */
    @Test
    void testIsCollidingTrue() {

        // player position
        player.setX(0);
        player.setY(0);

        // create dinosaur
        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 120, 120);

        // force dinosaur to overlap player
        dino.setX(-166.785);
        dino.setY(-30.599);

        boolean result = worldManager.isColliding(player, dino);

        assertTrue(result);
    }
    /**
     * Test collision detection when player is far from dinosaur.
     */
    @Test
    void testIsCollidingFalse() {

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 2000, 2000);

        boolean result = worldManager.isColliding(player, dino);

        assertFalse(result);
    }

    /**
     * Test removeDinosaur removes it from list.
     */
    @Test
    void testRemoveDinosaur() {

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 100, 100);

        worldManager.getDinosaurs().add(dino);

        assertEquals(1, worldManager.getDinosaurs().size());

        worldManager.removeDinosaur(dino);

        assertEquals(0, worldManager.getDinosaurs().size());
    }

    /**
     * Test removing dinosaur that is not in list.
     */
    @Test
    void testRemoveNonExistingDinosaur() {

        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 100, 100);

        worldManager.removeDinosaur(dino);

        assertTrue(worldManager.getDinosaurs().isEmpty());
    }

}