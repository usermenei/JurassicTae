package gamemode.gym.system;

import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ComboManager}.
 *
 * ComboManager only depends on two Text nodes (for display) and pure arithmetic,
 * so real Text objects are used — no fakes needed.
 *
 * Multiplier formula: multiplier = 1.0 + (combo / 25.0)
 * Key thresholds:
 *   combo  0  → x1.00
 *   combo  1  → x1.04
 *   combo 25  → x2.00
 *   combo 50  → x3.00
 */
class ComboManagerTest {

    private Text          comboText;
    private Text          multiplierText;
    private ComboManager  manager;

    @BeforeEach
    void setUp() {
        comboText      = new Text();
        multiplierText = new Text();
        manager        = new ComboManager(comboText, multiplierText);
    }

    // -----------------------------------------------------------------------
    // Constructor / initial state
    // -----------------------------------------------------------------------

    @Test
    void constructor_initialMultiplier_isOne() {
        assertEquals(1.0, manager.getMultiplier(), 0.0001,
                "Initial multiplier must be 1.0");
    }

    @Test
    void constructor_comboText_showsZero() {
        assertEquals("Combo : 0", comboText.getText(),
                "Combo text must be initialised to 'Combo : 0'");
    }

    @Test
    void constructor_multiplierText_showsOnePointZero() {
        assertEquals("x1.00", multiplierText.getText(),
                "Multiplier text must be initialised to 'x1.00'");
    }

    // -----------------------------------------------------------------------
    // addCombo — combo counter
    // -----------------------------------------------------------------------

    @Test
    void addCombo_singleCall_comboTextShowsOne() {
        manager.addCombo();
        assertEquals("Combo : 1", comboText.getText());
    }

    @Test
    void addCombo_threeCalls_comboTextShowsThree() {
        manager.addCombo();
        manager.addCombo();
        manager.addCombo();
        assertEquals("Combo : 3", comboText.getText());
    }

    // -----------------------------------------------------------------------
    // addCombo — multiplier arithmetic
    // -----------------------------------------------------------------------

    /**
     * After 1 hit: multiplier = 1.0 + 1/25.0 = 1.04
     */
    @Test
    void addCombo_oneHit_multiplierIsOnePointZeroFour() {
        manager.addCombo();
        assertEquals(1.0 + 1 / 25.0, manager.getMultiplier(), 0.0001);
    }

    /**
     * After 25 hits: multiplier = 1.0 + 25/25.0 = 2.0
     */
    @Test
    void addCombo_twentyFiveHits_multiplierIsTwo() {
        for (int i = 0; i < 25; i++) manager.addCombo();
        assertEquals(2.0, manager.getMultiplier(), 0.0001,
                "25 combo must yield multiplier x2.00");
    }

    /**
     * After 50 hits: multiplier = 1.0 + 50/25.0 = 3.0
     */
    @Test
    void addCombo_fiftyHits_multiplierIsThree() {
        for (int i = 0; i < 50; i++) manager.addCombo();
        assertEquals(3.0, manager.getMultiplier(), 0.0001,
                "50 combo must yield multiplier x3.00");
    }

    /**
     * Multiplier grows continuously — each hit increases it by 1/25.
     */
    @Test
    void addCombo_incrementalGrowth_multiplierIncreasesEachHit() {
        double prev = manager.getMultiplier();
        for (int i = 0; i < 10; i++) {
            manager.addCombo();
            assertTrue(manager.getMultiplier() > prev,
                    "Multiplier must increase with each hit");
            prev = manager.getMultiplier();
        }
    }

    /**
     * Multiplier is never below 1.0 even on the first hit.
     */
    @Test
    void addCombo_multiplierNeverBelowOne() {
        manager.addCombo();
        assertTrue(manager.getMultiplier() >= 1.0,
                "Multiplier must always be >= 1.0");
    }

    // -----------------------------------------------------------------------
    // addCombo — multiplier text formatting
    // -----------------------------------------------------------------------

    @Test
    void addCombo_twentyFiveHits_multiplierTextShowsTwoPointZero() {
        for (int i = 0; i < 25; i++) manager.addCombo();
        assertEquals("x2.00", multiplierText.getText(),
                "Multiplier text must show 'x2.00' at 25 combo");
    }

