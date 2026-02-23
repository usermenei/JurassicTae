package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.LivingThing.Player;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;

public class WorldManager {

    public static final int CHUNK_SIZE = 1024;
    private static final int RENDER_DISTANCE = 2;

    private Player player;

    private Map<WorldPoint, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private Set<WorldPoint> loadingChunks = ConcurrentHashMap.newKeySet();

    private List<Dinosaur> dinosaurs = new CopyOnWriteArrayList<>();
    private Set<WorldPoint> spawnedDinoChunks = ConcurrentHashMap.newKeySet();

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public WorldManager(Player player) {
        this.player = player;
    }

    public Collection<Chunk> getChunks() {
        return loadedChunks.values();
    }

    public List<Dinosaur> getDinosaurs() {
        return dinosaurs;
    }

    public void update() {

        int playerChunkX = (int)Math.floor(player.getX() / CHUNK_SIZE);
        int playerChunkY = (int)Math.floor(player.getY() / CHUNK_SIZE);

        Set<WorldPoint> activeChunks = new HashSet<>();

        // =========================
        // LOAD CHUNKS
        // =========================
        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {

                int cx = playerChunkX + x;
                int cy = playerChunkY + y;

                WorldPoint point = new WorldPoint(cx, cy);
                activeChunks.add(point);

                if (!loadedChunks.containsKey(point) && !loadingChunks.contains(point)) {

                    loadingChunks.add(point);

                    executor.submit(() -> {
                        Chunk chunk = new Chunk(cx, cy);

                        Platform.runLater(() -> {
                            loadedChunks.put(point, chunk);
                            loadingChunks.remove(point);
                        });
                    });
                }

                // Spawn dinosaur only once per chunk
                if (!spawnedDinoChunks.contains(point)) {
                    spawnDinosaur(cx, cy);
                    spawnedDinoChunks.add(point);
                }
            }
        }

        // =========================
        // UNLOAD FAR CHUNKS (DELETE ITEMS)
        // =========================
        loadedChunks.keySet().removeIf(point -> {

            if (!activeChunks.contains(point)) {

                Chunk chunk = loadedChunks.get(point);
                if (chunk != null) {
                    chunk.getItems().clear(); // delete items
                }

                spawnedDinoChunks.remove(point);
                return true;
            }
            return false;
        });

        // =========================
        // REMOVE DINOSAURS
        // =========================
        dinosaurs.removeIf(d ->
                d.isExpired() ||
                        !activeChunks.contains(new WorldPoint(d.getChunkX(), d.getChunkY()))
        );

        // =========================
        // UPDATE DINOSAURS
        // =========================
        for (Dinosaur d : dinosaurs) {
            d.update(player);
        }
    }

    private void spawnDinosaur(int chunkX, int chunkY) {

        double baseX = chunkX * CHUNK_SIZE;
        double baseY = chunkY * CHUNK_SIZE;

        if (Math.random() < 0.4) {

            Dinosaur dino = new Dinosaur(
                    baseX + Math.random() * CHUNK_SIZE,
                    baseY + Math.random() * CHUNK_SIZE
            );

            dino.setChunk(chunkX, chunkY);

            dinosaurs.add(dino);
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}