package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.Base.DinoBall;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameLogic;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Controls the turn-based battle logic between the player (P'Tae) and an enemy {@link Dinosaur}.
 *
 * <p>This class wires up all four combat actions available in {@link CommandBox}
 * (Fight, Bag, Catch, Escape) and manages the turn order, win/loss conditions,
 * and post-battle transitions.</p>
 *
 * <h2>Turn flow</h2>
 * <pre>
 *   Player presses action
 *       │
 *       ├─ FIGHT  → deal damage → enemy dead? → victory
 *       │                       → still alive → enemy turn → player dead? → defeat
 *       │                                                   → still alive → player turn
 *       ├─ BAG    → use potion → stay on player turn (enemy does not counter)
 *       ├─ CATCH  → HP ≤ 10%? → consume DinoBall → add TamedDinosaur → caught
 *       │         → HP > 10%? → message, stay on player turn
 *       └─ ESCAPE → immediately end battle, return to world
 * </pre>
 *
 * <h2>Guards</h2>
 * <ul>
 *   <li>All actions are silently ignored when {@code battleEnded} is {@code true}.</li>
 *   <li>Fight and Catch are silently ignored when it is not the player's turn
 *       ({@code playerTurn == false}).</li>
 * </ul>
 *
 * @see BattleView
 * @see CommandBox
 * @see Dinosaur
 * @see Player
 */
public class BattleController {

    /**
     * {@code true} when it is the player's turn to act;
     * {@code false} while the enemy turn is in progress.
     */
    private boolean playerTurn = true;

    /** The battle UI that this controller drives. */
    private final BattleView view;

    /** The enemy dinosaur being fought. */
    private final Dinosaur enemy;

    /** The command panel containing the four action buttons. */
    private final CommandBox commandBox;

    /** The current player, retrieved from {@link GameLogic} at construction. */
    private final Player player;

    /**
     * Set to {@code true} the moment a battle-ending event occurs
     * (victory, defeat, catch, or escape). All subsequent button presses
     * are ignored once this flag is raised.
     */
    private boolean battleEnded = false;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs a {@code BattleController} and registers all four action handlers
     * on the provided {@link CommandBox}.
     *
     * <p>Registered handlers</p>
     * <ul>
     *   <li><b>Fight</b> — opens a weapon selection sub-menu, deals damage, and
     *       triggers the enemy turn (or victory) after a 1-second pause.</li>
     *   <li><b>Bag</b> — opens a potion selection sub-menu and applies the chosen
     *       potion immediately. The enemy does <em>not</em> counter after a potion.</li>
     *   <li><b>Catch</b> — delegates to {@link #catchDinosaur()}.</li>
     *   <li><b>Escape</b> — ends the battle immediately and calls
     *       {@link BattleView#onEscape()}.</li>
     * </ul>
     *
     * @param commandBox the UI panel whose buttons will be wired to combat actions
     * @param view       the battle scene view for HP updates and scene transitions
     * @param enemy      the enemy dinosaur the player is fighting
     */
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

                    int expGained = enemy.getExpDrop();
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

    // ── Catch ────────────────────────────────────────────────────────────────

    /**
     * Attempts to catch the enemy dinosaur using a {@link DinoBall}.
     *
     * <p>Catch conditions (checked in order):
     * <ol>
     *   <li>Battle must not have ended and it must be the player's turn.</li>
     *   <li>The player must have at least one {@link DinoBall} in their inventory;
     *       otherwise a message is shown and the turn is returned.</li>
     *   <li>The enemy's HP must be at or below 10% of its maximum HP;
     *       otherwise a message is shown and the turn is returned.</li>
     * </ol>
     *
     * <p>On a successful catch:
     * <ul>
     *   <li>One {@link DinoBall} is consumed from the player's inventory.</li>
     *   <li>A {@link TamedDinosaur} wrapping the enemy is added to the inventory.</li>
     *   <li>A rarity-dependent success message is displayed.</li>
     *   <li>After a 1.5-second pause, {@link BattleView#onEnemyCaught()} is called.</li>
     * </ul>
     */
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

    // ── Enemy turn ───────────────────────────────────────────────────────────

    /**
     * Executes the enemy's turn: deals damage equal to {@link Dinosaur#getStrength()}
     * to the player, updates the HP display, and checks for player defeat.
     *
     * <p>Outcomes:
     * <ul>
     *   <li><b>Player HP &gt; 0</b> — sets {@code playerTurn = true} so the player
     *       may act again.</li>
     *   <li><b>Player HP ≤ 0</b> — resets player HP to full, displays a defeat
     *       message, and calls {@link BattleView#onPlayerDefeated()} after a
     *       2-second pause.</li>
     * </ul>
     *
     * <p>Returns immediately (no-op) if {@code battleEnded} is already {@code true}.</p>
     */
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
            player.setHp(player.getMaxHp()); // reset HP fully on defeat
            commandBox.setMessage("You were defeated... Returning to lobby.");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> view.onPlayerDefeated());
            pause.play();
            return;
        }

        playerTurn = true;
    }
}