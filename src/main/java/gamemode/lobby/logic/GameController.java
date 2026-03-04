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
 * <h1>GameController</h1>
 *
 * <p>
 * Central controller responsible for managing all scene transitions
 * and high-level game state across different game modes.
 * </p>
 *
 * <p>
 * This controller manages:
 * </p>
 * <ul>
 *     <li>Main Spawn Screen</li>
 *     <li>Cretaceous Exploration (Forest Mode)</li>
 *     <li>Battle Mode</li>
 *     <li>Gym Mini-Game</li>
 * </ul>
 *
 * <p>
 * It also handles:
 * </p>
 * <ul>
 *     <li>Battle result processing</li>
 *     <li>Enemy defeat logic</li>
 *     <li>Mini-game reward calculation</li>
 *     <li>UI refresh (money, sell scene)</li>
 * </ul>
 *
 * <p>
 * Implemented using the Singleton pattern to ensure
 * only one GameController exists throughout the game lifecycle.
 * </p>
 *
 * @author Pongtawan
 * @version 1.0
 * @since 2026
 */
public class GameController {

    /** Singleton instance */
    private static GameController instance = new GameController();

    /** Primary JavaFX stage */
    private Stage stage;

    /** Main spawn scene */
    private Scene mainScene;

    /** Root spawn screen reference */
    private SpawnScreen root;

    /** Current player reference */
    private Player player = GameLogic.getInstance().getPlayer();

    /** Keyboard input controller */
    private KeyboardController keyboard;

    /** Indicates whether the game has ended */
    private boolean gameEnded;

    /** Stores the last gym score */
    private int lastGymScore = 0;

    /* =========================
       ⭐ FOREST STATE
       ========================= */

    /** Forest exploration scene */
    private Scene forestScene;

    /** Cretaceous exploration controller */
    private CretaceousExplorationScene explorationScene;

    /**
     * Private constructor (Singleton pattern).
     */
    private GameController() {}

    /**
     * Returns the singleton instance.
     *
     * @return GameController instance
     */
    public static GameController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller with stage and main scene.
     *
     * @param stage primary JavaFX stage
     * @param scene main spawn scene
     */
    public void init(Stage stage, Scene scene) {
        this.stage = stage;
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

    /* =========================
       🌲 FOREST MODE
       ========================= */

    /**
     * Starts the Cretaceous Exploration mode.
     * Initializes the exploration scene and switches to it.
     */
    public void startCretaceousExploration() {

        explorationScene = new CretaceousExplorationScene(
                this::startBattleMode,
                this::returnToMain
        );

        forestScene = explorationScene.getScene();
        switchScene(forestScene);
    }

    /* =========================
       ⚔️ BATTLE MODE
       ========================= */

    /**
     * Starts battle mode against a given enemy dinosaur.
     *
     * @param enemy the enemy dinosaur to fight
     */
    private void startBattleMode(Dinosaur enemy) {
        BattleView battleView = new BattleView(enemy, this);
        Scene battleScene = new Scene(battleView, 1422, 800);
        stage.setScene(battleScene);
    }

    /* =========================
       🦖 ENEMY DEFEATED
       ========================= */

    /**
     * Handles logic when an enemy dinosaur is defeated.
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
    }

    /**
     * Returns to forest world without removing enemy.
     * Used when player escapes or battle ends without defeat.
     */
    public void returnToWorld() {
        explorationScene.getWorldManager().endBattle();

        explorationScene.clearInput();
        explorationScene.resumeWorld();

        stage.setScene(forestScene);
        forestScene.getRoot().requestFocus();
    }

    /* =========================
       🏋️ GYM MODE
       ========================= */

    /**
     * Starts the Gym mini-game menu.
     */
    public void startGymMiniGame() {

        MainMenu menu = new MainMenu(
                1422,
                800,
                lastGymScore,
                () -> startActualGymGame(),
                () -> returnToMain()
        );

        stage.setScene(menu.getScene());
    }

    /**
     * Starts the actual Gym gameplay.
     * Calculates rewards and displays result popup.
     */
    private void startActualGymGame() {

        Image red  = new Image(getClass().getResource("/gamemode/gym/redtile.png").toExternalForm());
        Image blue = new Image(getClass().getResource("/gamemode/gym/bluetile.png").toExternalForm());
        Image bg   = new Image(getClass().getResource("/gamemode/gym/gamebg.png").toExternalForm());

        GameScene game = new GameScene(
                1422,
                800,
                200,
                (1422 - (200 * 2)) / 2,
                red,
                blue,
                bg,
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

                    Text title = new Text("WORKOUT COMPLETE!");
                    title.setFont(fontLarge);
                    title.setFill(Color.WHITE);

                    Text scoreText = new Text("Score: " + score);
                    scoreText.setFont(fontMedium);
                    scoreText.setFill(Color.CYAN);

                    Text rewardText = new Text("Strength + " + bonusStrength);
                    rewardText.setFont(fontMedium);
                    rewardText.setFill(Color.LIME);

                    Text expText = new Text("EXP + " + bonusExp);
                    expText.setFont(fontMedium);
                    expText.setFill(Color.GOLD);

                    Text hpText = new Text("Max HP + " + bonusMaxHp);
                    hpText.setFont(fontMedium);
                    hpText.setFill(Color.RED);

                    Button okBtn = new Button("OK");
                    okBtn.setFont(fontMedium);
                    okBtn.setPrefWidth(200);
                    okBtn.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                    okBtn.setOnAction(e -> startGymMiniGame());

                    box.getChildren().addAll(title, scoreText, rewardText, expText, hpText, okBtn);
                    popupRoot.getChildren().add(box);

                    stage.setScene(new Scene(popupRoot, 1422, 800));
                }
        );

        stage.setScene(game.getScene());
        game.start();
    }

    /* =========================
       🔁 MAIN / SPAWN
       ========================= */

    /**
     * Returns to main spawn screen.
     */
    public void returnToMain() {
        stage.setScene(mainScene);
        if (root != null) root.requestFocus();
    }

    /**
     * Sets root SpawnScreen reference.
     *
     * @param spawnScreen spawn screen instance
     */
    public void setRoot(SpawnScreen spawnScreen) {
        this.root = spawnScreen;
        player = root.getSpawnCanvas().getPlayer();
    }

    /**
     * @return current SpawnScreen
     */
    public SpawnScreen getRoot() {
        return root;
    }

    /**
     * @return keyboard controller
     */
    public KeyboardController getKeyboard() {
        return keyboard;
    }

    /**
     * @return true if game ended
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * @return current player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Refreshes sell scene UI.
     */
    public void reloadSellScene() {
        root.getSellScene().refresh();
    }

    /**
     * Updates money label UI.
     */
    public void reloadMoney() {
        root.getMoneyLabel().setText(
                "Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $"
        );
    }

    /**
     * Handles logic when dinosaur is caught.
     *
     * @param enemy caught dinosaur
     */
    public void onEnemyCaught(Dinosaur enemy) {
        returnToWorld();
    }
}