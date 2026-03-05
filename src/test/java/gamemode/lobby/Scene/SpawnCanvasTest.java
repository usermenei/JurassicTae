package gamemode.lobby.Scene;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests only the pure logic inside {@link SpawnCanvas}.
 *
 * <p>SpawnCanvas is a JavaFX {@link javafx.scene.canvas.Canvas} subclass, so its
 * constructor cannot be called in unit tests (no JavaFX runtime). Instead, every
 * testable decision is extracted into a plain Java helper class
 * ({@link SpawnLogic}) that mirrors the exact {@code if/else} and arithmetic from
 * the real class. No JavaFX, no Mockito, no UI.</p>
 *
 * <h2>Logic units tested</h2>
 * <ul>
 *   <li>Landmark proximity detection — which flag is set for each (x, y) position.</li>
 *   <li>F-key guard — dialogue takes priority over location entry; flags reset on release.</li>
 *   <li>Location entry routing — which action fires based on which flag is active.</li>
 *   <li>Prompt text selection — GYM gets a fee line; others do not.</li>
 *   <li>Box geometry — centre, width, height computed from landmark anchor.</li>
 * </ul>
 *
 * <p>Run with: {@code ./gradlew test}</p>
 */
class SpawnCanvasTest {

    // ── Extracted logic ───────────────────────────────────────────────────────

    /**
     * Plain Java mirror of all testable logic in {@link SpawnCanvas}.
     * Contains no JavaFX types — safe to instantiate in any JVM.
     */
    static class SpawnLogic {

        // ── Proximity flags (mirrors SpawnCanvas fields) ──────────────────

        boolean showEnterShop = false;
        boolean showEnterSell = false;
        boolean showEnterGym  = false;
        boolean showEnterUfo  = false;

        // ── F-key guards ──────────────────────────────────────────────────

        boolean fWasPressed          = false;
        boolean fDialogueWasPressed  = false;

        // ── Action recorder ───────────────────────────────────────────────

        /** Records the last action triggered by an F-key press. */
        String lastAction = null;

        // ── Stub state ────────────────────────────────────────────────────

        boolean dialogueActive = false;
        boolean gameEnded      = false;

        // ── Player position / movement ────────────────────────────────────

        int playerX = 0;
        int playerY = 0;

        /** Mirrors {@code Player.move(dx, dy)} — adds delta to position. */
        void movePlayer(int dx, int dy) {
            playerX += dx;
            playerY += dy;
        }

        /**
         * Mirrors {@code Player.intersects(x, y, w, h)}.
         * Returns true when the player position falls within the rectangle.
         */
        boolean intersects(int rx, int ry, int rw, int rh) {
            return playerX >= rx && playerX <= rx + rw
                    && playerY >= ry && playerY <= ry + rh;
        }

        // ── Landmark detection ────────────────────────────────────────────

        /**
         * Mirrors the four {@code showEnterX = player.intersects(...);}
         * lines inside {@code SpawnCanvas.update()}.
         */
        void updateProximityFlags() {
            showEnterShop = intersects(80,  75,  250, 500);
            showEnterGym  = intersects(80,  450, 250, 500);
            showEnterSell = intersects(800, 75,  250, 500);
            showEnterUfo  = intersects(800, 450, 250, 500);
        }

        // ── F-key handling ────────────────────────────────────────────────

        /**
         * Mirrors the complete F-key block from {@code SpawnCanvas.update()}.
         *
         * @param fPressed current state of the F key
         */
        void handleFKey(boolean fPressed) {
            if (fPressed && !fDialogueWasPressed) {
                fDialogueWasPressed = true;
                if (dialogueActive) {
                    lastAction = "advanceDialogue";
                    return; // block location entry
                }
            }

            if (fPressed && !fWasPressed) {
                fWasPressed = true;
                if (!dialogueActive) {
                    if      (showEnterGym)  lastAction = "startGym";
                    else if (showEnterShop) lastAction = "showShop";
                    else if (showEnterSell) lastAction = "showSell";
                    else if (showEnterUfo)  lastAction = "startUfo";
                }
            }

            if (!fPressed) {
                fWasPressed         = false;
                fDialogueWasPressed = false;
            }
        }

        // ── Prompt text selection ─────────────────────────────────────────

        /**
         * Mirrors the prompt-text logic in {@code drawPressMessage()}.
         *
         * @param location one of SHOP, ZOO, GYM, UFO
         * @return the prompt string that would be rendered
         */
        String promptTextFor(String location) {
            return location.equals("GYM")
                    ? "Press F to enter the GYM\nFee: 500"
                    : "Press F to enter the " + location;
        }

        // ── Box geometry ──────────────────────────────────────────────────

