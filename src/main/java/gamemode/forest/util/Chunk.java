package gamemode.forest.util;

import gamemode.forest.entity.WorldItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fixed-size square region of the game world.
 * <p>
 * The world is divided into a grid of chunks, each identified by its
 * column and row index ({@code chunkX}, {@code chunkY}). On creation,
 * a chunk has a 25% chance of spawning a single {@link WorldItem} at a
 * random position within its bounds.
 * </p>
 *
 * @see WorldManager#CHUNK_SIZE
 */
public class Chunk {

    /** The column index of this chunk in the world grid. */
    private int chunkX;

    /** The row index of this chunk in the world grid. */
    private int chunkY;

    /** The list of world items currently present in this chunk. */
    private List<WorldItem> items = new ArrayList<>();

    /**
     * Constructs a {@code Chunk} at the given grid indices and randomly
     * populates it with a {@link WorldItem}.
     * <p>
     * There is a 25% chance that one item will be spawned at a uniformly
     * random position within the chunk's bounds. If the roll fails, the
     * chunk spawns with no items.
     * </p>
     *
     * @param chunkX the column index of this chunk in the world grid
     * @param chunkY the row index of this chunk in the world grid
     */
    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        double baseX = chunkX * WorldManager.CHUNK_SIZE;
        double baseY = chunkY * WorldManager.CHUNK_SIZE;

        if (Math.random() < 0.25) {
            items.add(new WorldItem(
                    baseX + Math.random() * WorldManager.CHUNK_SIZE,
                    baseY + Math.random() * WorldManager.CHUNK_SIZE
            ));
        }
    }

    /**
     * Returns the column index of this chunk in the world grid.
     *
     * @return the chunk's X index
     */
    public int getChunkX() { return chunkX; }

    /**
     * Returns the row index of this chunk in the world grid.
     *
     * @return the chunk's Y index
     */
    public int getChunkY() { return chunkY; }

    /**
     * Returns the list of world items currently present in this chunk.
     *
     * @return a mutable {@link List} of {@link WorldItem}s; may be empty
     */
    public List<WorldItem> getItems() { return items; }
}