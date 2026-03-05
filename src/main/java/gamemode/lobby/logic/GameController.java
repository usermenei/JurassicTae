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

public class GameController {

    private static GameController instance = new GameController();

    private Stage stage;
    private Scene mainScene;
    private SpawnScreen root;
    private Player player = GameLogic.getInstance().getPlayer();

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

    public boolean enoughMoney(Player player){
        if(player.getMoney() < 500){
            return false;
        }
        player.setMoney(player.getMoney() - 500);
        if(GameController.getInstance().getRoot() != null){
            GameController.getInstance().getRoot().updateMoney();
        }
        return true;
    }

    private void startActualGymGame() {
        if(!enoughMoney(player))return;
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

                    int bonusStrength = score / 300;
                    int bonusExp = score / 100;
                    int bonusMaxHp = score / 500;

                    setReward(score);

                    // ===== CUSTOM POPUP =====
                    StackPane popupRoot = new StackPane();
                    popupRoot.setStyle("-fx-background-color: rgba(0,0,0,0.6);");

                    VBox box = new VBox(20);
                    box.setAlignment(Pos.CENTER);
                    box.setPadding(new Insets(30));

                    box.setBackground(new Background(
                            new BackgroundFill(
                                    Color.rgb(20, 20, 20),
                                    new CornerRadii(20),
                                    null
                            )
                    ));

                    box.setBorder(new Border(
                            new BorderStroke(
                                    Color.LIME,
                                    BorderStrokeStyle.SOLID,
                                    new CornerRadii(20),
                                    new BorderWidths(3)
                            )
                    ));

                    Text title = new Text("WORKOUT COMPLETE!");
                    title.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 40));
                    title.setFill(Color.WHITE);

                    Text scoreText = new Text("Score: " + score);
                    scoreText.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 30));
                    scoreText.setFill(Color.CYAN);

                    Text rewardText = new Text("Strength + " + bonusStrength);
                    rewardText.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 35));
                    rewardText.setFill(Color.LIME);

                    Text expText = new Text("EXP + " + bonusExp);
                    expText.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 30));
                    expText.setFill(Color.GOLD);

                    Text hpText = new Text("Max HP + " + bonusMaxHp);
                    hpText.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 30));
                    hpText.setFill(Color.RED);

                    Button okBtn = new Button("OK");
                    okBtn.setFont(Font.loadFont(
                            getClass().getResourceAsStream("/fonts/pixel.ttf"), 28));
                    okBtn.setPrefWidth(200);
                    okBtn.setStyle("-fx-background-color: white; -fx-text-fill: black;");

                    okBtn.setOnAction(e -> {
                        startGymMiniGame();
                    });

                    box.getChildren().addAll(title, scoreText, rewardText, expText, hpText, okBtn);
                    popupRoot.getChildren().add(box);

                    Scene popupScene = new Scene(popupRoot, 1422, 800);
                    stage.setScene(popupScene);
                }
        );

        stage.setScene(game.getScene());
        game.start();
    }

    public void setReward(int score){
        int bonusStrength = score / 300;
        int bonusExp = score / 100;
        int bonusMaxHp = score / 500;

        Player player = GameLogic.getInstance().getPlayer();

        player.setStrength(player.getStrength() + bonusStrength);
        player.addExp(bonusExp);
        player.setMaxHp(player.getMaxHp() + bonusMaxHp);
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