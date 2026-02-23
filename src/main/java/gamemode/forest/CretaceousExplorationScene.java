package gamemode.forest;

import gamemode.forest.render.WorldRenderer;
import gamemode.DialogueManager;
import gamemode.lobby.LivingThing.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.HashSet;
import java.util.Set;

public class CretaceousExplorationScene {

    private static final int WIDTH = 1422;
    private static final int HEIGHT = 800;

    private Scene scene;
    private Player player;

    private Set<KeyCode> keys = new HashSet<>();

    private WorldManager worldManager;
    private WorldRenderer renderer;

    private double cameraX;
    private double cameraY;

    private Runnable onEnterBattle;
    private Runnable onExitWorld;

    private AnimationTimer gameLoop;

    public CretaceousExplorationScene(
            Runnable onEnterBattle,
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

        setupInput();
        startGameLoop(gc);
    }

    public Scene getScene() {
        return scene;
    }

    // =========================
    // INPUT
    // =========================
    private void setupInput() {

        scene.setOnKeyPressed(e -> {

            keys.add(e.getCode());

            if (e.getCode() == KeyCode.E) {
                worldManager.handlePickup();
            }
        });

        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

        // Mouse click closes dialogue
        scene.setOnMousePressed(e ->
                DialogueManager.getInstance().onClick()
        );
    }

    // =========================
    // GAME LOOP
    // =========================
    private void startGameLoop(GraphicsContext gc) {

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                update();

                // 1️⃣ Render world (camera space)
                renderer.render(cameraX, cameraY);

                // 2️⃣ Render dialogue (UI space, no camera)
                DialogueManager.getInstance().render(
                        gc,
                        WIDTH,
                        HEIGHT
                );
            }
        };

        gameLoop.start();
    }

    // =========================
    // UPDATE
    // =========================
    private void update() {

        // Exit world
        if (keys.contains(KeyCode.ESCAPE)) {
            gameLoop.stop();
            worldManager.shutdown();
            onExitWorld.run();
            return;
        }

        // If dialogue is active → freeze movement
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
        DialogueManager.getInstance().update();
    }
}