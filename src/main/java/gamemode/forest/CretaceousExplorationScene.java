package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.render.WorldRenderer;
import gamemode.DialogueManager;
import gamemode.lobby.Player.Player;
import gamemode.lobby.Scene.InventoryPane;
import gamemode.lobby.logic.GameLogic;
import gamemode.lobby.logic.KeyboardController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class
CretaceousExplorationScene {

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
    private InventoryPane inventoryPane;
    private StackPane uiLayer;
    private VBox inventoryPopup;
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
        uiLayer = new StackPane();

        root.getChildren().addAll(canvas, uiLayer);
        Button inventoryButton = new Button("Inventory");
        inventoryButton.setLayoutX(20);
        inventoryButton.setLayoutY(20);

        inventoryButton.setOnAction(e -> toggleInventory());
        inventoryPane = new InventoryPane();
        inventoryPane.setVisible(false);
        uiLayer.getChildren().addAll(inventoryButton,inventoryPane);
        StackPane.setAlignment(inventoryButton,Pos.TOP_RIGHT);
        StackPane.setMargin(inventoryButton, new Insets(20));
        StackPane.setAlignment(inventoryPane, Pos.CENTER);

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

        scene.setOnKeyReleased(e -> {

            keys.remove(e.getCode());

            if (e.getCode() == KeyCode.TAB) {
                toggleInventory();
            }
        });

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

        player.updateBuffs();
        // Dialogue active → freeze movement
        if (DialogueManager.getInstance().isActive()) {
            DialogueManager.getInstance().update();
            return;
        }

        double speed = 5;

        boolean moving = false;

        if (keys.contains(KeyCode.A)) {
            player.setX(player.getX() - speed);
            player.setMoving(true);
            player.setFacingRight(false);   // 👈 flip left
            moving = true;
        }

        if (keys.contains(KeyCode.D)) {
            player.setX(player.getX() + speed);
            player.setMoving(true);
            player.setFacingRight(true);    // 👈 face right
            moving = true;
        }

        if (keys.contains(KeyCode.W)) {
            player.setY(player.getY() - speed);
            player.setMoving(true);
            moving = true;
        }

        if (keys.contains(KeyCode.S)) {
            player.setY(player.getY() + speed);
            player.setMoving(true);
            moving = true;
        }

        if (!moving) {
            player.setMoving(false);
        }

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

    private void toggleInventory() {
        boolean isOpen = inventoryPane.isVisible();

        if (!isOpen) {
            inventoryPane.loadItems();
        }

        inventoryPane.setVisible(!isOpen);
    }
}