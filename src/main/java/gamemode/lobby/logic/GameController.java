package gamemode.lobby.logic;

import gamemode.gym.scene.GameScene;
import gamemode.gym.scene.MainMenu;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import gamemode.forest.CretaceousExplorationScene;
import gamemode.lobby.LivingThing.Player;
import gamemode.lobby.Scene.SpawnScreen;

public class GameController {

    private static GameController instance = new GameController();

    private Stage stage;
    private Scene mainScene;
    private SpawnScreen root;
    private Player player;

    private KeyboardController keyboard;
    private boolean gameEnded;

    private GameController(){}

    public static GameController getInstance() {
        return instance;
    }
    private int lastGymScore = 0;


    public void init(Stage stage, Scene scene){
        this.stage = stage;
        this.mainScene = scene;
        keyboard = new KeyboardController(scene);
    }

    public void switchScene(Scene scene){
        stage.setScene(scene);
    }

    public void startCretaceousExploration() {

        CretaceousExplorationScene exploration =
                new CretaceousExplorationScene(
                        () -> startBattleMode(),
                        () -> returnToMain()
                );

        GameController.getInstance().switchScene(
                exploration.getScene()
        );
    }

    private void startBattleMode() {
        System.out.println("BATTLE");
    }


    public void startGymMiniGame() {

        Stage stage = this.stage;

        MainMenu menu = new MainMenu(
                1422,
                800,
                lastGymScore,   // previous score (you can store this later)
                () -> {
                    startActualGymGame();
                },
                () -> {
                    returnToMain();   // back to spawn
                }
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
                    lastGymScore = score;     // ✅ SAVE SCORE
                    startGymMiniGame();       // go back to menu
                }
        );

        stage.setScene(game.getScene());
        game.start();
    }

    public void returnToMain(){
        stage.setScene(mainScene);

        if (root != null) {
            root.requestFocus();   // ✅ correct
        }
    }

    public void setRoot(SpawnScreen spawnScreen){
        this.root = spawnScreen;
        player = root.getSpawnCanvas().getPlayer();
    }

    public SpawnScreen getRoot(){
        return root;
    }

    public KeyboardController getKeyboard(){
        return keyboard;
    }

    public boolean isGameEnded(){
        return gameEnded;
    }

    public Player getPlayer(){
        return player;
    }

    public void reloadSellScene(){
        root.getSellScene().refresh();
    }

    public void reloadMoney(){
        root.getMoneyLabel().setText("Money : " + GameLogic.getInstance().getPlayer().getMoney() + " $");
    }
}