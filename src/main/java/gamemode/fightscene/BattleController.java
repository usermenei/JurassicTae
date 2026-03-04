package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.DinoBall;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class BattleController {

    private boolean playerTurn = true;

    private final BattleView view;
    private final Dinosaur enemy;
    private final CommandBox commandBox;
    private final Player player;

    private boolean battleEnded = false;

    public BattleController(CommandBox commandBox, BattleView view, Dinosaur enemy) {

        this.commandBox = commandBox;
        this.view = view;
        this.enemy = enemy;
        this.player = GameLogic.getInstance().getPlayer();

        /* ================= FIGHT ================= */

        commandBox.getFightButton().setOnAction(e -> {

            if (battleEnded || !playerTurn) return;

            var weapons = player.getInventory().stream()
                    .filter(item -> item instanceof gamemode.lobby.Item.Base.Weapon)
                    .map(item -> (gamemode.lobby.Item.Base.Weapon) item)
                    .toList();

            commandBox.showWeaponMenu(weapons, selectedWeapon -> {

                playerTurn = false;

                int damage = (selectedWeapon == null)
                        ? player.getStrength()
                        : selectedWeapon.getDamage();

                enemy.takeDamage(damage);
                view.updateEnemyHp();

                String weaponName = (selectedWeapon == null)
                        ? "Bare Hand"
                        : selectedWeapon.getName();

                commandBox.setMessage(
                        "P'Tae attacked with " + weaponName +
                                " (-" + damage + " HP)"
                );

                if (enemy.getHp() <= 0) {
                    battleEnded = true;

                    int expGained = enemy.getExpDrop(); // make sure Dinosaur has this
                    player.addExp(expGained);

                    commandBox.setMessage(enemy.getName() + " defeated! +" + expGained + " EXP");

                    PauseTransition reward = new PauseTransition(Duration.seconds(2));
                    reward.setOnFinished(ev -> view.onEnemyDefeated());
                    reward.play();
                    return;
                }

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> enemyTurn());
                pause.play();
            });
        });

        /* ================= BAG ================= */

        commandBox.getBagButton().setOnAction(e -> {

            if (battleEnded || !playerTurn) return;

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
                view.updatePlayerHp(player.getHp());

                commandBox.setMessage(selectedPotion.getName() + " used!");

//                playerTurn = false;
//
//                PauseTransition pause = new PauseTransition(Duration.seconds(1));
//                pause.setOnFinished(event -> enemyTurn());
//                pause.play();
            });
        });

        /* ================= CATCH ================= */

        commandBox.getCatchButton().setOnAction(e -> catchDinosaur());

        /* ================= ESCAPE ================= */

        commandBox.getEscapeButton().setOnAction(e -> {

            if (battleEnded) return;

            battleEnded = true;
            view.onEscape();
        });
    }

    /* =====================================================
                       CATCH LOGIC
       ===================================================== */

    private void catchDinosaur() {

        if (battleEnded || !playerTurn) return;

        playerTurn = false;

        DinoBall ball = player.getInventory().stream()
                .filter(item -> item instanceof DinoBall)
                .map(item -> (DinoBall) item)
                .findFirst()
                .orElse(null);

        if (ball == null) {
            commandBox.setMessage("No DinoBall available!");
            playerTurn = true;
            return;
        }

        double hpPercent = (double) enemy.getHp() / enemy.getMaxHp();

        if (hpPercent > 0.10) {
            commandBox.setMessage("HP must be below 10% to catch!");
            playerTurn = true;
            return;
        }

        player.removeItem(ball);

        TamedDinosaur tamed = new TamedDinosaur(enemy);
        player.addItem(tamed);

        switch (enemy.getRarity()) {
            case COMMON ->
                    commandBox.setMessage(enemy.getName() + " was caught!");
            case UNCOMMON ->
                    commandBox.setMessage("Nice! You captured an uncommon "
                            + enemy.getName() + "!");
            case RARE ->
                    commandBox.setMessage("INCREDIBLE! A rare "
                            + enemy.getName() + " has been captured!");
        }

        battleEnded = true;

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> view.onEnemyCaught());
        pause.play();
    }

    /* =====================================================
                       ENEMY TURN
       ===================================================== */

    private void enemyTurn() {

        if (battleEnded) return;

        int damage = enemy.getStrength();

        player.setHp(player.getHp() - damage);
        view.updatePlayerHp(player.getHp());

        commandBox.setMessage(
                enemy.getName() + " attacked P'Tae (-" + damage + " HP)"
        );

        if (player.getHp() <= 0) {
            battleEnded = true;
            player.setHp(player.getMaxHp()); // reset HP fully
            commandBox.setMessage("You were defeated... Returning to lobby.");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> view.onPlayerDefeated()); // new method
            pause.play();
            return;
        }

        playerTurn = true;
    }
}