    @Test
    void addCombo_oneHit_multiplierTextFormattedToTwoDecimalPlaces() {
        manager.addCombo();
        // 1 + 1/25 = 1.04
        assertEquals("x1.04", multiplierText.getText());
    }

    @Test
    void addCombo_tenHits_multiplierTextIsCorrect() {
        for (int i = 0; i < 10; i++) manager.addCombo();
        // 1 + 10/25 = 1.40
        assertEquals("x1.40", multiplierText.getText());
    }

    // -----------------------------------------------------------------------
    // resetCombo
    // -----------------------------------------------------------------------

    @Test
    void resetCombo_afterHits_multiplierReturnsToOne() {
        for (int i = 0; i < 25; i++) manager.addCombo();
        manager.resetCombo();
        assertEquals(1.0, manager.getMultiplier(), 0.0001,
                "Multiplier must reset to 1.0 after resetCombo()");
    }

    @Test
    void resetCombo_afterHits_comboTextShowsZero() {
        manager.addCombo();
        manager.addCombo();
        manager.resetCombo();
        assertEquals("Combo : 0", comboText.getText(),
                "Combo text must show 0 after reset");
    }

    @Test
    void resetCombo_afterHits_multiplierTextShowsOnePointZero() {
        for (int i = 0; i < 50; i++) manager.addCombo();
        manager.resetCombo();
        assertEquals("x1.00", multiplierText.getText(),
                "Multiplier text must show 'x1.00' after reset");
    }

    @Test
    void resetCombo_withNoHits_remainsAtInitialState() {
        manager.resetCombo(); // reset on a fresh manager
        assertEquals(1.0, manager.getMultiplier(), 0.0001);
        assertEquals("Combo : 0", comboText.getText());
        assertEquals("x1.00", multiplierText.getText());
    }

    // -----------------------------------------------------------------------
    // addCombo after reset — combo and multiplier rebuild from zero
    // -----------------------------------------------------------------------

    @Test
    void addCombo_afterReset_comboRebuildsFromZero() {
        for (int i = 0; i < 25; i++) manager.addCombo();
        manager.resetCombo();
        manager.addCombo();
        // combo = 1 → multiplier = 1 + 1/25 = 1.04
        assertEquals(1.0 + 1 / 25.0, manager.getMultiplier(), 0.0001,
                "After reset, first hit must restart multiplier from combo=1");
    }

    @Test
    void addCombo_afterReset_twentyFiveMoreHits_returnsToX2() {
        for (int i = 0; i < 50; i++) manager.addCombo(); // reach x3
        manager.resetCombo();
        for (int i = 0; i < 25; i++) manager.addCombo(); // rebuild to x2
        assertEquals(2.0, manager.getMultiplier(), 0.0001,
                "25 hits after reset must yield x2.00 again");
    }

    // -----------------------------------------------------------------------
    // Multiple resets
    // -----------------------------------------------------------------------

    @Test
    void resetCombo_calledTwice_staysAtInitialState() {
        manager.addCombo();
        manager.resetCombo();
        manager.resetCombo();
        assertEquals(1.0, manager.getMultiplier(), 0.0001);
        assertEquals("Combo : 0", comboText.getText());
    }

    // -----------------------------------------------------------------------
    // getMultiplier — consistency with text
    // -----------------------------------------------------------------------

    /**
     * getMultiplier() must always match the numeric value shown in multiplierText.
     */
    @Test
    void getMultiplier_matchesMultiplierText_afterHits() {
        for (int i = 0; i < 13; i++) manager.addCombo();
        String expected = "x" + String.format("%.2f", manager.getMultiplier());
        assertEquals(expected, multiplierText.getText(),
                "getMultiplier() must be consistent with the displayed text");
    }

    @Test
    void getMultiplier_matchesMultiplierText_afterReset() {
        for (int i = 0; i < 30; i++) manager.addCombo();
        manager.resetCombo();
        String expected = "x" + String.format("%.2f", manager.getMultiplier());
        assertEquals(expected, multiplierText.getText());
    }
}