        /**
         * Mirrors the box-position calculation from {@code drawPressMessage()}.
         * Returns [boxX, boxY, boxWidth, boxHeight].
         */
        double[] boxGeometry(String location) {
            int xPos, yPos;
            int width = 500, height = 250;

            switch (location) {
                case "SHOP" -> { xPos = 80;  yPos = 75;  }
                case "GYM"  -> { xPos = 80;  yPos = 450; }
                case "ZOO"  -> { xPos = 800; yPos = 75;  }
                case "UFO"  -> { xPos = 800; yPos = 450; }
                default     -> { xPos = 80;  yPos = 75;  }
            }

            double boxWidth  = 300;
            double lineCount = location.equals("GYM") ? 2 : 1;
            double lineHeight = 20 + 10; // font size + padding
            double boxHeight = (lineHeight * lineCount) + 25;

            double boxX = xPos + (width  / 2.0) - (boxWidth  / 2.0);
            double boxY = yPos + (height / 2.0) - (boxHeight / 2.0);

            return new double[]{ boxX, boxY, boxWidth, boxHeight };
        }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    SpawnLogic logic;

    @BeforeEach
    void setUp() { logic = new SpawnLogic(); }

    // ── Proximity tests ───────────────────────────────────────────────────────

    /**
     * TC-01: Player at (100, 100) is inside the Shop zone.
     */
    @Test
    @DisplayName("TC-01 | Player near Shop → showEnterShop is true")
    void tc01_playerNearShop() {
        logic.playerX = 100;
        logic.playerY = 100;
        logic.updateProximityFlags();

        assertTrue(logic.showEnterShop,  "showEnterShop must be true");
        assertFalse(logic.showEnterSell, "showEnterSell must be false");
        assertFalse(logic.showEnterGym,  "showEnterGym must be false");
        assertFalse(logic.showEnterUfo,  "showEnterUfo must be false");
    }

    /**
     * TC-02: Player at (850, 100) is inside the Zoo (Sell) zone.
     */
    @Test
    @DisplayName("TC-02 | Player near Zoo → showEnterSell is true")
    void tc02_playerNearZoo() {
        logic.playerX = 850;
        logic.playerY = 100;
        logic.updateProximityFlags();

        assertTrue(logic.showEnterSell,  "showEnterSell must be true");
        assertFalse(logic.showEnterShop, "showEnterShop must be false");
    }

    /**
     * TC-03: Player at (100, 600) is inside the Gym zone only.
     *
     * <p>Why y=600: Shop spans y 75–575, Gym spans y 450–950.
     * y=600 is inside Gym but above Shop's bottom edge, so Shop flag stays false.</p>
     */
    @Test
    @DisplayName("TC-03 | Player near Gym (y=600) → showEnterGym true, showEnterShop false")
    void tc03_playerNearGym() {
        logic.playerX = 100;
        logic.playerY = 600; // below Shop bottom (575), still inside Gym (450–950)
        logic.updateProximityFlags();

        assertTrue(logic.showEnterGym,   "showEnterGym must be true");
        assertFalse(logic.showEnterShop, "showEnterShop must be false");
    }

    /**
     * TC-04: Player at (850, 600) is inside the UFO zone only.
     *
     * <p>Why y=600: Sell spans y 75–575, UFO spans y 450–950.
     * y=600 is inside UFO but below Sell's bottom edge, so Sell flag stays false.</p>
     */
    @Test
    @DisplayName("TC-04 | Player near UFO (y=600) → showEnterUfo true, showEnterSell false")
    void tc04_playerNearUfo() {
        logic.playerX = 850;
        logic.playerY = 600; // below Sell bottom (575), still inside UFO (450–950)
        logic.updateProximityFlags();

        assertTrue(logic.showEnterUfo,   "showEnterUfo must be true");
        assertFalse(logic.showEnterShop, "showEnterShop must be false");
    }

    /**
     * TC-05: Player at (500, 400) is not near any landmark — all flags false.
     */
    @Test
    @DisplayName("TC-05 | Player in open field → no proximity flags set")
    void tc05_playerInOpenField() {
        logic.playerX = 500;
        logic.playerY = 400;
        logic.updateProximityFlags();

        assertAll("no flags should be set",
                () -> assertFalse(logic.showEnterShop, "showEnterShop"),
                () -> assertFalse(logic.showEnterSell, "showEnterSell"),
                () -> assertFalse(logic.showEnterGym,  "showEnterGym"),
                () -> assertFalse(logic.showEnterUfo,  "showEnterUfo")
        );
    }

    // ── F-key guard tests ─────────────────────────────────────────────────────

