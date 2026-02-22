package scene;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.*;

public class CretaceousExplorationScene {

    private static final int WIDTH = 1422;
    private static final int HEIGHT = 800;
    private static final int WORLD_WIDTH = 5000;

    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;

    private Player player;
    private List<Platform> platforms = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<Dinosaur> dinosaurs = new ArrayList<>();

    private Set<KeyCode> keys = new HashSet<>();

    private double cameraX = 0;
    private final double GRAVITY = 0.7;

    private Runnable onBattleStart;
    private boolean battleTriggered = false;

    public CretaceousExplorationScene(Runnable onBattleStart) {
        this.onBattleStart = onBattleStart;

        StackPane root = new StackPane();
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scene = new Scene(root, WIDTH, HEIGHT);

        setupInput();
        setupWorld();
        startGameLoop();
    }

    public Scene getScene() {
        return scene;
    }

    /* =========================
       WORLD SETUP
     ========================= */

    private void setupWorld() {

        player = new Player(200, 500);

        // Ground
        platforms.add(new Platform(0, 700, WORLD_WIDTH, 100));

        // Floating platforms
        platforms.add(new Platform(600, 600, 200, 20));
        platforms.add(new Platform(1200, 550, 200, 20));
        platforms.add(new Platform(2000, 500, 200, 20));

        // Items
        items.add(new Item(650, 560));
        items.add(new Item(1250, 510));
        items.add(new Item(2050, 460));

        // Dinosaur
        dinosaurs.add(new Dinosaur(2500, 640));
    }

    /* =========================
       INPUT
     ========================= */

    private void setupInput() {
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));
    }

    /* =========================
       GAME LOOP
     ========================= */

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    /* =========================
       UPDATE
     ========================= */

    private void update() {

        // Horizontal movement
        if (keys.contains(KeyCode.A)) {
            player.vx = -6;
        } else if (keys.contains(KeyCode.D)) {
            player.vx = 6;
        } else {
            player.vx = 0;
        }

        // Jump
        if (keys.contains(KeyCode.SPACE) && player.onGround) {
            player.vy = -17;
            player.onGround = false;
        }

        // Apply gravity
        player.vy += GRAVITY;

        // Apply velocity
        player.x += player.vx;
        player.y += player.vy;

        // Platform collision
        player.onGround = false;

        for (Platform p : platforms) {

            if (intersects(
                    player.x, player.y, player.width, player.height,
                    p.x, p.y, p.width, p.height)) {

                // Only land if falling
                if (player.vy > 0 &&
                        player.y + player.height - player.vy <= p.y) {

                    player.y = p.y - player.height;
                    player.vy = 0;
                    player.onGround = true;
                }
            }
        }

        // Prevent falling below world
        if (player.y > HEIGHT) {
            player.y = 0;
        }

        // Item pickup
        items.removeIf(item -> {
            if (intersects(
                    player.x, player.y, player.width, player.height,
                    item.x, item.y, item.size, item.size)) {

                System.out.println("Item Collected!");
                return true;
            }
            return false;
        });

        // Dino trigger (only once)
        if (!battleTriggered) {
            for (Dinosaur d : dinosaurs) {
                if (intersects(
                        player.x, player.y, player.width, player.height,
                        d.x, d.y, d.width, d.height)) {

                    battleTriggered = true;
                    onBattleStart.run();
                }
            }
        }

        // Camera follow
        cameraX = player.x - WIDTH / 2.0;

        if (cameraX < 0) cameraX = 0;
        if (cameraX > WORLD_WIDTH - WIDTH)
            cameraX = WORLD_WIDTH - WIDTH;
    }

    /* =========================
       RENDER
     ========================= */

    private void render() {

        // Sky
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.save();
        gc.translate(-cameraX, 0);

        // Ground
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 700, WORLD_WIDTH, 100);

        // Platforms
        gc.setFill(Color.SADDLEBROWN);
        for (Platform p : platforms) {
            gc.fillRect(p.x, p.y, p.width, p.height);
        }

        // Items
        gc.setFill(Color.GOLD);
        for (Item item : items) {
            gc.fillOval(item.x, item.y, item.size, item.size);
        }

        // Dinosaurs
        gc.setFill(Color.RED);
        for (Dinosaur d : dinosaurs) {
            gc.fillRect(d.x, d.y, d.width, d.height);
        }

        // Player
        gc.setFill(Color.BLUE);
        gc.fillRect(player.x, player.y, player.width, player.height);

        gc.restore();
    }

    /* =========================
       AABB COLLISION
     ========================= */

    private boolean intersects(
            double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2) {

        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }

    /* =========================
       INNER CLASSES
     ========================= */

    private static class Player {
        double x, y;
        double vx = 0;
        double vy = 0;
        double width = 50;
        double height = 70;
        boolean onGround = false;

        Player(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Platform {
        double x, y, width, height;

        Platform(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }
    }

    private static class Item {
        double x, y;
        double size = 25;

        Item(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Dinosaur {
        double x, y;
        double width = 80;
        double height = 60;

        Dinosaur(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}