package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import javafx.scene.control.ChoiceDialog;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

        commandBox.getBagButton().setOnAction(e -> {

            if (battleEnded) return;

            var potions = player.getInventory().stream()
                    .filter(item -> item instanceof gamemode.lobby.Item.Base.Potion)
                    .map(item -> (gamemode.lobby.Item.Base.Potion) item)
                    .toList();

            if (potions.isEmpty()) {
                commandBox.setMessage("No potions available!");
                return;
            }

            commandBox.showPotionMenu(potions, selectedPotion -> {

                player.usePotion(selectedPotion);

                commandBox.setMessage(selectedPotion.getName() + " used!");
            });
        });

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