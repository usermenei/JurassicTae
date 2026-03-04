package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BattleView extends BorderPane {

    private final GameController controller;
    private final Dinosaur enemy;
    private InfoBox enemyInfo;
    private final Player player;
    private InfoBox playerInfo;

    public BattleView(Dinosaur enemy, GameController controller) {

        this.enemy = enemy;
        this.controller = controller;
        this.player = controller.getPlayer();

        // ===== BATTLE AREA =====
        StackPane battleArea = new StackPane();

        ImageView background = new ImageView(
                new Image(getClass().getResource("/forest/bg.jpg").toExternalForm())
        );
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(battleArea.widthProperty());
        background.fitHeightProperty().bind(battleArea.heightProperty());

        // Use AnchorPane so we can pin sprites to corners reliably
        AnchorPane spriteLayer = new AnchorPane();
        spriteLayer.setPickOnBounds(false);

        // ===== ENEMY (top-right) =====
        ImageView enemyPic = new ImageView(
                new Image(getClass().getResource("/forest/dinosaur.png").toExternalForm())
        );
        enemyPic.setFitWidth(220);
        enemyPic.setPreserveRatio(true);

        enemyInfo = new InfoBox(enemy.getName(), enemy.getMaxHp());
        enemyInfo.setHp(enemy.getHp(), enemy.getMaxHp()); // ← add this
        VBox enemyGroup = new VBox(8, enemyInfo, enemyPic);
        enemyGroup.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(enemyGroup, 20.0);
        AnchorPane.setRightAnchor(enemyGroup, 60.0);

        // ===== PLAYER (bottom-left) =====
        ImageView playerPic = new ImageView(
                new Image(getClass().getResource("/forest/player.png").toExternalForm())
        );
        playerPic.setFitWidth(220);
        playerPic.setPreserveRatio(true);

        playerInfo = new InfoBox("P'Tae", controller.getPlayer().getMaxHp());
        playerInfo.setHp(player.getHp(), player.getMaxHp());
        VBox playerGroup = new VBox(8, playerPic, playerInfo);
        playerGroup.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(playerGroup, 20.0);
        AnchorPane.setLeftAnchor(playerGroup, 60.0);

        spriteLayer.getChildren().addAll(enemyGroup, playerGroup);
        battleArea.getChildren().addAll(background, spriteLayer);

        // ===== COMMAND BOX =====
        CommandBox commandBox = new CommandBox("P'Tae");
        commandBox.setMinHeight(220);
        commandBox.setPrefHeight(220);
        commandBox.setMaxHeight(220);
        commandBox.prefWidthProperty().bind(widthProperty());

        new BattleController(commandBox, this, enemy);

        // ===== LAYOUT =====
        setCenter(battleArea);
        setBottom(commandBox);
        BorderPane.setAlignment(commandBox, Pos.CENTER);
    }

    public void updateEnemyHp() {
        enemyInfo.setHp(enemy.getHp(), enemy.getMaxHp());
    }

    public void updatePlayerHp(int hp) {
        playerInfo.setHp(hp, player.getMaxHp());
    }

    public void onEnemyDefeated() {
        controller.onEnemyDefeated(enemy);
    }

    public void onEnemyCaught() {
        controller.onEnemyCaught(enemy);
    }

    public void onEscape() {
        controller.returnToWorld();
    }
    public void onPlayerDefeated() {
        controller.returnToMain();// or controller.returnToLobby() — depends on your GameController
    }
}