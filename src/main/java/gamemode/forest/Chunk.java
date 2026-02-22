package gamemode.forest;

import gamemode.forest.entity.WorldItem;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private int chunkX, chunkY;

    private List<WorldItem> items = new ArrayList<>();

    public Chunk(int chunkX, int chunkY) {

        this.chunkX = chunkX;
        this.chunkY = chunkY;

        double baseX = chunkX * WorldManager.CHUNK_SIZE;
        double baseY = chunkY * WorldManager.CHUNK_SIZE;

        for (int i = 0; i < 3; i++) {
            items.add(new WorldItem(
                    baseX + Math.random() * WorldManager.CHUNK_SIZE,
                    baseY + Math.random() * WorldManager.CHUNK_SIZE
            ));
        }
    }

    public int getChunkX() { return chunkX; }
    public int getChunkY() { return chunkY; }

    public List<WorldItem> getItems() { return items; }
}