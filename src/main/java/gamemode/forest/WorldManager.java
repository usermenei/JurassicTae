package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.WorldItem;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class WorldManager {

    public static final int CHUNK_SIZE = 1024;
    private static final int RENDER_DISTANCE = 2;

    private final Player player;

    private final Map<WorldPoint, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final Set<WorldPoint> loadingChunks = ConcurrentHashMap.newKeySet();

    private final List<Dinosaur> dinosaurs = new CopyOnWriteArrayList<>();
    private final Set<WorldPoint> spawnedDinoChunks = ConcurrentHashMap.newKeySet();

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /* =========================
       ⭐ Battle system
       ========================= */
    private boolean inBattle = false;
    private Consumer<Dinosaur> onBattleTriggered;

    public WorldManager(Player player) {
        this.player = player;
    }

    public Collection<Chunk> getChunks() {
        return loadedChunks.values();
    }

    public List<Dinosaur> getDinosaurs() {
        return dinosaurs;
    }

    public void setOnBattleTriggered(Consumer<Dinosaur> listener) {
        this.onBattleTriggered = listener;
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
                        Platform.runLater(() -> {
                            loadedChunks.put(point, chunk);
                            loadingChunks.remove(point);
                        });
                    });
                }

                if (!spawnedDinoChunks.contains(point)) {
                    spawnDinosaur(cx, cy);
                    spawnedDinoChunks.add(point);
                }
            }
        }

        for (Dinosaur d : dinosaurs) {
            d.update(player);
        }

        if (!inBattle) {
            for (Dinosaur d : dinosaurs) {
                if (isColliding(player, d)) {
                    inBattle = true;
                    if (onBattleTriggered != null) {
                        onBattleTriggered.accept(d);
                    }
                    break;
                }
            }
        }
    }

    /* =========================
       ✅ REMOVE DINOSAUR
       ========================= */
    public void removeDinosaur(Dinosaur dino) {
        // ลบไดโนออกจากโลก
        dinosaurs.remove(dino);

        // ปลดล็อก chunk นี้
        WorldPoint chunkPoint = new WorldPoint(dino.getChunkX(), dino.getChunkY());
        spawnedDinoChunks.remove(chunkPoint);
    }

    private void spawnDinosaur(int chunkX, int chunkY) {
        if (Math.random() < 0.4) {
            double randomNum = Math.random();
            Dinosaur dino;
            if (randomNum < 0.5) {
                dino = new Dinosaur(
                        "Noob", 20, 1, 10, 1,
                        chunkX * CHUNK_SIZE + Math.random() * CHUNK_SIZE,
                        chunkY * CHUNK_SIZE + Math.random() * CHUNK_SIZE
                );
            } else if (randomNum < 0.75) {
                dino = new Dinosaur(
                        "Pro Dino", 50, 5, 20, 1,
                        chunkX * CHUNK_SIZE + Math.random() * CHUNK_SIZE,
                        chunkY * CHUNK_SIZE + Math.random() * CHUNK_SIZE
                );
            } else {
                dino = new Dinosaur(
                        "Big Boss", 100, 30, 50, 1,
                        chunkX * CHUNK_SIZE + Math.random() * CHUNK_SIZE,
                        chunkY * CHUNK_SIZE + Math.random() * CHUNK_SIZE
                );
            }
            dino.setChunk(chunkX, chunkY);
            dinosaurs.add(dino);
        }
    }

    public void handlePickup() {

        Player player = GameLogic.getInstance().getPlayer();

        for (Chunk chunk : loadedChunks.values()) {
            Iterator<WorldItem> iterator = chunk.getItems().iterator();

            while (iterator.hasNext()) {
                WorldItem worldItem = iterator.next();

                if (isNearPlayer(worldItem)) {

                    if (player.getInventory().size() >= 8) {
                        gamemode.DialogueManager.getInstance().showDialogue(
                                "System",
                                "Your inventory is full!",
                                "/character/ptae.png"
                        );
                        return;
                    }

                    player.addItem(worldItem.getItem());
                    iterator.remove();

                    gamemode.DialogueManager.getInstance().showDialogue(
                            "System",
                            "Picked up " + worldItem.getItem().getName() + "!",
                            "/character/ptae.png"
                    );
                    return;
                }
            }
        }
    }

    private boolean isNearPlayer(WorldItem item) {
        double dx = player.getX() - item.getX();
        double dy = player.getY() - item.getY();
        return dx * dx + dy * dy <= 80 * 80;
    }

    private boolean isColliding(Player p, Dinosaur d) {
        return p.getX() < d.getX() + 64 &&
                p.getX() + 48 > d.getX() &&
                p.getY() < d.getY() + 64 &&
                p.getY() + 48 > d.getY();
    }

    public void endBattle() {
        inBattle = false;
        pushPlayerOut();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void pushPlayerOut() {

        for (Dinosaur d : dinosaurs) {

            if (isColliding(player, d)) {

                double dx = player.getX() - d.getX();
                double dy = player.getY() - d.getY();

                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) {
                    dx = 1;
                    dy = 0;
                    length = 1;
                }

                dx /= length;
                dy /= length;

                double pushDistance = 80;

                player.setX(player.getX() + dx * pushDistance);
                player.setY(player.getY() + dy * pushDistance);

                break;
            }
        }
    }
}