package gamemode.forest.render;

import gamemode.forest.Chunk;
import gamemode.forest.WorldManager;
import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.WorldItem;
import gamemode.forest.util.AssetLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import gamemode.lobby.Player.Player;

public class WorldRenderer {

    private GraphicsContext gc;
    private Player player;
    private WorldManager worldManager;

    private Image background;
    private Image dinosaurImage;

    public WorldRenderer(GraphicsContext gc,
                         Player player,
                         WorldManager worldManager) {

        this.gc = gc;
        this.player = player;
        this.worldManager = worldManager;

        background = AssetLoader.load("/gamemode/forest/background.jpg");
        dinosaurImage = AssetLoader.load("/gamemode/forest/dinosaur.png");
    }

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
        // 1️⃣ DRAW BACKGROUNDS (VISIBLE CHUNKS ONLY)
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
        // 2️⃣ DEPTH SORT ALL VISIBLE ENTITIES
        // =========================
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
        // 2.1️⃣ WORLD ITEMS (FROM CHUNKS)
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
// 2.2️⃣ GLOBAL DINOSAURS
// =========================
        double margin = 100;

        for (Dinosaur d : worldManager.getDinosaurs()) {

            double dx = d.getX();
            double dy = d.getY();

            double width  = d.getWidth();
            double height = d.getHeight();

            // Proper visibility check
            if (dx + width < viewLeft - margin ||
                    dx > viewRight + margin ||
                    dy + height < viewTop - margin ||
                    dy > viewBottom + margin) {
                continue;
            }

            double finalX = dx;
            double finalY = dy;

            renderList.add(new RenderObject(
                    finalY + height,  // ✅ correct depth
                    () -> {

                        if (d.getSprite() != null) {

                            gc.drawImage(
                                    d.getSprite(),
                                    finalX,
                                    finalY,
                                    width,
                                    height
                            );

                        } else {
                            gc.fillRect(finalX, finalY, width, height);
                        }
                    }
            ));
        }

        // =========================
        // 2.3️⃣ PLAYER (DEPTH SORTED)
        // =========================
        renderList.add(new RenderObject(
                player.getY() + player.getHeight(),
                () -> player.render(gc)
        ));

        // Sort by Y (top to bottom)
        renderList.sort((a, b) -> Double.compare(a.y, b.y));

        // Draw in correct order
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