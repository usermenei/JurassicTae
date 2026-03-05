package gamemode.fightscene;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests only the pure HP arithmetic logic extracted from {@link InfoBox}.
 *
 * <p>{@code InfoBox} extends {@link javafx.scene.layout.VBox}, so its constructor
 * requires a JavaFX runtime. Rather than spinning up JavaFX for every test,
 * the two testable computations — HP clamping and progress-bar fraction — are
 * extracted into the plain Java helper {@link HpLogic} and tested in isolation.</p>
 *
 * <h2>Logic units tested</h2>
 * <ul>
 *   <li><b>HP clamping</b> — {@code currentHp = max(0, hp)}, never negative.</li>
 *   <li><b>Progress fraction</b> — {@code currentHp / maxHp} as a {@code double}.</li>
 *   <li><b>Bar colour selection</b> — green above 50 %, yellow above 25 %, red at/below 25 %.</li>
 *   <li><b>Label text</b> — formatted as "{@code currentHp / maxHp}".</li>
 *   <li><b>setHp(int, int)</b> — max update propagates correctly.</li>
 * </ul>
 *
 * <p>No JavaFX, no Mockito. Run with: {@code ./gradlew test}</p>
 */
class InfoBoxTest {

    // ── Extracted logic ───────────────────────────────────────────────────────

    /**
     * Plain Java mirror of the HP-related logic inside {@link InfoBox}.
     *
     * <p>Contains no JavaFX types — safe to construct in any JVM environment.
     * Every method is a direct copy of the corresponding lines in
     * {@code InfoBox.setHp(int)} and {@code InfoBox.setHp(int, int)}.</p>
     */
    static class HpLogic {

        int    maxHp;
        int    currentHp;
        double progress;
        String barColour;
        String labelText;

        /**
         * Constructs the logic state with the given maximum HP.
         * Sets {@code currentHp = maxHp} (full HP on creation).
         *
         * @param maxHp maximum HP value
         */
        HpLogic(int maxHp) {
            this.maxHp     = maxHp;
            this.currentHp = maxHp;
        }

        /**
         * Mirrors {@link InfoBox#setHp(int)} exactly.
         *
         * @param hp new current HP (negative → clamped to 0)
         */
        void setHp(int hp) {
            this.currentHp = Math.max(0, hp);
            this.progress  = (double) currentHp / maxHp;
            this.labelText = currentHp + " / " + maxHp;

            if (progress > 0.5) {
                barColour = "green";
            } else if (progress > 0.25) {
                barColour = "yellow";
            } else {
                barColour = "red";
            }
        }

        /**
         * Mirrors {@link InfoBox#setHp(int, int)} exactly.
         *
         * @param hp  new current HP
         * @param max new maximum HP
         */
        void setHp(int hp, int max) {
            this.maxHp = max;
            setHp(hp);
        }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    HpLogic logic;

    @BeforeEach
    void setUp() {
        logic = new HpLogic(100); // 100 maxHp by default
    }

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * TC-01: On construction, currentHp equals maxHp (full HP).
     *
     * <p><em>Given</em> a new HpLogic with maxHp = 100,<br>
     * <em>Then</em> currentHp must equal 100.</p>
     */
    @Test
    @DisplayName("TC-01 | Initial HP equals maxHp (full bar)")
    void tc01_initialHpIsMax() {
        assertEquals(100, logic.currentHp, "currentHp must equal maxHp on creation");
    }

    // ── HP clamping ───────────────────────────────────────────────────────────

    /**
     * TC-02: A normal HP value is stored as-is.
     */
    @Test
    @DisplayName("TC-02 | Normal HP value is stored correctly")
    void tc02_normalHpStored() {
        logic.setHp(60);
        assertEquals(60, logic.currentHp);
    }

    /**
     * TC-03: Zero HP is stored as zero (not clamped further).
     */
    @Test
    @DisplayName("TC-03 | HP = 0 is stored as 0")
    void tc03_zeroHpStored() {
        logic.setHp(0);
        assertEquals(0, logic.currentHp);
    }

    /**
     * TC-04: Negative HP is clamped to 0.
     *
     * <p>Mirrors the {@code Math.max(0, hp)} guard in {@link InfoBox#setHp(int)}.</p>
     */
    @Test
    @DisplayName("TC-04 | Negative HP is clamped to 0")
    void tc04_negativeHpClamped() {
        logic.setHp(-30);
        assertEquals(0, logic.currentHp, "HP must not go below 0");
    }

    // ── Progress fraction ─────────────────────────────────────────────────────

    /**
     * TC-05: Full HP produces a progress of 1.0.
     */
    @Test
    @DisplayName("TC-05 | Full HP → progress = 1.0")
    void tc05_fullHpProgressIsOne() {
        logic.setHp(100);
        assertEquals(1.0, logic.progress, 0.001);
    }

