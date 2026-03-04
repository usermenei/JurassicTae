package gamemode.lobby.logic;

/**
 * <h1>GameState</h1>
 *
 * <p>
 * Represents the different states of the lobby game mode.
 * This enum is used to control game flow and determine
 * what logic should be executed in the update cycle.
 * </p>
 *
 * <h2>States:</h2>
 * <ul>
 *     <li><b>PLAYING</b> – Player is actively moving and interacting.</li>
 *     <li><b>SHOP</b> – Player is browsing or purchasing items.</li>
 *     <li><b>SELL</b> – Player is selling items.</li>
 *     <li><b>PUSHUP</b> – Player is performing push-up training activity.</li>
 *     <li><b>SPAWN</b> – Initial spawn state when entering the lobby.</li>
 * </ul>
 *
 * <p>
 * This enum is commonly used inside {@link GameLogic}
 * to control state-dependent behavior.
 * </p>
 *
 * @author Pongtawan
 * @version 1.0
 */
public enum GameState {

    /** Player is actively playing in the lobby. */
    PLAYING,

    /** Player is inside the shop interface. */
    SHOP,

    /** Player is selling items. */
    SELL,

    /** Player is performing push-up training. */
    PUSHUP,

    /** Initial spawn state when entering the lobby. */
    SPAWN
}