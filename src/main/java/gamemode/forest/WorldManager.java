package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.DinosaurFactory;
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
    private Dinosaur currentBattleDino = null;
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
                    currentBattleDino = d;
                    if (onBattleTriggered != null) {
                        onBattleTriggered.accept(d);
                    }
                    break;
                }
            }
        }
    }

    /* =========================
       ✅ REMOVE DINOSAUR (defeat / catch)
       ========================= */
    public void removeDinosaur(Dinosaur dino) {
        dinosaurs.remove(dino);

        WorldPoint chunkPoint = new WorldPoint(dino.getChunkX(), dino.getChunkY());
        spawnedDinoChunks.remove(chunkPoint);

        if (dino == currentBattleDino) {
            currentBattleDino = null;
        }
    }

    private void spawnDinosaur(int chunkX, int chunkY) {

        // 40% chance that a chunk even attempts to spawn
        if (Math.random() > 0.4) return;

        double spawnX = chunkX * CHUNK_SIZE + Math.random() * CHUNK_SIZE;
        double spawnY = chunkY * CHUNK_SIZE + Math.random() * CHUNK_SIZE;

        double roll = Math.random();
        Dinosaur dino;

        if (roll < 0.50) {
            // 🟢 COMMON
            if (Math.random() < 0.5) {
                dino = DinosaurFactory.createHerbivore("LongNeck", spawnX, spawnY);
            } else {
                dino = DinosaurFactory.createCarnivore("Raptor", spawnX, spawnY);
            }

        } else if (roll < 0.85) {
            // 🔵 UNCOMMON
            if (Math.random() < 0.5) {
                dino = DinosaurFactory.createHerbivore("Triceratops", spawnX, spawnY);
            } else {
                dino = DinosaurFactory.createCarnivore("TRex", spawnX, spawnY);
            }

        } else {
            // 🔴 RARE
            dino = DinosaurFactory.createMega(spawnX, spawnY);
        }

        if (dino != null) {
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

                    if (player.getInventory().size() >= GameLogic.getInstance().getPlayer().getInventorylimit()) {
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

        double dx = (p.getX() + p.getWidth() / 2.0) -
                (d.getX() + d.getWidth() / 2.0);

        double dy = (p.getY() + p.getHeight() / 2.0) -
                (d.getY() + d.getHeight() / 2.0);

        double shrinkFactor = 0.5;
        double combinedHalfWidths  = (p.getWidth() * shrinkFactor + d.getWidth() * shrinkFactor) / 2.0;
        double combinedHalfHeights = (p.getHeight() * shrinkFactor + d.getHeight() * shrinkFactor) / 2.0;

        return Math.abs(dx) < combinedHalfWidths &&
                Math.abs(dy) < combinedHalfHeights;
    }

    /* =========================
       🔙 END BATTLE (escape)
       — replaces damaged dino with a fresh one
       ========================= */
    public void endBattle() {
        inBattle = false;

        if (currentBattleDino != null) {
            int cx = currentBattleDino.getChunkX();
            int cy = currentBattleDino.getChunkY();

            dinosaurs.remove(currentBattleDino);
            spawnedDinoChunks.remove(new WorldPoint(cx, cy));
            spawnDinosaur(cx, cy); // fresh dino, full HP
            currentBattleDino = null;
        }

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

                player.setX(player.getX() + dx * 80);
                player.setY(player.getY() + dy * 80);

                break;
            }
        }
    }
}