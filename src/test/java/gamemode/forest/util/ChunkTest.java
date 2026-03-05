package gamemode.forest.util;

import gamemode.forest.entity.WorldItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Chunk}.
 *
 * <p>These tests verify that chunk coordinates are stored correctly,
 * items spawn within valid bounds, and that the item list behaves
 * as expected even with randomized spawning.</p>
 */
public class ChunkTest {

    /**
     * Tests that chunk coordinates are stored correctly.
     */
    @Test
    void testChunkCoordinates() {
        Chunk chunk = new Chunk(3, 5);

        assertEquals(3, chunk.getChunkX());
        assertEquals(5, chunk.getChunkY());
    }

    /**
     * Tests that the items list is never null.
     */
    @Test
    void testItemsListNotNull() {
        Chunk chunk = new Chunk(0, 0);

        assertNotNull(chunk.getItems());
    }

    /**
     * Tests that the number of items spawned is valid.
     *
     * Because spawning is random (25% chance), the list
     * should contain either 0 or 1 item.
     */
    @Test
    void testItemSpawnCount() {
        Chunk chunk = new Chunk(1, 1);

        int size = chunk.getItems().size();

        assertTrue(size == 0 || size == 1);
    }

    /**
     * Tests that spawned items are within chunk boundaries.
     */
    @Test
    void testItemPositionWithinChunk() {

        Chunk chunk = new Chunk(2, 3);
        List<WorldItem> items = chunk.getItems();

        double baseX = 2 * WorldManager.CHUNK_SIZE;
        double baseY = 3 * WorldManager.CHUNK_SIZE;

        for (WorldItem item : items) {

            assertTrue(item.getX() >= baseX);
            assertTrue(item.getX() <= baseX + WorldManager.CHUNK_SIZE);

            assertTrue(item.getY() >= baseY);
            assertTrue(item.getY() <= baseY + WorldManager.CHUNK_SIZE);
        }
    }

    /**
     * Tests that multiple chunk creations behave consistently
     * and never spawn more than one item.
     */
    @Test
    void testMultipleChunkCreation() {

        for (int i = 0; i < 100; i++) {

            Chunk chunk = new Chunk(i, i);
            int size = chunk.getItems().size();

            assertTrue(size == 0 || size == 1);
        }
    }
}