package gamemode.forest.render;

import gamemode.forest.Chunk;
import gamemode.forest.WorldManager;
import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.WorldItem;
import gamemode.forest.util.AssetLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import gamemode.lobby.Player.Player;

/**
 * Handles all rendering for the forest game world each frame.
 * <p>
 * Rendering is performed in three ordered passes:
 * <ol>
 *   <li>Background tiles for all visible {@link Chunk}s.</li>
 *   <li>All visible entities (world items, dinosaurs, and the player)
 *       depth-sorted by their bottom Y coordinate to create a convincing
 *       top-down perspective.</li>
 *   <li>The dialogue overlay rendered on top of the world.</li>
 * </ol>
 * Only entities within or near the camera viewport are rendered each frame
 * to avoid unnecessary draw calls.
 * </p>
 */
public class WorldRenderer {

    /** The graphics context used to issue all draw calls. */
    private GraphicsContext gc;

    /** The player entity, included in depth-sorted rendering each frame. */
    private Player player;

    /** The world manager providing access to chunks and dinosaur lists. */
    private WorldManager worldManager;

    /** The background tile image drawn once per visible chunk. */
    private Image background;

    /** Reserved dinosaur image asset (loaded but used as a fallback). */
    private Image dinosaurImage;

    /**
     * Constructs a {@code WorldRenderer} and pre-loads required image assets.
     *
     * @param gc           the {@link GraphicsContext} to draw on
     * @param player       the player entity to include in each render pass
     * @param worldManager the world manager providing chunks and dinosaurs
     */
    public WorldRenderer(GraphicsContext gc,
                         Player player,
                         WorldManager worldManager) {
        this.gc = gc;
        this.player = player;
        this.worldManager = worldManager;
        background = AssetLoader.load("/gamemode/forest/background.jpg");
        dinosaurImage = AssetLoader.load("/gamemode/forest/dinosaur.png");
    }

    /**
     * Renders a complete frame of the game world at the given camera position.
     * <p>
     * The canvas is cleared, then the camera transform is applied before drawing.
     * Only chunks and entities that intersect the current viewport (with a small
     * margin for dinosaurs) are drawn. World items, dinosaurs, and the player are
     * depth-sorted by their bottom Y edge so that entities lower on screen appear
     * in front of those higher up. The dialogue overlay is drawn last, without the
     * camera transform, so it always appears at a fixed screen position.
     * </p>
     *
     * @param cameraX the X offset of the camera in world coordinates
     * @param cameraY the Y offset of the camera in world coordinates
     */
    public void render(double cameraX, double cameraY) {

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        gc.save();
        gc.translate(-cameraX, -cameraY);

        double viewLeft   = cameraX;
        double viewRight  = cameraX + canvasWidth;
        double viewTop    = cameraY;
        double viewBottom = cameraY + canvasHeight;

        // =========================
        // 1. DRAW BACKGROUNDS (VISIBLE CHUNKS ONLY)
        // =========================
        for (Chunk chunk : worldManager.getChunks()) {

            double baseX = chunk.getChunkX() * WorldManager.CHUNK_SIZE;
            double baseY = chunk.getChunkY() * WorldManager.CHUNK_SIZE;

            if (baseX + WorldManager.CHUNK_SIZE < viewLeft ||
                    baseX > viewRight ||
                    baseY + WorldManager.CHUNK_SIZE < viewTop ||
                    baseY > viewBottom) {
                continue;
            }

            gc.drawImage(background,
                    baseX,
                    baseY,
                    WorldManager.CHUNK_SIZE,
                    WorldManager.CHUNK_SIZE);
        }

        // =========================
        // 2. DEPTH SORT ALL VISIBLE ENTITIES
        // =========================

        /**
         * A lightweight container pairing a Y depth value with a deferred draw call,
         * used to sort all visible entities before rendering.
         */
        class RenderObject {
            double y;
            Runnable draw;

            RenderObject(double y, Runnable draw) {
                this.y = y;
                this.draw = draw;
            }
        }

        java.util.List<RenderObject> renderList = new java.util.ArrayList<>();

        // =========================
        // 2.1. WORLD ITEMS (FROM CHUNKS)
        // =========================
        for (Chunk chunk : worldManager.getChunks()) {

            double baseX = chunk.getChunkX() * WorldManager.CHUNK_SIZE;
            double baseY = chunk.getChunkY() * WorldManager.CHUNK_SIZE;

            if (baseX + WorldManager.CHUNK_SIZE < viewLeft ||
                    baseX > viewRight ||
                    baseY + WorldManager.CHUNK_SIZE < viewTop ||
                    baseY > viewBottom) {
                continue;
            }

            for (WorldItem item : chunk.getItems()) {

                if (item.getX() + 40 < viewLeft ||
                        item.getX() > viewRight ||
                        item.getY() + 40 < viewTop ||
                        item.getY() > viewBottom) {
                    continue;
                }

                renderList.add(new RenderObject(
                        item.getY(),
                        () -> gc.drawImage(item.getImage(),
                                item.getX(),
                                item.getY(),
                                80, 80)
                ));
            }
        }

        // =========================
        // 2.2. GLOBAL DINOSAURS
        // =========================
        double margin = 100;

        for (Dinosaur d : worldManager.getDinosaurs()) {

            double dx = d.getX();
            double dy = d.getY();
            double width  = d.getWidth();
            double height = d.getHeight();

            if (dx + width < viewLeft - margin ||
                    dx > viewRight + margin ||
                    dy + height < viewTop - margin ||
                    dy > viewBottom + margin) {
                continue;
            }

            double finalX = dx;
            double finalY = dy;

            renderList.add(new RenderObject(
                    finalY + height,
                    () -> {
                        if (d.getSprite() != null) {
                            gc.drawImage(d.getSprite(), finalX, finalY, width, height);
                        } else {
                            gc.fillRect(finalX, finalY, width, height);
                        }
                    }
            ));
        }

        // =========================
        // 2.3. PLAYER (DEPTH SORTED)
        // =========================
        renderList.add(new RenderObject(
                player.getY() + player.getHeight(),
                () -> player.render(gc)
        ));

        // Sort by bottom Y edge (top to bottom)
        renderList.sort((a, b) -> Double.compare(a.y, b.y));

        for (RenderObject ro : renderList) {
            ro.draw.run();
        }

        gc.restore();

        gamemode.DialogueManager
                .getInstance()
                .render(gc,
                        gc.getCanvas().getWidth(),
                        gc.getCanvas().getHeight());
    }


}