package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.render.WorldRenderer;
import gamemode.DialogueManager;
import gamemode.lobby.Player.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CretaceousExplorationScene {

    private static final int WIDTH = 1422;
    private static final int HEIGHT = 800;

    private final Scene scene;
    private final Player player;

    private final Set<KeyCode> keys = new HashSet<>();

    private final WorldManager worldManager;
    private final WorldRenderer renderer;

    private double cameraX;
    private double cameraY;

    /* =========================
       ⭐ CALLBACKS (UPDATED)
       ========================= */
    private final Consumer<Dinosaur> onEnterBattle;
    private final Runnable onExitWorld;

    private AnimationTimer gameLoop;

    /* =========================
       CONSTRUCTOR
       ========================= */
    public CretaceousExplorationScene(
            Consumer<Dinosaur> onEnterBattle,
            Runnable onExitWorld
    ) {
        this.onEnterBattle = onEnterBattle;
        this.onExitWorld = onExitWorld;

        StackPane root = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scene = new Scene(root, WIDTH, HEIGHT);

        player = new Player(0, 0);

        worldManager = new WorldManager(player);
        renderer = new WorldRenderer(gc, player, worldManager);

        /* =========================
           ⭐ REGISTER BATTLE CALLBACK
           ========================= */
        worldManager.setOnBattleTriggered(enemy -> {
            if (gameLoop != null) {
                gameLoop.stop();           // ⏸ freeze world
            }
            onEnterBattle.accept(enemy);   // 🔥 send dinosaur to lobby GC
        });

        setupInput();
        startGameLoop(gc);
    }

    /* =========================
       GETTERS
       ========================= */
    public Scene getScene() {
        return scene;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    /* =========================
       INPUT
       ========================= */
    private void setupInput() {

        scene.setOnKeyPressed(e -> {

            keys.add(e.getCode());

            // Pick up item
            if (e.getCode() == KeyCode.E) {
                worldManager.handlePickup();
            }
        });

        scene.setOnKeyReleased(e ->
                keys.remove(e.getCode())
        );

        // Mouse click closes dialogue
        scene.setOnMousePressed(e ->
                DialogueManager.getInstance().onClick()
        );
    }

    /* =========================
       GAME LOOP
       ========================= */
    private void startGameLoop(GraphicsContext gc) {

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                update();

                // 1️⃣ Render world (camera space)
                renderer.render(cameraX, cameraY);

                // 2️⃣ Render dialogue (UI space)
                DialogueManager.getInstance().render(
                        gc,
                        WIDTH,
                        HEIGHT
                );
            }
        };

        gameLoop.start();
    }

    /* =========================
       UPDATE
       ========================= */
    private void update() {

        // Exit world
        if (keys.contains(KeyCode.ESCAPE)) {
            gameLoop.stop();
            worldManager.shutdown();
            onExitWorld.run();
            return;
        }

        // Dialogue active → freeze movement
        if (DialogueManager.getInstance().isActive()) {
            DialogueManager.getInstance().update();
            return;
        }

        double speed = 5;

        if (keys.contains(KeyCode.W)) player.setY(player.getY() - speed);
        if (keys.contains(KeyCode.S)) player.setY(player.getY() + speed);
        if (keys.contains(KeyCode.A)) player.setX(player.getX() - speed);
        if (keys.contains(KeyCode.D)) player.setX(player.getX() + speed);

        // Camera follows player
        cameraX = player.getX() - WIDTH / 2.0;
        cameraY = player.getY() - HEIGHT / 2.0;

        worldManager.update();
    }

    public void resumeWorld() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void clearInput() {
        keys.clear();
    }
}