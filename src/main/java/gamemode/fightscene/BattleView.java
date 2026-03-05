package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Represents the graphical battle interface between the player and a dinosaur enemy.
 *
 * <p>This class renders the full battle scene including the background, enemy sprite
 * and HP, player sprite and HP, and the {@link CommandBox} for battle actions.</p>
 *
 * <p>Communication with the rest of the game is done via {@link BattleCallback},
 * which is implemented by {@link GameController} in production and by a simple
 * stub in tests. This removes the hard dependency on the {@code GameController}
 * singleton and makes the class fully testable.</p>
 *
 * <h2>Layout</h2>
 * <pre>
 * BorderPane (BattleView)
 * ├── CENTER — StackPane (battleArea)
 * │   ├── ImageView background
 * │   └── AnchorPane spriteLayer
 * │       ├── VBox enemyGroup   (top-right)
 * │       └── VBox playerGroup  (bottom-left)
 * └── BOTTOM — CommandBox
 * </pre>
 *
 * @see BattleCallback
 * @see CommandBox
 * @see Dinosaur
 * @see Player
 */
public class BattleView extends BorderPane {

    /**
     * Callback used to notify the game of battle outcomes.
     * Backed by {@link GameController} in production; by a stub in tests.
     */
    private final BattleCallback callback;

    /** The enemy dinosaur currently being fought. */
    private final Dinosaur enemy;

    /** UI component displaying enemy name and HP bar. */
    private InfoBox enemyInfo;

    /** The player participating in the battle. */
    private final Player player;

    /** UI component displaying player name and HP bar. */
    private InfoBox playerInfo;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Production constructor — accepts the real {@link GameController}.
     *
     * <p>{@code GameController} is adapted to {@link BattleCallback} inline so
     * no changes are needed in {@code GameController} itself.</p>
     *
     * @param enemy      the enemy dinosaur encountered in battle
     * @param controller the singleton game controller
     */
    public BattleView(Dinosaur enemy, GameController controller) {
        this(enemy, new BattleCallback() {
            @Override public Player   getPlayer()                    { return controller.getPlayer(); }
            @Override public void     onEnemyDefeated(Dinosaur e)   { controller.onEnemyDefeated(e); }
            @Override public void     onEnemyCaught(Dinosaur e)     { controller.onEnemyCaught(e); }
            @Override public void     returnToWorld()                { controller.returnToWorld(); }
            @Override public void     returnToMain()                 { controller.returnToMain(); }
        });
    }

    /**
     * Testable constructor — accepts any {@link BattleCallback} implementation.
     *
     * <p>Use this constructor in unit tests to pass a lightweight stub without
     * needing to construct or subclass the {@link GameController} singleton.</p>
     *
     * @param enemy    the enemy dinosaur encountered in battle
     * @param callback the callback that handles battle outcomes
     */
    public BattleView(Dinosaur enemy, BattleCallback callback) {

        this.enemy    = enemy;
        this.callback = callback;
        this.player   = callback.getPlayer();

        // ── Battle area ───────────────────────────────────────────────────

        StackPane battleArea = new StackPane();

        ImageView background = new ImageView(
                new Image(getClass().getResource("/forest/bg.jpg").toExternalForm())
        );
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(battleArea.widthProperty());
        background.fitHeightProperty().bind(battleArea.heightProperty());

        AnchorPane spriteLayer = new AnchorPane();
        spriteLayer.setPickOnBounds(false);

        // ── Enemy sprite (top-right) ──────────────────────────────────────

        ImageView enemyPic = new ImageView(
                new Image(getClass().getResource("/forest/" + enemy.getName() + ".PNG").toExternalForm())
        );
        enemyPic.setFitWidth(220);
        enemyPic.setPreserveRatio(true);

        enemyInfo = new InfoBox(enemy.getName(), enemy.getMaxHp());
        enemyInfo.setHp(enemy.getHp(), enemy.getMaxHp());

        VBox enemyGroup = new VBox(8, enemyInfo, enemyPic);
        enemyGroup.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(enemyGroup,   20.0);
        AnchorPane.setRightAnchor(enemyGroup, 300.0);

        // ── Player sprite (bottom-left) ───────────────────────────────────

        ImageView playerPic = new ImageView(
                new Image(getClass().getResource("/forest/player.png").toExternalForm())
        );
        playerPic.setFitWidth(220);
        playerPic.setPreserveRatio(true);

        playerInfo = new InfoBox("P'Tae", player.getMaxHp());
        playerInfo.setHp(player.getHp(), player.getMaxHp());

        VBox playerGroup = new VBox(8, playerPic, playerInfo);
        playerGroup.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(playerGroup, 20.0);
        AnchorPane.setLeftAnchor(playerGroup,   60.0);

        spriteLayer.getChildren().addAll(enemyGroup, playerGroup);
        battleArea.getChildren().addAll(background, spriteLayer);

        // ── Command box ───────────────────────────────────────────────────

        CommandBox commandBox = new CommandBox("P'Tae");
        commandBox.setMinHeight(220);
        commandBox.setPrefHeight(220);
        commandBox.setMaxHeight(220);
        commandBox.prefWidthProperty().bind(widthProperty());

        new BattleController(commandBox, this, enemy);

        // ── Layout ────────────────────────────────────────────────────────

        setCenter(battleArea);
        setBottom(commandBox);
        BorderPane.setAlignment(commandBox, Pos.CENTER);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Refreshes the enemy HP display using the enemy's current HP and max HP.
     */
    public void updateEnemyHp() {
        enemyInfo.setHp(enemy.getHp(), enemy.getMaxHp());
    }

    /**
     * Refreshes the player HP display.
     *
     * @param hp the player's current HP value
     */
    public void updatePlayerHp(int hp) {
        playerInfo.setHp(hp, player.getMaxHp());
    }

    /**
     * Called when the enemy dinosaur is defeated.
     * Delegates to {@link BattleCallback#onEnemyDefeated(Dinosaur)}.
     */
    public void onEnemyDefeated() {
        callback.onEnemyDefeated(enemy);
    }

    /**
     * Called when the player successfully catches the dinosaur.
     * Delegates to {@link BattleCallback#onEnemyCaught(Dinosaur)}.
     */
    public void onEnemyCaught() {
        callback.onEnemyCaught(enemy);
    }

    /**
     * Called when the player escapes from battle.
     * Delegates to {@link BattleCallback#returnToWorld()}.
     */
    public void onEscape() {
        callback.returnToWorld();
    }

    /**
     * Called when the player's HP reaches zero.
     * Delegates to {@link BattleCallback#returnToMain()}.
     */
    public void onPlayerDefeated() {
        callback.returnToMain();
    }
}