    /**
     * TC-06: Half HP produces a progress of 0.5.
     */
    @Test
    @DisplayName("TC-06 | Half HP → progress = 0.5")
    void tc06_halfHpProgressIsHalf() {
        logic.setHp(50);
        assertEquals(0.5, logic.progress, 0.001);
    }

    /**
     * TC-07: Zero HP produces a progress of 0.0.
     */
    @Test
    @DisplayName("TC-07 | Zero HP → progress = 0.0")
    void tc07_zeroHpProgressIsZero() {
        logic.setHp(0);
        assertEquals(0.0, logic.progress, 0.001);
    }

    // ── Bar colour ────────────────────────────────────────────────────────────

    /**
     * TC-08: HP above 50 % → green bar.
     */
    @Test
    @DisplayName("TC-08 | HP > 50% → green bar")
    void tc08_highHpIsGreen() {
        logic.setHp(51);
        assertEquals("green", logic.barColour);
    }

    /**
     * TC-09: HP exactly at 50 % → yellow bar (boundary: not above 50 %).
     */
    @Test
    @DisplayName("TC-09 | HP = 50% → yellow bar (boundary)")
    void tc09_fiftyPercentIsYellow() {
        logic.setHp(50); // 50/100 = 0.5, not > 0.5
        assertEquals("yellow", logic.barColour);
    }

    /**
     * TC-10: HP between 25 % and 50 % (exclusive) → yellow bar.
     */
    @Test
    @DisplayName("TC-10 | HP 26–50% → yellow bar")
    void tc10_midHpIsYellow() {
        logic.setHp(30);
        assertEquals("yellow", logic.barColour);
    }

    /**
     * TC-11: HP exactly at 25 % → red bar (boundary: not above 25 %).
     */
    @Test
    @DisplayName("TC-11 | HP = 25% → red bar (boundary)")
    void tc11_twentyFivePercentIsRed() {
        logic.setHp(25); // 25/100 = 0.25, not > 0.25
        assertEquals("red", logic.barColour);
    }

    /**
     * TC-12: HP below 25 % → red bar.
     */
    @Test
    @DisplayName("TC-12 | HP < 25% → red bar")
    void tc12_lowHpIsRed() {
        logic.setHp(10);
        assertEquals("red", logic.barColour);
    }

    /**
     * TC-13: HP = 0 → red bar.
     */
    @Test
    @DisplayName("TC-13 | HP = 0 → red bar")
    void tc13_zeroHpIsRed() {
        logic.setHp(0);
        assertEquals("red", logic.barColour);
    }

    // ── Label text ────────────────────────────────────────────────────────────

    /**
     * TC-14: Label text is formatted as "{@code currentHp / maxHp}".
     */
    @Test
    @DisplayName("TC-14 | Label text format is 'currentHp / maxHp'")
    void tc14_labelTextFormat() {
        logic.setHp(70);
        assertEquals("70 / 100", logic.labelText);
    }

    /**
     * TC-15: Label text shows 0 when HP is clamped from a negative input.
     */
    @Test
    @DisplayName("TC-15 | Label shows '0 / maxHp' when HP goes negative")
    void tc15_labelTextAtZero() {
        logic.setHp(-999);
        assertEquals("0 / 100", logic.labelText);
    }

    // ── setHp(int, int) — max update ──────────────────────────────────────────

    /**
     * TC-16: {@code setHp(hp, max)} updates maxHp before calculating progress.
     *
     * <p>50 HP out of a new max of 200 should give 25 % progress (red bar).</p>
     */
    @Test
    @DisplayName("TC-16 | setHp(hp, max) updates maxHp correctly")
    void tc16_setHpWithMaxUpdatesMax() {
        logic.setHp(50, 200);

        assertAll(
                () -> assertEquals(200,    logic.maxHp,    "maxHp must update to 200"),
                () -> assertEquals(50,     logic.currentHp,"currentHp must be 50"),
                () -> assertEquals(0.25,   logic.progress,  0.001),
                () -> assertEquals("red",  logic.barColour, "25% → red"),
                () -> assertEquals("50 / 200", logic.labelText)
        );
    }

    /**
     * TC-17: {@code setHp(max, max)} after a max change gives a full green bar.
     */
    @Test
    @DisplayName("TC-17 | setHp(max, max) gives full green bar after max change")
    void tc17_fullHpAfterMaxChange() {
        logic.setHp(200, 200);

        assertEquals(1.0,     logic.progress,  0.001);
        assertEquals("green", logic.barColour);
        assertEquals("200 / 200", logic.labelText);
    }
}