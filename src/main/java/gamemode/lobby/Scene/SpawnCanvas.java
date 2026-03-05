package gamemode.lobby.Scene;

import gamemode.DialogueManager;
import gamemode.lobby.Item.Base.DinoBall;
import gamemode.lobby.Player.Player;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import gamemode.lobby.logic.*;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.KeyboardController;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * The main game canvas for the lobby (spawn) area.
 *
 * <p>Extends {@link Canvas} and owns the primary {@link AnimationTimer} game loop.
 * Every frame it clears the canvas, updates game state, renders the player and
 * contextual UI prompts, then delegates to {@link DialogueManager} for any active
 * cutscene text.</p>
 *
 * <p>Music is <strong>not</strong> managed here — it is handled entirely by
 * {@link GameController#playMusic(String)} on every scene transition.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Grants the player 3 starter {@link DinoBall} items on construction.</li>
 *   <li>Queues the intro dialogue sequence via {@link DialogueManager} after a
 *       0.5-second delay so the scene has time to appear first.</li>
 *   <li>Reads {@link KeyboardController} each frame to move the player
 *       ({@code WASD} / arrow keys).</li>
 *   <li>Detects when the player walks near a landmark (Shop, Zoo, Gym, UFO) and
 *       displays a "Press F to enter" prompt via {@link #drawPressMessage(String)}.</li>
 *   <li>On {@code F} press: advances an open dialogue <em>or</em> triggers the
 *       appropriate scene/game-mode transition — never both in the same press.</li>
 *   <li>Updates the HUD (exp bar, level, money) every frame via
 *       {@link SpawnScreen#updateExpBar()}, {@link SpawnScreen#updateLevel()},
 *       and {@link SpawnScreen#updateMoney()}.</li>
 * </ul>
 *
 * <h2>Landmark positions (x, y, w, h)</h2>
 * <pre>
 *   SHOP  —  (80,  75,  250, 500)   top-left
 *   GYM   —  (80,  450, 250, 500)   bottom-left
 *   ZOO   —  (800, 75,  250, 500)   top-right
 *   UFO   —  (800, 450, 250, 500)   bottom-right
 * </pre>
 *
 * @see GameLogic
 * @see GameController
 * @see DialogueManager
 * @see KeyboardController
 */
public class SpawnCanvas extends Canvas {

    /** JavaFX drawing context used for all canvas rendering. */
    private GraphicsContext gc;

    /** The current player, retrieved once from {@link GameLogic} at construction. */
    private Player player = GameLogic.getInstance().getPlayer();

    /**
     * Guards against repeated F-key location-entry triggers.
     * {@code true} while F is held; reset to {@code false} when F is released.
     */
    private boolean fWasPressed = false;

    /**
     * Guards against repeated F-key dialogue-advance triggers.
     * {@code true} while F is held; reset to {@code false} when F is released.
     */
    private boolean fDialogueWasPressed = false;

    /** {@code true} when the player is inside the Shop landmark hitbox. */
    private boolean showEnterShop = false;

    /** {@code true} when the player is inside the Zoo (sell) landmark hitbox. */
    private boolean showEnterSell = false;

    /** {@code true} when the player is inside the Gym landmark hitbox. */
    private boolean showEnterGym = false;

    /** {@code true} when the player is inside the UFO landmark hitbox. */
    private boolean showEnterUfo = false;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs the {@code SpawnCanvas} (1422 × 800 px) and bootstraps the game.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Creates the canvas at 1422 × 800 pixels.</li>
     *   <li>Adds 3 starter {@link DinoBall} items to the player's inventory.</li>
     *   <li>Obtains the {@link GraphicsContext} for rendering.</li>
     *   <li>Starts the {@link AnimationTimer} game loop via {@link #startGameLoop()}.</li>
     *   <li>Schedules a 0.5-second pause, then queues 9 intro dialogue lines
     *       through {@link DialogueManager}.</li>
     *   <li>Registers a mouse-click handler that forwards clicks to
     *       {@link DialogueManager#onClick()} to advance dialogue.</li>
     * </ol>
     */
    public SpawnCanvas() {

        super(1422, 800);

        for (int i = 0; i < 3; i++) player.addItem(new DinoBall());

        gc = this.getGraphicsContext2D();

        startGameLoop();

        javafx.animation.PauseTransition intro =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.5));

        intro.setOnFinished(e -> {
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "Hm... As the Prime Minister of Thailand, I never expected to wake up surrounded by dinosaurs.",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "I have 3 DinoBalls in my bag. I should use them wisely to catch dinosaurs!",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "See that Shop in the top-left? I can buy more DinoBalls and potions there. Press F to enter.",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "The Zoo is in the top-right. I can sell caught dinosaurs there for money. Press F to enter.",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "There is also a Gym. If I pay 500$, I can train and boost my strength for battle!",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "The UFO will take me to the dinosaur world. Weaken a dinosaur below 10% HP to catch it.",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "Press E to pick up items I find on the ground while exploring.",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "And if things get too dangerous... press ESC to retreat back to this lobby!",
                    "/character/ptae.png");
            DialogueManager.getInstance().queueDialogue("P'Tae",
                    "Alright. As Prime Minister, I cannot afford to fail. Let's go catch some dinosaurs!",
                    "/character/ptae.png");
        });

        intro.play();

        setOnMouseClicked(e -> DialogueManager.getInstance().onClick());
    }

    // ── Game loop ────────────────────────────────────────────────────────────

    /**
     * Creates and starts the {@link AnimationTimer} that drives the game loop.
     *
     * <p>Each frame (called by JavaFX approximately 60 times per second):
     * <ol>
     *   <li>Clears the entire canvas.</li>
     *   <li>Calls {@link #update()} to process input and advance game state.</li>
     *   <li>Calls {@link #render()} to draw the player and prompt overlays.</li>
     *   <li>Resets text alignment so dialogue text is never mis-aligned.</li>
     *   <li>Calls {@link DialogueManager#update()} and
     *       {@link DialogueManager#render(GraphicsContext, double, double)}.</li>
     *   <li>Updates the HUD exp bar, level, and money displays.</li>
     * </ol>
     */
    private void startGameLoop() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, getWidth(), getHeight());

                update();
                render();

                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BASELINE);

                DialogueManager.getInstance().update();
                DialogueManager.getInstance().render(gc, getWidth(), getHeight());

                SpawnScreen screen = GameController.getInstance().getRoot();
                if (screen != null) {
                    screen.updateExpBar();
                    screen.updateLevel();
                    screen.updateMoney();
                }
            }
        };

        timer.start();
    }

    // ── Update ───────────────────────────────────────────────────────────────

    /**
     * Processes one frame of game-state updates.
     *
     * <p>In order:
     * <ol>
     *   <li>Returns immediately if the game has ended
     *       ({@link GameController#isGameEnded()}).</li>
     *   <li>Ticks active player buffs via {@link Player#updateBuffs()}.</li>
     *   <li>Returns early if {@link KeyboardController} is not yet registered
     *       (prevents NPE during the first few frames).</li>
     *   <li>Reads directional keys and calls {@link Player#move(int, int)}
     *       with a delta of −1, 0, or +1 on each axis.</li>
     *   <li>Refreshes the four landmark proximity flags.</li>
     *   <li>On the <em>first</em> frame F is pressed:
     *     <ul>
     *       <li>If dialogue is active → advances dialogue and returns.</li>
     *       <li>Otherwise → triggers the nearest landmark transition via
     *           {@link GameController} (which also switches the music).</li>
     *     </ul>
     *   </li>
     *   <li>Resets both F-key guard flags when F is released.</li>
     * </ol>
     */
    private void update() {

        if (GameController.getInstance().isGameEnded()) return;

        player.updateBuffs();

        KeyboardController keyboard = GameController.getInstance().getKeyboard();
        if (keyboard == null) return; // not yet registered — skip input this frame

        int dx = 0, dy = 0;
        if (keyboard.isLeftPressed())  dx = -1;
        if (keyboard.isRightPressed()) dx = 1;
        if (keyboard.isUpPressed())    dy = -1;
        if (keyboard.isDownPressed())  dy = 1;

        player.move(dx, dy);

        showEnterShop = player.intersects(80,  75,  250, 500);
        showEnterGym  = player.intersects(80,  450, 250, 500);
        showEnterSell = player.intersects(800, 75,  250, 500);
        showEnterUfo  = player.intersects(800, 450, 250, 500);

        if (keyboard.isFPressed() && !fDialogueWasPressed) {
            fDialogueWasPressed = true;
            if (DialogueManager.getInstance().isActive()) {
                DialogueManager.getInstance().onClick();
                return;
            }
        }

        if (keyboard.isFPressed() && !fWasPressed) {
            fWasPressed = true;
            if (!DialogueManager.getInstance().isActive()) {
                if      (showEnterGym)  GameController.getInstance().startGymMiniGame();
                else if (showEnterShop) GameController.getInstance().getRoot().showShopScene();
                else if (showEnterSell) GameController.getInstance().getRoot().showSellScene();
                else if (showEnterUfo)  GameController.getInstance().startCretaceousExploration();
            }
        }

        if (!keyboard.isFPressed()) {
            fWasPressed = false;
            fDialogueWasPressed = false;
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    /**
     * Renders one frame: draws the player sprite then, if the player is near a
     * landmark, draws the appropriate "Press F to enter" overlay.
     *
     * <p>Text alignment is reset after {@link #drawPressMessage(String)} so that
     * the subsequent dialogue render is never affected by centred alignment.</p>
     */
    private void render() {

        player.render(gc);

        if      (showEnterShop) drawPressMessage("SHOP");
        else if (showEnterSell) drawPressMessage("ZOO");
        else if (showEnterUfo)  drawPressMessage("UFO");
        else if (showEnterGym)  drawPressMessage("GYM");

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
    }

    // ── Prompt overlay ───────────────────────────────────────────────────────

    /**
     * Draws a semi-transparent rounded-rectangle overlay centred above the given
     * landmark, containing a "Press F to enter" message.
     *
     * <p>For the {@code "GYM"} location an additional fee line ("Fee: 500") is
     * appended, resulting in a two-line prompt box.</p>
     *
     * @param location one of {@code "SHOP"}, {@code "ZOO"}, {@code "GYM"},
     *                 or {@code "UFO"}; any other value defaults to SHOP position
     */
    private void drawPressMessage(String location) {

        String text = location.equals("GYM")
                ? "Press F to enter the GYM\nFee: 500"
                : "Press F to enter the " + location;

        String[] lines = text.split("\n");

        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/pixel.ttf"), 20);
        gc.setFont(font);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        double lineHeight = font.getSize() + 10;
        double boxWidth   = 300;
        double boxHeight  = (lineHeight * lines.length) + 25;

        int xPos, yPos;
        int width = 500, height = 250;

        switch (location) {
            case "SHOP" -> { xPos = 80;  yPos = 75;  }
            case "GYM"  -> { xPos = 80;  yPos = 450; }
            case "ZOO"  -> { xPos = 800; yPos = 75;  }
            case "UFO"  -> { xPos = 800; yPos = 450; }
            default     -> { xPos = 80;  yPos = 75;  }
        }

        double boxX    = xPos + (width  / 2.0) - (boxWidth  / 2.0);
        double boxY    = yPos + (height / 2.0) - (boxHeight / 2.0);
        double centerY = boxY + boxHeight / 2.0;

        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        gc.setFill(Color.WHITE);
        for (int i = 0; i < lines.length; i++) {
            double yOffset = (i - (lines.length - 1) / 2.0) * lineHeight;
            gc.fillText(lines[i], boxX + boxWidth / 2.0, centerY + yOffset);
        }
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the {@link Player} instance managed by this canvas.
     *
     * @return the current player
     */
    public Player getPlayer() { return player; }
}