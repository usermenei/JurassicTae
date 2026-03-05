package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.render.WorldRenderer;
import gamemode.forest.util.WorldManager;
import gamemode.lobby.DialogueManager;
import gamemode.lobby.Player.Player;
import gamemode.lobby.Scene.InventoryPane;
import gamemode.lobby.logic.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The main JavaFX scene for the Cretaceous forest exploration game mode.
 * <p>
 * This scene manages the full game loop, player input, camera, world rendering,
 * inventory UI, and dialogue system. It serves as the top-level coordinator
 * between all forest subsystems.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Constructing and laying out the canvas, UI layer, and inventory panel.</li>
 *   <li>Running a JavaFX {@link AnimationTimer} game loop that updates and renders
 *       the world each frame.</li>
 *   <li>Handling keyboard input for player movement (WASD), item pickup (E),
 *       inventory toggle (TAB), and exit (ESC).</li>
 *   <li>Centering the camera on the player each frame.</li>
 *   <li>Showing a one-time introductory dialogue the first time the scene is entered.</li>
 *   <li>Delegating to {@code onEnterBattle} when a dinosaur encounter is triggered,
 *       and to {@code onExitWorld} when the player presses ESC.</li>
 * </ul>
 */
public class CretaceousExplorationScene {

    /** The width of the game canvas in pixels. */
    private static final int WIDTH  = 1422;

    /** The height of the game canvas in pixels. */
    private static final int HEIGHT = 800;

    /**
     * Tracks whether the one-time forest intro dialogue has already been shown.
     * Static so it persists across re-entries without resetting until a full restart.
     */
    private static boolean hasShownForestIntro = false;

    /** The JavaFX scene containing the canvas and UI layer. */
    private final Scene scene;

    /** The player entity controlled by keyboard input. */
    private final Player player;

    /** The set of keyboard keys currently held down this frame. */
    private final Set<KeyCode> keys = new HashSet<>();

    /** Manages chunk loading, dinosaur spawning, item pickup, and battle triggers. */
    private final WorldManager worldManager;

    /** Renders background tiles, entities, and the player each frame. */
    private final WorldRenderer renderer;

    /** The X offset of the camera in world coordinates, centered on the player. */
    private double cameraX;

    /** The Y offset of the camera in world coordinates, centered on the player. */
    private double cameraY;

    /**
     * Callback invoked when a battle is triggered, receiving the enemy dinosaur.
     * The game loop is stopped before this is called.
     */
    private final Consumer<Dinosaur> onEnterBattle;

    /** Callback invoked when the player exits the world by pressing ESC. */
    private final Runnable onExitWorld;

    /** The main game loop timer; stopped during battles and on exit. */
    private AnimationTimer gameLoop;

    /** The inventory UI panel, toggled by TAB or the inventory button. */
    private InventoryPane inventoryPane;

    /** The UI overlay layer containing the inventory button and inventory panel. */
    private StackPane uiLayer;

    /**
     * Constructs the {@code CretaceousExplorationScene}, initialises all subsystems,
     * wires up input and battle callbacks, and starts the game loop.
     * <p>
     * If this is the player's first entry into the forest, a short introductory
     * dialogue sequence is queued after a 0.5-second delay.
     * </p>
     *
     * @param onEnterBattle callback invoked with the enemy {@link Dinosaur} when a
     *                      battle is triggered; the game loop is stopped beforehand
     * @param onExitWorld   callback invoked when the player presses ESC to return
     *                      to the lobby
     */
    public CretaceousExplorationScene(
            Consumer<Dinosaur> onEnterBattle,
            Runnable onExitWorld
    ) {
        this.onEnterBattle = onEnterBattle;
        this.onExitWorld   = onExitWorld;

        StackPane root     = new StackPane();
        Canvas canvas      = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        uiLayer            = new StackPane();

        root.getChildren().addAll(canvas, uiLayer);

        Button inventoryButton = new Button("INVENTORY");
        inventoryButton.setStyle("""
                -fx-background-color: #7a7a7a;
                -fx-text-fill: white;
                -fx-font-family: 'Minecraft';
                -fx-font-weight: bold;
                -fx-padding: 10 25 10 25;
                -fx-border-color: #3c3c3c;
                -fx-border-width: 3;
                -fx-font-size: 18px;
                """);

        inventoryButton.setOnMouseClicked(e -> {
            inventoryPane.loadItems();
            inventoryPane.setVisible(true);
        });

        StackPane.setAlignment(inventoryButton, Pos.TOP_RIGHT);
        StackPane.setMargin(inventoryButton, new Insets(20));

        inventoryButton.setOnAction(e -> toggleInventory());

        inventoryPane = new InventoryPane();
        inventoryPane.setVisible(false);

        uiLayer.getChildren().addAll(inventoryButton, inventoryPane);
        StackPane.setAlignment(inventoryButton, Pos.TOP_RIGHT);
        StackPane.setMargin(inventoryButton, new Insets(20));
        StackPane.setAlignment(inventoryPane, Pos.CENTER);

        scene  = new Scene(root, WIDTH, HEIGHT);
        player = new Player(0, 0);

        worldManager = new WorldManager(player);
        renderer     = new WorldRenderer(gc, player, worldManager);

        worldManager.setOnBattleTriggered(enemy -> {
            if (gameLoop != null) gameLoop.stop();
            onEnterBattle.accept(enemy);
        });

        setupInput();
        startGameLoop(gc);

        if (!hasShownForestIntro) {
            hasShownForestIntro = true;

            javafx.animation.PauseTransition intro =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.5));

