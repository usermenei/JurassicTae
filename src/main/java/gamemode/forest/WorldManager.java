package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.DinosaurFactory;
import gamemode.forest.entity.WorldItem;
import gamemode.lobby.DialogueManager;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages the state of the forest game world, including chunk loading,
 * dinosaur spawning, item pickup, collision detection, and battle triggering.
 * <p>
 * The world is divided into square {@link Chunk}s of size {@link #CHUNK_SIZE}.
 * Each update tick, chunks within {@link #RENDER_DISTANCE} of the player are
 * loaded asynchronously. Dinosaurs are spawned once per chunk when it first
 * enters render distance, and despawned when defeated or caught.
 * </p>
 *
 * <p>Dinosaur spawn probabilities per chunk:</p>
 * <ul>
 *   <li>25% base chance that a chunk attempts to spawn any dinosaur.</li>
 *   <li>50% — Common (LongNeck or Raptor, chosen randomly)</li>
 *   <li>35% — Uncommon (Triceratops or T-Rex, chosen randomly)</li>
 *   <li>15% — Rare (Ancient Colossus / MegaDinosaur)</li>
 * </ul>
 */
public class WorldManager {

    /** The side length in pixels of each square world chunk. */
    public static final int CHUNK_SIZE = 1024;

    /** The radius in chunks around the player that are kept loaded. */
    private static final int RENDER_DISTANCE = 2;

    /** The player entity used for chunk loading, collision, and pickup range checks. */
    private final Player player;

    /** All currently loaded chunks, keyed by their grid coordinate. */
    private final Map<WorldPoint, Chunk> loadedChunks = new ConcurrentHashMap<>();

    /** Grid coordinates of chunks that are currently being loaded asynchronously. */
    private final Set<WorldPoint> loadingChunks = ConcurrentHashMap.newKeySet();

    /** All dinosaurs currently active in the world. */
    private final List<Dinosaur> dinosaurs = new CopyOnWriteArrayList<>();

    /** Grid coordinates of chunks that have already had a dinosaur spawn attempted. */
    private final Set<WorldPoint> spawnedDinoChunks = ConcurrentHashMap.newKeySet();

    /** Thread pool used for asynchronous chunk generation. */
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Whether a battle is currently in progress.
     * Prevents multiple battles from triggering simultaneously.
     */
    private boolean inBattle = false;

    /** The dinosaur that triggered the current battle, or {@code null} if not in battle. */
    private Dinosaur currentBattleDino = null;

    /** Callback invoked on the JavaFX thread when a battle is triggered. */
    private Consumer<Dinosaur> onBattleTriggered;

    /**
     * Constructs a {@code WorldManager} for the given player.
     *
     * @param player the player entity to track for chunk loading and interactions
     */
    public WorldManager(Player player) {
        this.player = player;
    }

    /**
     * Returns all currently loaded chunks.
     *
     * @return a collection of active {@link Chunk} instances
     */
    public Collection<Chunk> getChunks() {
        return loadedChunks.values();
    }

    /**
     * Returns the list of all dinosaurs currently active in the world.
     *
     * @return a thread-safe list of {@link Dinosaur} instances
     */
    public List<Dinosaur> getDinosaurs() {
        return dinosaurs;
    }

    /**
     * Registers a callback to be invoked when the player collides with a dinosaur.
     * The callback receives the dinosaur that triggered the battle.
     *
     * @param listener the {@link Consumer} to call on battle trigger
     */
    public void setOnBattleTriggered(Consumer<Dinosaur> listener) {
        this.onBattleTriggered = listener;
    }

    /**
     * Processes one game tick for the world.
     * <p>
     * Each tick this method:
     * <ol>
     *   <li>Loads any unloaded chunks within {@link #RENDER_DISTANCE} of the player,
     *       asynchronously via the thread pool.</li>
     *   <li>Attempts to spawn a dinosaur in each newly entered chunk.</li>
     *   <li>Updates every active dinosaur's AI via {@link Dinosaur#update(Player)}.</li>
     *   <li>Checks for player-dinosaur collisions and triggers a battle if one is found
     *       and no battle is already in progress.</li>
     * </ol>
     * </p>
     */
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

    /**
     * Removes a defeated or caught dinosaur from the world and allows its
     * chunk to spawn a new dinosaur in a future update.
     *
     * @param dino the {@link Dinosaur} to remove
     */
    public void removeDinosaur(Dinosaur dino) {
        dinosaurs.remove(dino);

        WorldPoint chunkPoint = new WorldPoint(dino.getChunkX(), dino.getChunkY());
        spawnedDinoChunks.remove(chunkPoint);

        if (dino == currentBattleDino) {
            currentBattleDino = null;
        }
    }

    /**
     * Attempts to spawn a dinosaur in the given chunk.
     * <p>
     * There is a 25% base chance that any spawn occurs. If it does, the
     * dinosaur type is chosen by rarity roll: 50% common, 35% uncommon, 15% rare.
     * The dinosaur is placed at a random position within the chunk bounds.
     * </p>
     *
     * @param chunkX the column index of the chunk
     * @param chunkY the row index of the chunk
     */
    private void spawnDinosaur(int chunkX, int chunkY) {

        if (Math.random() > 0.25) return;

        double spawnX = chunkX * CHUNK_SIZE + Math.random() * CHUNK_SIZE;
        double spawnY = chunkY * CHUNK_SIZE + Math.random() * CHUNK_SIZE;

        double roll = Math.random();
        Dinosaur dino;

        if (roll < 0.50) {
            dino = Math.random() < 0.5
                    ? DinosaurFactory.createHerbivore("LongNeck", spawnX, spawnY)
                    : DinosaurFactory.createCarnivore("Raptor", spawnX, spawnY);

        } else if (roll < 0.85) {
            dino = Math.random() < 0.5
                    ? DinosaurFactory.createHerbivore("Triceratops", spawnX, spawnY)
                    : DinosaurFactory.createCarnivore("TRex", spawnX, spawnY);

        } else {
            dino = DinosaurFactory.createMega(spawnX, spawnY);
        }

        if (dino != null) {
            dino.setChunk(chunkX, chunkY);
            dinosaurs.add(dino);
        }
    }

    /**
     * Attempts to pick up the nearest {@link WorldItem} within range of the player.
     * <p>
     * Iterates all loaded chunks and removes the first item within 80 units of the
     * player, adding it to the player's inventory. Shows a dialogue if the inventory
     * is full or on successful pickup. Does nothing if no item is in range.
     * </p>
     */
    public void handlePickup() {

        Player player = GameLogic.getInstance().getPlayer();

        for (Chunk chunk : loadedChunks.values()) {
            Iterator<WorldItem> iterator = chunk.getItems().iterator();

            while (iterator.hasNext()) {
                WorldItem worldItem = iterator.next();

                if (isNearPlayer(worldItem)) {

                    if (player.getInventory().size() >= GameLogic.getInstance().getPlayer().getInventorylimit()) {
                        DialogueManager.getInstance().showDialogue(
                                "System",
                                "Your inventory is full!",
                                "/character/ptae.png"
                        );
                        return;
                    }

                    player.addItem(worldItem.getItem());
                    iterator.remove();

                    DialogueManager.getInstance().showDialogue(
                            "System",
                            "Picked up " + worldItem.getItem().getName() + "!",
                            "/character/ptae.png"
                    );
                    return;
                }
            }
        }
    }

    /**
     * Returns whether the given world item is within pickup range of the player.
     * Pickup range is 80 units (compared using squared distance for efficiency).
     *
     * @param item the {@link WorldItem} to check
     * @return {@code true} if the item is within 80 units of the player's position
     */
    private boolean isNearPlayer(WorldItem item) {
        double dx = player.getX() - item.getX();
        double dy = player.getY() - item.getY();
        return dx * dx + dy * dy <= 80 * 80;
    }

    /**
     * Returns whether the player and a dinosaur are overlapping using
     * axis-aligned bounding box collision with a 50% shrink factor applied
     * to both entities, reducing false positives from large sprites.
     *
     * @param p the player entity
     * @param d the dinosaur entity
     * @return {@code true} if their shrunk bounding boxes overlap
     */
    private boolean isColliding(Player p, Dinosaur d) {

        double dx = (p.getX() + p.getWidth() / 2.0) -
                (d.getX() + d.getWidth() / 2.0);

        double dy = (p.getY() + p.getHeight() / 2.0) -
                (d.getY() + d.getHeight() / 2.0);

        double shrinkFactor = 0.5;
        double combinedHalfWidths  = (p.getWidth()  * shrinkFactor + d.getWidth()  * shrinkFactor) / 2.0;
        double combinedHalfHeights = (p.getHeight() * shrinkFactor + d.getHeight() * shrinkFactor) / 2.0;

        return Math.abs(dx) < combinedHalfWidths &&
                Math.abs(dy) < combinedHalfHeights;
    }

    /**
     * Ends the current battle and resets battle state.
     * <p>
     * Removes the battle dinosaur from the world, clears its chunk's spawn record
     * so a fresh dinosaur can appear, then calls {@link #pushPlayerOut()} to ensure
     * the player is not immediately overlapping any remaining dinosaur.
     * </p>
     */
    public void endBattle() {
        inBattle = false;

        if (currentBattleDino != null) {
            int cx = currentBattleDino.getChunkX();
            int cy = currentBattleDino.getChunkY();

            dinosaurs.remove(currentBattleDino);
            spawnedDinoChunks.remove(new WorldPoint(cx, cy));
            spawnDinosaur(cx, cy);
            currentBattleDino = null;
        }

        pushPlayerOut();
    }

    /**
     * Shuts down the chunk-loading thread pool immediately.
     * Should be called when leaving the forest scene to release resources.
     */
    public void shutdown() {
        executor.shutdownNow();
    }

    /**
     * Nudges the player 80 units away from the first dinosaur they are
     * still colliding with after a battle ends, preventing an immediate
     * re-trigger of the battle system.
     * <p>
     * If the player is exactly on top of a dinosaur, they are pushed
     * horizontally to the right as a fallback.
     * </p>
     */
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