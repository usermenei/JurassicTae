package gamemode.lobby.logic;

import gamemode.forest.CretaceousExplorationScene;
import gamemode.forest.entity.Dinosaur;
import gamemode.fightscene.BattleView;
import gamemode.gym.scene.GameScene;
import gamemode.gym.scene.MainMenu;
import gamemode.lobby.Player.Player;
import gamemode.lobby.Scene.SpawnScreen;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BorderWidths;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * <h2>GameController</h2>
 *
 * <p>Central controller responsible for managing all scene transitions,
 * high-level game state, and background music across different game modes.</p>
 *
 * <h3>Managed Game Modes</h3>
 * <ul>
 *   <li>Main Spawn Screen — {@code /sounds/lobby_bgm.mp3}</li>
 *   <li>Cretaceous Exploration (Forest) — {@code /sounds/forest_bgm.mp3}</li>
 *   <li>Battle Mode — {@code /sounds/battle_bgm.mp3}</li>
 *   <li>Gym Mini-Game — {@code /sounds/gym_bgm.mp3}</li>
 * </ul>
 *
 * <h3>Music system</h3>
 * <p>A single {@link MediaPlayer} ({@link #bgmPlayer}) is kept at all times.
 * Every scene transition calls {@link #playMusic(String)} with the path to the
 * new track. {@code playMusic} stops and disposes the previous track before
 * starting the new one, so only one track is ever playing.</p>
 *
 * <p>Sound files must be placed under {@code src/main/resources/sounds/}.</p>
 *
 * <p>Implemented as a <b>Singleton</b>.</p>
 *
 * @author Pongtawan
 * @version 2.0
 * @since 2026
 */
public class GameController {

    // ── Singleton ────────────────────────────────────────────────────────────

    /** Singleton instance. */
    private static GameController instance = new GameController();

    /** Private constructor (Singleton pattern). */
    private GameController() {}

    /**
     * Returns the singleton instance.
     *
     * @return the single {@code GameController}
     */
    public static GameController getInstance() { return instance; }

    // ── Sound paths ───────────────────────────────────────────────────────────

    /** Resource path for the lobby background music. */
    private static final String BGM_LOBBY  = "/sounds/lobby_bgm.mp3";

    /** Resource path for the forest/exploration background music. */
    private static final String BGM_FOREST = "/sounds/forest_bgm.mp3";

    /** Resource path for the battle background music. */
    private static final String BGM_BATTLE = "/sounds/battle_bgm.mp3";

    /** Resource path for the gym mini-game background music. */
    private static final String BGM_GYM    = "/sounds/gym_bgm.mp3";

    // ── Fields ────────────────────────────────────────────────────────────────

    /** Primary JavaFX stage. */
    private Stage stage;

    /** Main spawn scene. */
    private Scene mainScene;

    /** Root spawn screen reference. */
    private SpawnScreen root;

    /** Current player reference. */
    private Player player = GameLogic.getInstance().getPlayer();

    /** Keyboard input controller. */
    private KeyboardController keyboard;

    /** Indicates whether the game has ended. */
    private boolean gameEnded;

    /** Stores the last gym score for the result screen. */
    private int lastGymScore = 0;

    /** Forest exploration scene. */
    private Scene forestScene;

    /** Cretaceous exploration controller. */
    private CretaceousExplorationScene explorationScene;

    /**
     * The single active background music player.
     * Always stopped and replaced when {@link #playMusic(String)} is called.
     * {@code null} when no music is playing or the sound file was not found.
     */
    private MediaPlayer bgmPlayer;

    // ── Music ─────────────────────────────────────────────────────────────────

    /**
     * Stops the currently playing background music (if any) and starts a new
     * track from the given resource path.
     *
     * <p>The new track loops indefinitely at 50% volume. If the resource cannot
     * be found, a warning is printed to stderr and the game continues silently —
     * no exception is thrown.</p>
     *
     * <p>Passing {@code null} or an empty string simply stops the current music
     * without starting a new track.</p>
     *
     * @param resourcePath classpath resource path to an {@code .mp3} file,
     *                     e.g. {@code "/sounds/lobby_bgm.mp3"}
     */
    private void playMusic(String resourcePath) {
        // Stop and dispose previous track
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
            bgmPlayer = null;
        }

        if (resourcePath == null || resourcePath.isBlank()) return;

        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("[GameController] WARNING: BGM not found — " + resourcePath);
                return;
            }
            Media media = new Media(url.toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(0.5);
            bgmPlayer.play();
        } catch (Exception e) {
            System.err.println("[GameController] WARNING: Could not load BGM — " + e.getMessage());
        }
    }

    /**
     * Stops the current background music without starting a new one.
     * Safe to call even if no music is playing.
     */
    public void stopMusic() {
        playMusic(null);
    }

    /**
     * Sets the volume of the currently playing background music.
     * Has no effect if no music is playing.
     *
     * @param volume a value between {@code 0.0} (silent) and {@code 1.0} (full)
     */
    public void setMusicVolume(double volume) {
        if (bgmPlayer != null)
            bgmPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    /**
     * Initialises the controller with the primary stage and main scene,
     * then starts the lobby background music.
     *
     * @param stage primary JavaFX stage
     * @param scene main spawn scene
     */
    public void init(Stage stage, Scene scene) {
        this.stage     = stage;
        this.mainScene = scene;
        keyboard = new KeyboardController(scene);
    }

    /**
     * Switches the stage to a new scene.
     *
     * @param scene target scene
     */
    public void switchScene(Scene scene) {
        stage.setScene(scene);
    }

    // ── Forest mode ───────────────────────────────────────────────────────────

    /**
     * Starts the Cretaceous Exploration mode and switches to the forest music.
     *
     * <p>Stops the current music, loads the forest scene, then plays
     * {@value #BGM_FOREST}.</p>
     */
    public void startCretaceousExploration() {
        explorationScene = new CretaceousExplorationScene(
                this::startBattleMode,
                this::returnToMain
        );
        forestScene = explorationScene.getScene();
        switchScene(forestScene);
        playMusic(BGM_FOREST);
    }

    // ── Battle mode ───────────────────────────────────────────────────────────

    /**
     * Starts battle mode against a given enemy dinosaur and switches to battle music.
     *
     * @param enemy the enemy dinosaur to fight
     */
    private void startBattleMode(Dinosaur enemy) {
        BattleView battleView = new BattleView(enemy, this);
        Scene battleScene = new Scene(battleView, 1422, 800);
        stage.setScene(battleScene);
        playMusic(BGM_BATTLE);
    }

    // ── Enemy defeated ────────────────────────────────────────────────────────

    /**
     * Handles logic when an enemy dinosaur is defeated.
     * Returns to the forest scene and resumes forest music.
     *
     * @param enemy defeated dinosaur
     */
    public void onEnemyDefeated(Dinosaur enemy) {
        explorationScene.getWorldManager().removeDinosaur(enemy);
        explorationScene.getWorldManager().endBattle();
        explorationScene.clearInput();
        explorationScene.resumeWorld();
        stage.setScene(forestScene);
        forestScene.getRoot().requestFocus();
        playMusic(BGM_FOREST);
    }

    /**
     * Returns to the forest world without removing the enemy.
     * Used when the player escapes; resumes forest music.
     */
    public void returnToWorld() {
        explorationScene.getWorldManager().endBattle();
        explorationScene.clearInput();
        explorationScene.resumeWorld();
        stage.setScene(forestScene);
        forestScene.getRoot().requestFocus();
        playMusic(BGM_FOREST);
    }

    // ── Gym mode ──────────────────────────────────────────────────────────────

    /**
     * Starts the Gym mini-game menu and switches to gym music.
     */
    public void startGymMiniGame() {
        MainMenu menu = new MainMenu(
                1422, 800, lastGymScore,
                () -> startActualGymGame(),
                () -> returnToMain()
        );
        stage.setScene(menu.getScene());
        playMusic(BGM_GYM);
    }

    /**
     * Starts the actual Gym gameplay, calculates rewards, and displays a result popup.
     * Music continues as {@value #BGM_GYM} — no track change needed here.
     */
    private void startActualGymGame() {

        Image red  = new Image(getClass().getResource("/gamemode/gym/redtile.png").toExternalForm());
        Image blue = new Image(getClass().getResource("/gamemode/gym/bluetile.png").toExternalForm());
        Image bg   = new Image(getClass().getResource("/gamemode/gym/gamebg.png").toExternalForm());

        GameScene game = new GameScene(
                1422, 800, 200,
                (1422 - (200 * 2)) / 2,
                red, blue, bg,
                score -> {
                    lastGymScore = score;

                    int bonusStrength = score / 300;
                    int bonusExp      = score / 100;
                    int bonusMaxHp    = score / 500;

                    Player player = GameLogic.getInstance().getPlayer();
                    player.setStrength(player.getStrength() + bonusStrength);
                    player.addExp(bonusExp);
                    player.setMaxHp(player.getMaxHp() + bonusMaxHp);

                    StackPane popupRoot = new StackPane();
                    popupRoot.setStyle("-fx-background-color: rgba(0,0,0,0.6);");

                    VBox box = new VBox(20);
                    box.setAlignment(Pos.CENTER);
                    box.setPadding(new Insets(30));
                    box.setBackground(new Background(
                            new BackgroundFill(Color.rgb(20, 20, 20), new CornerRadii(20), null)
                    ));
                    box.setBorder(new Border(
                            new BorderStroke(Color.LIME, BorderStrokeStyle.SOLID,
                                    new CornerRadii(20), new BorderWidths(3))
                    ));

                    Font fontLarge  = Font.loadFont(getClass().getResourceAsStream("/fonts/pixel.ttf"), 40);
                    Font fontMedium = Font.loadFont(getClass().getResourceAsStream("/fonts/pixel.ttf"), 30);

                    Text title      = new Text("WORKOUT COMPLETE!");
                    title.setFont(fontLarge);
                    title.setFill(Color.WHITE);

                    Text scoreText  = new Text("Score: " + score);
                    scoreText.setFont(fontMedium);
                    scoreText.setFill(Color.CYAN);

                    Text rewardText = new Text("Strength + " + bonusStrength);
                    rewardText.setFont(fontMedium);
                    rewardText.setFill(Color.LIME);

                    Text expText    = new Text("EXP + " + bonusExp);
                    expText.setFont(fontMedium);
                    expText.setFill(Color.GOLD);

                    Text hpText     = new Text("Max HP + " + bonusMaxHp);
                    hpText.setFont(fontMedium);
                    hpText.setFill(Color.RED);

                    Button okBtn = new Button("OK");
                    okBtn.setFont(fontMedium);
                    okBtn.setPrefWidth(200);
                    okBtn.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    okBtn.setOnAction(e -> startGymMiniGame()); // goes back to gym menu (music stays)

                    box.getChildren().addAll(title, scoreText, rewardText, expText, hpText, okBtn);
                    popupRoot.getChildren().add(box);
                    stage.setScene(new Scene(popupRoot, 1422, 800));
                }
        );

        stage.setScene(game.getScene());
        game.start();
    }

    // ── Return to lobby ───────────────────────────────────────────────────────

    /**
     * Returns to the main spawn screen and restarts the lobby music.
     */
    public void returnToMain() {
        stage.setScene(mainScene);
        if (root != null) root.requestFocus();
        playMusic(BGM_LOBBY);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    /**
     * Sets the root {@link SpawnScreen} reference and updates the player reference.
     *
     * @param spawnScreen spawn screen instance
     */
    public void setRoot(SpawnScreen spawnScreen) {
        this.root = spawnScreen;
        player = root.getSpawnCanvas().getPlayer();
    }

    /** @return current {@link SpawnScreen} */
    public SpawnScreen getRoot() { return root; }

    /** @return keyboard controller */
    public KeyboardController getKeyboard() { return keyboard; }

    /** @return {@code true} if the game has ended */
    public boolean isGameEnded() { return gameEnded; }

    /** @return current player */
    public Player getPlayer() { return player; }

    /**
     * Refreshes the sell scene UI grid.
     */
    public void reloadSellScene() {
        root.getSellScene().refresh();
    }

    /**
     * Updates the money label in the HUD.
     */
    public void reloadMoney() {
        root.getMoneyLabel().setText(
                "Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $"
        );
    }

    /**
     * Handles logic when a dinosaur is caught. Returns to forest world.
     *
     * @param enemy caught dinosaur
     */
    public void onEnemyCaught(Dinosaur enemy) {
        returnToWorld();
    }
}