            intro.setOnFinished(e -> {
                DialogueManager.getInstance().queueDialogue(
                        "P'Tae",
                        "So this is the Cretaceous world... dinosaurs roam freely here.",
                        "/character/ptae.png"
                );
                DialogueManager.getInstance().queueDialogue(
                        "P'Tae",
                        "I need to weaken a dinosaur below 10% HP before throwing a DinoBall to catch it.",
                        "/character/ptae.png"
                );
                DialogueManager.getInstance().queueDialogue(
                        "P'Tae",
                        "Press E to pick up any items I find on the ground.",
                        "/character/ptae.png"
                );
                DialogueManager.getInstance().queueDialogue(
                        "P'Tae",
                        "And if it gets too dangerous... ESC will bring me back to the lobby. Stay sharp!",
                        "/character/ptae.png"
                );
            });

            intro.play();
        }
    }

    /**
     * Returns the JavaFX {@link Scene} for this exploration screen.
     *
     * @return the scene instance
     */
    public Scene getScene() { return scene; }

    /**
     * Returns the {@link WorldManager} managing this scene's world state.
     *
     * @return the world manager
     */
    public WorldManager getWorldManager() { return worldManager; }

    /**
     * Registers keyboard and mouse input handlers on the scene.
     * <p>
     * Key press adds the code to the active set and handles one-shot actions
     * (E for item pickup). Key release removes the code and handles TAB for
     * inventory toggle. Mouse press advances the dialogue on click.
     * </p>
     */
    private void setupInput() {
        scene.setOnKeyPressed(e -> {
            keys.add(e.getCode());

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

        scene.setOnMousePressed(e -> DialogueManager.getInstance().onClick());
    }

    /**
     * Creates and starts the {@link AnimationTimer} game loop.
     * <p>
     * Each frame calls {@link #update()}, renders the world via
     * {@link WorldRenderer#render(double, double)}, and redraws the
     * dialogue overlay.
     * </p>
     *
     * @param gc the graphics context passed to the dialogue manager each frame
     */
    private void startGameLoop(GraphicsContext gc) {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                renderer.render(cameraX, cameraY);
                DialogueManager.getInstance().render(gc, WIDTH, HEIGHT);
            }
        };

        gameLoop.start();
    }

    /**
     * Processes one game tick: handles ESC exit, updates player buffs, processes
     * movement input, advances the camera, and updates the world manager.
     * <p>
     * If a dialogue is currently active, movement input is suppressed and only
     * the dialogue state is advanced. If ESC is pressed, the game loop is stopped,
     * the world is shut down, and {@code onExitWorld} is invoked.
     * </p>
     */
    private void update() {
        if (keys.contains(KeyCode.ESCAPE)) {
            gameLoop.stop();
            worldManager.shutdown();
            onExitWorld.run();
            return;
        }

        player.updateBuffs();

        if (DialogueManager.getInstance().isActive()) {
            DialogueManager.getInstance().update();
            return;
        }

        double speed  = GameController.getInstance().getPlayer().getSpeed();
        boolean moving = false;

        if (keys.contains(KeyCode.A)) {
            player.setX(player.getX() - speed);
            player.setMoving(true);
            player.setFacingRight(false);
            moving = true;
        }
        if (keys.contains(KeyCode.D)) {
            player.setX(player.getX() + speed);
            player.setMoving(true);
            player.setFacingRight(true);
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

        if (!moving) player.setMoving(false);

        cameraX = player.getX() - WIDTH  / 2.0;
        cameraY = player.getY() - HEIGHT / 2.0;

        worldManager.update();
    }

    /**
     * Resumes the game loop after it has been stopped (e.g. returning from battle).
     * Has no effect if the game loop is already running.
     */
    public void resumeWorld() {
        if (gameLoop != null) gameLoop.start();
    }

    /**
     * Clears all currently tracked key inputs.
     * Should be called when re-entering the scene to prevent stuck keys
     * from a previous session carrying over.
     */
    public void clearInput() {
        keys.clear();
    }

    /**
     * Toggles the inventory panel open or closed.
     * Reloads the inventory contents each time it is opened.
     */
    private void toggleInventory() {
        boolean isOpen = inventoryPane.isVisible();
        if (!isOpen) inventoryPane.loadItems();
        inventoryPane.setVisible(!isOpen);
    }
}