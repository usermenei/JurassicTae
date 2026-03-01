package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;

public class BattleController {

    private final BattleView view;
    private final Dinosaur enemy;
    private boolean battleEnded = false;
    private Player player;

    public BattleController(CommandBox commandBox, BattleView view, Dinosaur enemy) {
        player = GameLogic.getInstance().getPlayer();
        this.view = view;
        this.enemy = enemy;

        commandBox.getFightButton().setOnAction(e -> {
            int damage = player.getStrength();

            enemy.takeDamage(damage);      // ⭐ ลด HP ตัวจริง
            view.updateEnemyHp();

            commandBox.setMessage(
                    "P'Tae attack Dinosaur (-" + damage + " HP)"
            );

            if (enemy.getHp() <= 0) {
                commandBox.setMessage("Dinosaur defeated!");
                view.onEnemyDefeated();
            }
        });

        commandBox.getBagButton().setOnAction(e ->
                commandBox.setMessage("Opening bag...")
        );

        commandBox.getCatchButton().setOnAction(e ->
                commandBox.setMessage("Attempting capture...")
        );

        commandBox.getEscapeButton().setOnAction(e -> {
            if (battleEnded) return;
            battleEnded = true;
            view.onEscape();
        });
    }
}