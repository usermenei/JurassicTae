package gamemode.CretaceousExploration;

import gamemode.CretaceousExploration.entity.Dinosaur;
import javafx.application.Platform;
import gamemode.spawnscreen.LivingThing.Player;

import java.util.*;
import java.util.concurrent.*;

public class WorldManager {

    public static final int CHUNK_SIZE = 1024;
    private static final int RENDER_DISTANCE = 2;

    private Player player;

    private Map<WorldPoint, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private Set<WorldPoint> loadingChunks = ConcurrentHashMap.newKeySet();

    // ✅ GLOBAL dinosaurs list
    private List<Dinosaur> dinosaurs = new CopyOnWriteArrayList<>();

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public WorldManager(Player player) {
        this.player = player;
    }

    public Collection<Chunk> getChunks() {
        return loadedChunks.values();
    }

    // ✅ NEW getter for global dinosaurs
    public List<Dinosaur> getDinosaurs() {
        return dinosaurs;
    }

    public void update() {

        int playerChunkX = (int) Math.floor(player.getX() / CHUNK_SIZE);
        int playerChunkY = (int) Math.floor(player.getY() / CHUNK_SIZE);

        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {

                int cx = playerChunkX + x;
                int cy = playerChunkY + y;

                WorldPoint point = new WorldPoint(cx, cy);

                if (!loadedChunks.containsKey(point) && !loadingChunks.contains(point)) {

                    loadingChunks.add(point);

                    executor.submit(() -> {

                        Chunk chunk = new Chunk(cx, cy);

                        // ✅ Spawn dinosaur BASED on chunk, but store globally
                        spawnDinosaursForChunk(cx, cy);

                        Platform.runLater(() -> {
                            loadedChunks.put(point, chunk);
                            loadingChunks.remove(point);
                        });
                    });
                }
            }
        }

        // ✅ Update ALL global dinosaurs
        for (Dinosaur d : dinosaurs) {
            d.update(player);
        }
    }

    // ✅ NEW: spawn dinosaur tied to chunk location but stored globally
    private void spawnDinosaursForChunk(int chunkX, int chunkY) {

        double baseX = chunkX * CHUNK_SIZE;
        double baseY = chunkY * CHUNK_SIZE;

        if (Math.random() < 0.4) {

            Dinosaur dino = new Dinosaur(
                    baseX + Math.random() * CHUNK_SIZE,
                    baseY + Math.random() * CHUNK_SIZE
            );

            Platform.runLater(() -> dinosaurs.add(dino));
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}