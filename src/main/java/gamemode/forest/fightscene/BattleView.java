package gamemode.forest.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.logic.GameController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BattleView extends BorderPane {

    private final GameController controller;
    private final Dinosaur enemy;
    private InfoBox enemyInfo;

    public BattleView(Dinosaur enemy, GameController controller) {
        this.enemy = enemy;
        this.controller = controller;

        StackPane battleArea = new StackPane();

        ImageView background = new ImageView(
                new Image(getClass().getResource("/forest/bg.jpg").toExternalForm())
        );
        background.fitWidthProperty().bind(battleArea.widthProperty());
        background.fitHeightProperty().bind(battleArea.heightProperty());
        background.setMouseTransparent(true);

        Pane characterLayer = new Pane();
        characterLayer.setMouseTransparent(true);

        ImageView enemyPic = new ImageView(
                new Image(getClass().getResource("/forest/dinosaur.png").toExternalForm())
        );
        enemyPic.setFitWidth(220);
        enemyPic.setPreserveRatio(true);
        enemyPic.setLayoutX(800);
        enemyPic.setLayoutY(150);

        enemyInfo = new InfoBox(enemy.getName(), enemy.getHp());
        enemyInfo.setLayoutX(820);
        enemyInfo.setLayoutY(60);

        ImageView playerPic = new ImageView(
                new Image(getClass().getResource("/forest/player.png").toExternalForm())
        );
        playerPic.setFitWidth(260);
        playerPic.setPreserveRatio(true);
        playerPic.setLayoutX(350);
        playerPic.setLayoutY(70);

        InfoBox playerInfo = new InfoBox("P'Tae", 20);
        playerInfo.setLayoutX(100);
        playerInfo.setLayoutY(420);

        characterLayer.getChildren().addAll(
                enemyPic, enemyInfo,
                playerPic, playerInfo
        );

        battleArea.getChildren().addAll(background, characterLayer);

        CommandBox commandBox = new CommandBox("What will P'Tae do?");
        new BattleController(commandBox, this, enemy);

        setCenter(battleArea);
        setBottom(commandBox);
    }

    /* =========================
       UI ONLY
       ========================= */
    public void updateEnemyHp() {
        enemyInfo.setHp(enemy.getHp());
    }

    public void onEnemyDefeated() {
        controller.onEnemyDefeated(enemy);
    }
}