package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;

public class BattleController {

    private final BattleView view;
    private final Dinosaur enemy;
    private boolean battleEnded = false;

    public BattleController(CommandBox commandBox, BattleView view, Dinosaur enemy) {
        this.view = view;
        this.enemy = enemy;

        commandBox.getFightButton().setOnAction(e -> {
            int damage = 3;

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