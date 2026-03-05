package gamemode.gym.system;

import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ScoreManager}.
 *
 * ScoreManager only depends on a Text node and pure arithmetic,
 * so a real Text object is used — no fakes needed.
 *
 * Key behaviours:
 *   - Initial score is 0
 *   - addScore(base, mult) adds (int)(base * mult) to the score
 *   - subtractScore(amount) subtracts amount, clamped to minimum 0
 *   - scoreText always reflects the current score as "Score : N"
 */
class ScoreManagerTest {

    private Text         scoreText;
    private ScoreManager manager;

    @BeforeEach
    void setUp() {
        scoreText = new Text();
        manager   = new ScoreManager(scoreText);
    }

    // -----------------------------------------------------------------------
    // Constructor / initial state
    // -----------------------------------------------------------------------

    @Test
    void constructor_initialScore_isZero() {
        assertEquals(0, manager.getScore(),
                "Initial score must be 0");
    }

    @Test
    void constructor_scoreText_showsZero() {
        assertEquals("Score : 0", scoreText.getText(),
                "Score text must be initialised to 'Score : 0'");
    }

    // -----------------------------------------------------------------------
    // addScore — arithmetic
    // -----------------------------------------------------------------------

    @Test
    void addScore_baseHundredMultiplierOne_addsHundred() {
        manager.addScore(100, 1.0);
        assertEquals(100, manager.getScore());
    }

    @Test
    void addScore_baseHundredMultiplierTwo_addsTwoHundred() {
        manager.addScore(100, 2.0);
        assertEquals(200, manager.getScore());
    }

    /**
     * Result is cast to int: (int)(100 * 1.5) = 150.
     */
    @Test
    void addScore_fractionalMultiplier_truncatesToInt() {
        manager.addScore(100, 1.5);
        assertEquals(150, manager.getScore(),
                "(int)(100 * 1.5) = 150");
    }

    /**
     * Truncation floors toward zero: (int)(100 * 1.04) = (int)104.0 = 104.
     */
    @Test
    void addScore_smallFractionalMultiplier_truncatesCorrectly() {
        manager.addScore(100, 1.04);
        assertEquals(104, manager.getScore(),
                "(int)(100 * 1.04) = 104");
    }

    @Test
    void addScore_multipleCalls_accumulatesCorrectly() {
        manager.addScore(100, 1.0);
        manager.addScore(100, 2.0);
        assertEquals(300, manager.getScore(),
                "100 + 200 = 300");
    }

    @Test
    void addScore_zeroBase_addsNothing() {
        manager.addScore(0, 5.0);
        assertEquals(0, manager.getScore(),
                "Base 0 must add nothing regardless of multiplier");
    }

    @Test
    void addScore_multiplierZero_addsNothing() {
        manager.addScore(100, 0.0);
        assertEquals(0, manager.getScore(),
                "Multiplier 0 must add nothing");
    }

    // -----------------------------------------------------------------------
    // addScore — text update
    // -----------------------------------------------------------------------

    @Test
    void addScore_updatesScoreText() {
        manager.addScore(100, 1.0);
        assertEquals("Score : 100", scoreText.getText());
    }

    @Test
    void addScore_textMatchesGetScore() {
        manager.addScore(250, 2.0);
        assertEquals("Score : " + manager.getScore(), scoreText.getText(),
                "Score text must always match getScore()");
    }

    // -----------------------------------------------------------------------
    // subtractScore — arithmetic
    // -----------------------------------------------------------------------

    @Test
    void subtractScore_fromPositiveScore_reducesCorrectly() {
        manager.addScore(100, 1.0);
        manager.subtractScore(30);
        assertEquals(70, manager.getScore());
    }

    @Test
    void subtractScore_exactBalance_resultIsZero() {
        manager.addScore(100, 1.0);
        manager.subtractScore(100);
        assertEquals(0, manager.getScore(),
                "Subtracting exactly the current score must leave 0");
    }

    /**
     * Score is clamped to 0 — never goes negative.
     */
    @Test
    void subtractScore_moreThanCurrentScore_clampsToZero() {
        manager.addScore(50, 1.0);
        manager.subtractScore(100);
        assertEquals(0, manager.getScore(),
                "Score must be clamped to 0, never negative");
    }

    @Test
    void subtractScore_fromZero_staysAtZero() {
        manager.subtractScore(30);
        assertEquals(0, manager.getScore(),
                "Subtracting from 0 must leave score at 0");
    }

    @Test
    void subtractScore_zeroAmount_doesNotChangeScore() {
        manager.addScore(100, 1.0);
        manager.subtractScore(0);
        assertEquals(100, manager.getScore(),
                "Subtracting 0 must not change the score");
    }

    @Test
    void subtractScore_multipleTimes_clampsCorrectly() {
        manager.addScore(100, 1.0);
        manager.subtractScore(60);  // 40
        manager.subtractScore(60);  // would be -20, clamped to 0
        assertEquals(0, manager.getScore());
    }

    // -----------------------------------------------------------------------
    // subtractScore — text update
    // -----------------------------------------------------------------------

    @Test
    void subtractScore_updatesScoreText() {
        manager.addScore(100, 1.0);
        manager.subtractScore(30);
        assertEquals("Score : 70", scoreText.getText());
    }

    @Test
    void subtractScore_clampedToZero_textShowsZero() {
        manager.subtractScore(999);
        assertEquals("Score : 0", scoreText.getText(),
                "Text must show 'Score : 0' when score is clamped");
    }

    @Test
    void subtractScore_textMatchesGetScore() {
        manager.addScore(200, 1.0);
        manager.subtractScore(50);
        assertEquals("Score : " + manager.getScore(), scoreText.getText());
    }

    // -----------------------------------------------------------------------
    // getScore — consistency with text
    // -----------------------------------------------------------------------

    @Test
    void getScore_afterAddAndSubtract_matchesText() {
        manager.addScore(300, 1.0);
        manager.subtractScore(100);
        manager.addScore(50, 2.0);
        assertEquals("Score : " + manager.getScore(), scoreText.getText());
    }

    @Test
    void getScore_neverNegative_afterMultipleSubtractions() {
        for (int i = 0; i < 10; i++) {
            manager.subtractScore(50);
        }
        assertTrue(manager.getScore() >= 0,
                "Score must never be negative after repeated subtractions");
    }

    // -----------------------------------------------------------------------
    // Mixed add and subtract sequences
    // -----------------------------------------------------------------------

    @Test
    void mixed_addThenSubtract_correctFinalScore() {
        manager.addScore(100, 1.0);   // 100
        manager.subtractScore(30);    // 70
        manager.addScore(100, 2.0);   // 270
        manager.subtractScore(20);    // 250
        assertEquals(250, manager.getScore());
    }

    @Test
    void mixed_subtractBelowZeroThenAdd_rebuildsFromZero() {
        manager.addScore(10, 1.0);    // 10
        manager.subtractScore(100);   // clamped to 0
        manager.addScore(50, 1.0);    // 50
        assertEquals(50, manager.getScore(),
                "After clamp to 0, subsequent adds build from 0");
    }
}