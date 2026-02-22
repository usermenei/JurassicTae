package scene;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import spawnscreen.Item.Base.Item;
import spawnscreen.Item.Potion.HealPotion;
import spawnscreen.LivingThing.Player;

import java.util.*;

public class CretaceousExplorationScene {

    private static final int WIDTH = 1422;
    private static final int HEIGHT = 800;

    private static final int CHUNK_SIZE = 1024;
    private static final int RENDER_DISTANCE = 2;

    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;

    private Player player;
    private Set<KeyCode> keys = new HashSet<>();

    private double cameraX = 0;
    private double cameraY = 0;

    // ✅ LOAD ONCE
    private Image background;
    private static Image ITEM_IMAGE;
    private static Image DINOSAUR_IMAGE;

    private Runnable onEnterBattle;
    private Runnable onExitWorld;

    private Map<Point, Chunk> loadedChunks = new HashMap<>();

    public CretaceousExplorationScene(
            Runnable onEnterBattle,
            Runnable onExitWorld
    ) {
        this.onEnterBattle = onEnterBattle;
        this.onExitWorld = onExitWorld;

        StackPane root = new StackPane();
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scene = new Scene(root, WIDTH, HEIGHT);

        loadAssets();

        player = new Player(0, 0);

        setupInput();
        startGameLoop();
    }

    public Scene getScene() {
        return scene;
    }

    private void loadAssets() {

        background = loadImage("/CretaceousExploration/background.jpg");

        // ✅ LOAD ONCE ONLY
        ITEM_IMAGE = loadImage("/trap.png");
        DINOSAUR_IMAGE = loadImage("/CretaceousExploration/dinosaur.png");
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null)
            throw new RuntimeException("Missing resource: " + path);
        return new Image(url.toExternalForm());
    }

    private void setupInput() {
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    private void update() {

        if (keys.contains(KeyCode.ESCAPE)) {
            onExitWorld.run();
            return;
        }

        double speed = 5;

        if (keys.contains(KeyCode.W)) player.setY(player.getY() - speed);
        if (keys.contains(KeyCode.S)) player.setY(player.getY() + speed);
        if (keys.contains(KeyCode.A)) player.setX(player.getX() - speed);
        if (keys.contains(KeyCode.D)) player.setX(player.getX() + speed);

        cameraX = player.getX() - WIDTH / 2.0;
        cameraY = player.getY() - HEIGHT / 2.0;

        updateChunks();
        for (Chunk chunk : loadedChunks.values()) {
            for (Dinosaur d : chunk.dinosaurs) {
                d.update(player);
            }
        }
        checkItemPickup();
    }

    private void checkItemPickup() {

        if (!keys.contains(KeyCode.E)) return;

        double px = player.getX();
        double py = player.getY();

        for (Chunk chunk : loadedChunks.values()) {

            Iterator<WorldItem> iterator = chunk.items.iterator();

            while (iterator.hasNext()) {

                WorldItem wi = iterator.next();

                double dx = px - wi.x;
                double dy = py - wi.y;

                double distSq = dx * dx + dy * dy;

                if (distSq < 60*60) { // pickup range

                    player.addItem(wi.item);
                    iterator.remove(); // remove from world
                    return; // pick only one per press
                }
            }
        }
    }

    private void updateChunks() {

        int playerChunkX = (int)Math.floor(player.getX() / CHUNK_SIZE);
        int playerChunkY = (int)Math.floor(player.getY() / CHUNK_SIZE);

        Set<Point> needed = new HashSet<>();

        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {

                int cx = playerChunkX + x;
                int cy = playerChunkY + y;

                Point p = new Point(cx, cy);
                needed.add(p);

                if (!loadedChunks.containsKey(p)) {
                    loadedChunks.put(p, new Chunk(cx, cy));
                }
            }
        }

        // ✅ REMOVE FAR CHUNKS
        if (loadedChunks.size() > 200) {
            loadedChunks.clear(); // safety limit
        }
    }

    private void render() {

        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.save();
        gc.translate(-cameraX, -cameraY);

        for (Chunk chunk : loadedChunks.values()) {

            double baseX = chunk.chunkX * CHUNK_SIZE;
            double baseY = chunk.chunkY * CHUNK_SIZE;

            // Draw background per chunk
            gc.drawImage(background, baseX, baseY, CHUNK_SIZE, CHUNK_SIZE);

            for (WorldItem wi : chunk.items) {
                gc.drawImage(ITEM_IMAGE, wi.x, wi.y, 40, 40);
            }

            for (Dinosaur d : chunk.dinosaurs) {
                gc.drawImage(DINOSAUR_IMAGE, d.x, d.y, 80, 60);
            }
        }

        player.render(gc);

        gc.restore();
    }


    /* ========================= */

    private static class Point {
        int x, y;
        Point(int x, int y){ this.x=x; this.y=y; }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Point)) return false;
            Point p=(Point)o;
            return x==p.x && y==p.y;
        }

        @Override
        public int hashCode(){
            return Objects.hash(x,y);
        }
    }

    private class Chunk {

        int chunkX, chunkY;

        List<WorldItem> items = new ArrayList<>();
        List<Dinosaur> dinosaurs = new ArrayList<>();

        Chunk(int chunkX, int chunkY) {

            this.chunkX = chunkX;
            this.chunkY = chunkY;

            double baseX = chunkX * CHUNK_SIZE;
            double baseY = chunkY * CHUNK_SIZE;

            // Spawn items
            for (int i = 0; i < 3; i++) {
                items.add(new WorldItem(
                        baseX + Math.random() * CHUNK_SIZE,
                        baseY + Math.random() * CHUNK_SIZE
                ));
            }

            // Spawn dinosaur (40% chance)
            if (Math.random() < 0.4) {
                dinosaurs.add(new Dinosaur(
                        baseX + Math.random() * CHUNK_SIZE,
                        baseY + Math.random() * CHUNK_SIZE
                ));
            }
        }
    }

    private static class WorldItem {
        double x, y;
        Item item;
        Image image;

        WorldItem(double x, double y) {

            this.x = x;
            this.y = y;

            // randomly spawn potion
            this.item = new HealPotion();  // you can random later
            this.image = ((HealPotion) item).getImage();
        }
    }

    private static class Dinosaur {

        double x, y;
        private static final double SPEED = 2.0;

        Dinosaur(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void update(Player player) {

            double dx = player.getX() - x;
            double dy = player.getY() - y;

            double distSq = dx * dx + dy * dy;
            if (distSq > 1) {
                double inv = 1 / Math.sqrt(distSq);
                x += dx * inv * SPEED;
                y += dy * inv * SPEED;
            }
        }
    }
}