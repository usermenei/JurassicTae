package gamemode.lobby.logic;

import gamemode.forest.CretaceousExplorationScene;
import gamemode.forest.entity.Dinosaur;
import gamemode.fightscene.BattleView;
import gamemode.gym.scene.GameScene;
import gamemode.gym.scene.MainMenu;
import gamemode.lobby.Player.Player;
import gamemode.lobby.Scene.SpawnScreen;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GameController {

    private static GameController instance = new GameController();

    private Stage stage;
    private Scene mainScene;
    private SpawnScreen root;
    private Player player;

    private KeyboardController keyboard;
    private boolean gameEnded;

    private int lastGymScore = 0;

    /* =========================
       ⭐ FOREST STATE
       ========================= */
    private Scene forestScene;
    private CretaceousExplorationScene explorationScene;

    private GameController() {}

    public static GameController getInstance() {
        return instance;
    }

    public void init(Stage stage, Scene scene) {
        this.stage = stage;
        this.mainScene = scene;
        keyboard = new KeyboardController(scene);
    }

    public void switchScene(Scene scene) {
        stage.setScene(scene);
    }

    /* =========================
       🌲 FOREST MODE
       ========================= */
    public void startCretaceousExploration() {

        explorationScene =
                new CretaceousExplorationScene(
                        this::startBattleMode,   // ✅ ส่ง method reference ตรง
                        this::returnToMain
                );

        forestScene = explorationScene.getScene();
        switchScene(forestScene);
    }

    /* =========================
       ⚔️ BATTLE MODE (ตัวจริง)
       ========================= */
    private void startBattleMode(Dinosaur enemy) {

        BattleView battleView = new BattleView(enemy, this);
        Scene battleScene = new Scene(battleView, 1422, 800);

        stage.setScene(battleScene);
    }
    /* =========================
       🦖 ENEMY DEFEATED (⭐ จุดสำคัญ)
       ========================= */
    public void onEnemyDefeated(Dinosaur enemy) {

        // ⭐ ลบไดโนออกจาก world จริง
        explorationScene
                .getWorldManager()
                .removeDinosaur(enemy);

        explorationScene
                .getWorldManager()
                .endBattle();

        explorationScene.clearInput();
        explorationScene.resumeWorld();

        stage.setScene(forestScene);

        forestScene.getRoot().requestFocus();
    }


    /* =========================
       🔙 RETURN FROM BATTLE
       ========================= */
    public void returnToWorld() {
        explorationScene.getWorldManager().endBattle();

        explorationScene.clearInput();
        explorationScene.resumeWorld();

        stage.setScene(forestScene);

        forestScene.getRoot().requestFocus();
    }

    /* =========================
       🏋️ GYM MODE (ของเดิม)
       ========================= */
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

    private void startActualGymGame() {

        Image red = new Image(
                getClass().getResource("/gamemode/gym/redtile.png").toExternalForm()
        );

        Image blue = new Image(
                getClass().getResource("/gamemode/gym/bluetile.png").toExternalForm()
        );

        Image bg = new Image(
                getClass().getResource("/gamemode/gym/gamebg.png").toExternalForm()
        );

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
                    startGymMiniGame();
                }
        );

        stage.setScene(game.getScene());
        game.start();
    }

    /* =========================
       🔁 MAIN / SPAWN (ของเดิม)
       ========================= */
    public void returnToMain() {
        stage.setScene(mainScene);

        if (root != null) {
            root.requestFocus();
        }
    }

    public void setRoot(SpawnScreen spawnScreen) {
        this.root = spawnScreen;
        player = root.getSpawnCanvas().getPlayer();
    }

    public SpawnScreen getRoot() {
        return root;
    }

    public KeyboardController getKeyboard() {
        return keyboard;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public Player getPlayer() {
        return player;
    }

    public void reloadSellScene() {
        root.getSellScene().refresh();
    }

    public void reloadMoney() {
        root.getMoneyLabel().setText(
                "Money : " +
                        GameLogic.getInstance().getPlayer().getMoney() +
                        " $"
        );
    }
    public void onEnemyCaught(Dinosaur enemy) {
        // DO NOT give exp
        // Remove dinosaur from world
        returnToWorld();
    }
}