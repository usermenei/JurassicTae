package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;

/**
 * Callback interface that decouples {@link BattleView} from the concrete
 * {@link GameController} singleton.
 *
 * <p>In production, {@code GameController} implements this interface.
 * In tests, a simple anonymous class or lambda-based stub implements it
 * without needing to subclass the singleton.</p>
 *
 * @see BattleView
 * @see GameController
 */
public interface BattleCallback {

    /**
     * Returns the current player.
     *
     * @return the active {@link Player}
     */
    Player getPlayer();

    /**
     * Called when the enemy dinosaur is defeated.
     *
     * @param enemy the defeated dinosaur
     */
    void onEnemyDefeated(Dinosaur enemy);

    /**
     * Called when the player successfully catches the dinosaur.
     *
     * @param enemy the caught dinosaur
     */
    void onEnemyCaught(Dinosaur enemy);

    /**
     * Called when the player escapes from battle.
     * Typically returns the player to the forest world.
     */
    void returnToWorld();

    /**
     * Called when the player is defeated.
     * Typically returns the player to the main lobby.
     */
    void returnToMain();
}