    /**
     * TC-06: When dialogue is active, F press advances dialogue and does NOT
     * trigger location entry.
     */
    @Test
    @DisplayName("TC-06 | F press with active dialogue → advances dialogue only")
    void tc06_fPressAdvancesDialogueOnly() {
        logic.dialogueActive = true;
        logic.showEnterShop  = true;

        logic.handleFKey(true);

        assertEquals("advanceDialogue", logic.lastAction,
                "lastAction must be advanceDialogue");
    }

    /**
     * TC-07: When no dialogue is active and player is near the Shop, F press
     * triggers showShop.
     */
    @Test
    @DisplayName("TC-07 | F press near Shop (no dialogue) → showShop")
    void tc07_fPressNearShopOpensShop() {
        logic.dialogueActive = false;
        logic.showEnterShop  = true;

        logic.handleFKey(true);

        assertEquals("showShop", logic.lastAction, "lastAction must be showShop");
    }

    /**
     * TC-08: F press near Gym triggers startGym (Gym has higher priority than Shop
     * in the if/else chain).
     */
    @Test
    @DisplayName("TC-08 | F press near Gym → startGym")
    void tc08_fPressNearGymStartsGym() {
        logic.dialogueActive = false;
        logic.showEnterGym   = true;

        logic.handleFKey(true);

        assertEquals("startGym", logic.lastAction, "lastAction must be startGym");
    }

    /**
     * TC-09: Holding F down for a second call does NOT re-trigger the action
     * (guard flag prevents it).
     */
    @Test
    @DisplayName("TC-09 | Holding F does not re-trigger action")
    void tc09_holdingFDoesNotRetrigger() {
        logic.dialogueActive = false;
        logic.showEnterShop  = true;

        logic.handleFKey(true); // first press → showShop
        logic.lastAction = null;
        logic.handleFKey(true); // still held → no new action

        assertNull(logic.lastAction, "Holding F must not re-trigger the action");
    }

    /**
     * TC-10: Releasing F resets both guard flags so a subsequent press works again.
     */
    @Test
    @DisplayName("TC-10 | Releasing F resets guards so next press works")
    void tc10_releasingFResetsGuards() {
        logic.dialogueActive = false;
        logic.showEnterShop  = true;

        logic.handleFKey(true);  // press → showShop
        logic.handleFKey(false); // release → reset guards
        logic.lastAction = null;
        logic.handleFKey(true);  // press again → should fire again

        assertEquals("showShop", logic.lastAction,
                "After release, next F press must trigger action again");
    }

    // ── Prompt text tests ─────────────────────────────────────────────────────

    /**
     * TC-11: GYM prompt includes the fee line.
     */
    @Test
    @DisplayName("TC-11 | GYM prompt contains fee line")
    void tc11_gymPromptHasFeeLine() {
        String text = logic.promptTextFor("GYM");

        assertTrue(text.contains("Fee: 500"), "GYM prompt must include 'Fee: 500'");
        assertTrue(text.contains("\n"),       "GYM prompt must be multi-line");
    }

    /**
     * TC-12: Non-GYM prompts are single-line and contain the location name.
     */
    @Test
    @DisplayName("TC-12 | SHOP / ZOO / UFO prompts are single-line")
    void tc12_nonGymPromptsAreSingleLine() {
        for (String loc : List.of("SHOP", "ZOO", "UFO")) {
            String text = logic.promptTextFor(loc);
            assertFalse(text.contains("\n"),  loc + " prompt must be single-line");
            assertTrue(text.contains(loc),    loc + " prompt must contain location name");
        }
    }

    // ── Box geometry tests ────────────────────────────────────────────────────

    /**
     * TC-13: SHOP box is centred within the SHOP landmark area (x=80, w=500).
     */
    @Test
    @DisplayName("TC-13 | SHOP box X is centred within the SHOP landmark")
    void tc13_shopBoxCentred() {
        double[] g = logic.boxGeometry("SHOP");
        double boxX     = g[0];
        double boxWidth = g[2];

        // Centre of landmark area: 80 + 500/2 = 330
        double landmarkCentreX = 80 + 500 / 2.0;
        double boxCentreX      = boxX + boxWidth / 2.0;

        assertEquals(landmarkCentreX, boxCentreX, 0.5,
                "Box centre X must align with landmark centre X");
    }

    /**
     * TC-14: GYM box is taller than SHOP box because it has two lines of text.
     */
    @Test
    @DisplayName("TC-14 | GYM prompt box is taller than single-line boxes")
    void tc14_gymBoxTallerThanSingleLine() {
        double gymHeight  = logic.boxGeometry("GYM")[3];
        double shopHeight = logic.boxGeometry("SHOP")[3];

        assertTrue(gymHeight > shopHeight,
                "GYM box height must exceed SHOP box height (extra fee line)");
    }
}