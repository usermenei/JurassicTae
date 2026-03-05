package gamemode.gym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import gamemode.forest.util.Chunk;
import gamemode.forest.util.WorldManager;
import gamemode.forest.util.WorldPoint;
import gamemode.lobby.Player.Player;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gamemode.forest.entity.*;
import gamemode.lobby.Item.Potion.*;
import gamemode.lobby.logic.*;

public class WorldExploreTest {
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
    void testCollisionWithPlayer() {
        player.setX(150);
        player.setY(150);

        Dinosaur d = DinosaurFactory.createCarnivore("Raptor", 100, 100);

        boolean collide = world.isColliding(player, d);

        System.out.println(world.isColliding(player,d));

        assertTrue(collide);
    }

    @Test
    void testCreateRaptor() {
        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",100,100);

        assertEquals("Raptor", d.getName());
        assertEquals(150, d.getHp());
    }
    @Test
    void testInvalidDinosaur() {
        Dinosaur d = DinosaurFactory.createCarnivore("Dragon",100,100);

        assertEquals(null, d);
    }
    @Test
    void testAddDinosaur() {

        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",100,100);

        world.getDinosaurs().add(d);

        assertTrue(world.getDinosaurs().contains(d));
    }
    @Test
    void testNoCollision() {

        player.setX(0);
        player.setY(0);

        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",500,500);

        boolean collide = world.isColliding(player,d);

        assertFalse(collide);
    }
    @Test
    void testRemoveDinosaurFromChunk(){

        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",100,100);
        d.setChunk(0,0);

        world.getDinosaurs().add(d);

        world.removeDinosaur(d);

        assertFalse(world.getDinosaurs().contains(d));
    }

    @Test
    void testDinosaurSpawnPosition() {
        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",200,300);

        assertEquals(200, d.getX());
        assertEquals(300, d.getY());
    }
    @Test
    void testMultipleDinosaurs() {

        Dinosaur d1 = DinosaurFactory.createCarnivore("Raptor",100,100);
        Dinosaur d2 = DinosaurFactory.createCarnivore("Raptor",200,200);

        world.getDinosaurs().add(d1);
        world.getDinosaurs().add(d2);

        assertEquals(2, world.getDinosaurs().size());
    }

    @Test
    void testRemoveNonExistingDinosaur() {

        Dinosaur d = DinosaurFactory.createCarnivore("Raptor",100,100);

        world.removeDinosaur(d);

        assertFalse(world.getDinosaurs().contains(d));
    }

    @Test
    void testCreateWorldItem() {

        WorldItem item = new WorldItem(100, 200);

        assertEquals(100, item.getX());
        assertEquals(200, item.getY());
    }
    @Test
    void testAddWorldItemToChunk() {

        Chunk chunk = new Chunk(0,0);

        WorldItem item = new WorldItem(100,100);

        chunk.getItems().add(item);

        assertTrue(chunk.getItems().contains(item));
    }

    @Test
    void testRemoveWorldItemFromChunk() {

        Chunk chunk = new Chunk(0,0);

        WorldItem item = new WorldItem(100,100);

        chunk.getItems().add(item);
        chunk.getItems().remove(item);

        assertFalse(chunk.getItems().contains(item));
    }

    @Test
    void testItemNearPlayer() {

        player.setX(100);
        player.setY(100);

        WorldItem item = new WorldItem(110,110);

        boolean near = world.isNearPlayer(item);

        assertTrue(near);
    }

    @Test
    void testPlayerPickupItem() {

        Player player = GameLogic.getInstance().getPlayer();
        player.getInventory().clear();

        WorldManager world1 = new WorldManager(player);

        Chunk chunk = new Chunk(0,0);

        WorldItem worldItem = new WorldItem(110,110);

        chunk.getItems().add(worldItem);

        WorldPoint point = new WorldPoint(0,0);
        world1.getLoadedChunks().put(point, chunk);

        player.setX(100);
        player.setY(100);

        world1.handlePickup();

        assertEquals(1, player.getInventory().size());
    }

    @Test
    void testPickupWhenInventoryFull() {

        Player player = GameLogic.getInstance().getPlayer();
        player.getInventory().clear();

        WorldManager world1 = new WorldManager(player);

        // เติม inventory ให้เต็ม
        for (int i = 0; i < player.getInventorylimit(); i++) {
            player.getInventory().add(new HealPotion());
        }

        assertEquals(player.getInventorylimit(), player.getInventory().size());

        Chunk chunk = new Chunk(0,0);
        WorldItem worldItem = new WorldItem(100,100);

        chunk.getItems().add(worldItem);

        WorldPoint point = new WorldPoint(0,0);
        world1.getLoadedChunks().put(point, chunk);

        player.setX(100);
        player.setY(100);

        world1.handlePickup();

        // inventory ไม่ควรเพิ่ม
        assertEquals(player.getInventorylimit(), player.getInventory().size());

        // item ยังอยู่ใน chunk
        assertTrue(chunk.getItems().contains(worldItem));
    }

    @Test
    void testPickupWhenItemFar() {

        Player player = GameLogic.getInstance().getPlayer();
        player.getInventory().clear();

        WorldManager world1 = new WorldManager(player);

        Chunk chunk = new Chunk(0,0);
        WorldItem worldItem = new WorldItem(500,500);

        chunk.getItems().add(worldItem);

        WorldPoint point = new WorldPoint(0,0);
        world1.getLoadedChunks().put(point, chunk);

        player.setX(0);
        player.setY(0);

        world1.handlePickup();

        assertEquals(0, player.getInventory().size());
        assertTrue(chunk.getItems().contains(worldItem));
    }
}
