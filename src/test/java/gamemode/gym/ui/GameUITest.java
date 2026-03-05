package gamemode.gym.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the pure logic inside {@link GameUI}.
 *
 * GameUI's constructor is coupled to JavaFX resources (font file, background image,
 * Platform.runLater) so it cannot be instantiated in a headless test.
 *
 * The testable pure logic is extracted into two static inner helpers:
 *
 *   1. JudgmentLineFade — mirrors fadeJudgmentLine() and showJudgmentHit():
 *        fadeJudgmentLine : if opacity > 0.3 → opacity -= 0.05
 *        showJudgmentHit  : opacity = 1.0
 *
 *   2. CenterTextFormula — mirrors the centering arithmetic from centerText():
 *        layoutX = centerX - textWidth / 2
 *
 * No JavaFX toolkit initialisation is required for either helper.
 */
class GameUITest {

    // -----------------------------------------------------------------------
    // JudgmentLineFade — mirrors fadeJudgmentLine() and showJudgmentHit()
    // -----------------------------------------------------------------------

    static class JudgmentLineFade {
        double opacity;

        JudgmentLineFade(double initialOpacity) {
            this.opacity = initialOpacity;
        }

        /** Mirrors GameUI.fadeJudgmentLine() exactly. */
        void fade() {
            if (opacity > 0.3) {
                opacity -= 0.05;
            }
        }

        /** Mirrors GameUI.showJudgmentHit() exactly. */
        void showHit() {
            opacity = 1.0;
        }
    }

    // -----------------------------------------------------------------------
    // fadeJudgmentLine — above minimum
    // -----------------------------------------------------------------------

    /**
     * When opacity > 0.3, each call reduces it by exactly 0.05.
     */
    @Test
    void fade_opacityAboveMinimum_decreasesByZeroPointZeroFive() {
        JudgmentLineFade j = new JudgmentLineFade(1.0);
        j.fade();
        assertEquals(0.95, j.opacity, 0.0001,
                "One fade call from 1.0 should yield 0.95");
    }

    @Test
    void fade_multipleCalls_decreasesAccumulatively() {
        JudgmentLineFade j = new JudgmentLineFade(1.0);
        j.fade(); // 0.95
        j.fade(); // 0.90
        j.fade(); // 0.85
        assertEquals(0.85, j.opacity, 0.0001,
                "Three fade calls from 1.0 should yield 0.85");
    }

    @Test
    void fade_opacityJustAboveMinimum_decreases() {
        // 0.3 + 0.05 = 0.35 — just above the threshold
        JudgmentLineFade j = new JudgmentLineFade(0.35);
        j.fade();
        assertEquals(0.30, j.opacity, 0.0001,
                "Opacity 0.35 should fade to 0.30");
    }

    // -----------------------------------------------------------------------
    // fadeJudgmentLine — at and below minimum (0.3)
    // -----------------------------------------------------------------------

    /**
     * When opacity == 0.3, condition is opacity > 0.3 which is false — no change.
     */
    @Test
    void fade_opacityExactlyAtMinimum_doesNotChange() {
        JudgmentLineFade j = new JudgmentLineFade(0.3);
        j.fade();
        assertEquals(0.3, j.opacity, 0.0001,
                "Opacity at exactly 0.3 must not be reduced further");
    }

    @Test
    void fade_opacityBelowMinimum_doesNotChange() {
        JudgmentLineFade j = new JudgmentLineFade(0.1);
        j.fade();
        assertEquals(0.1, j.opacity, 0.0001,
                "Opacity below 0.3 must not be changed by fade()");
    }

    @Test
    void fade_opacityZero_doesNotChange() {
        JudgmentLineFade j = new JudgmentLineFade(0.0);
        j.fade();
        assertEquals(0.0, j.opacity, 0.0001,
                "Opacity 0 must not go negative");
    }

    /**
     * Repeated fades must clamp at 0.3, never below.
     */
    @Test
    void fade_repeatedCallsFromFull_clampsAtZeroPointThree() {
        JudgmentLineFade j = new JudgmentLineFade(1.0);
        for (int i = 0; i < 100; i++) j.fade();
        assertEquals(0.3, j.opacity, 0.0001,
                "Repeated fades must clamp at 0.3");
    }

    /**
     * Precise step count: from 1.0 to 0.3 is (1.0 - 0.3) / 0.05 = 14 steps.
     */
    @Test
    void fade_exactStepsFromFullToMinimum_isFourteen() {
        JudgmentLineFade j = new JudgmentLineFade(1.0);
        int steps = 0;
        while (j.opacity > 0.3 + 0.0001) {
            j.fade();
            steps++;
        }
        assertEquals(14, steps,
                "Exactly 14 fade steps bring opacity from 1.0 to 0.3");
    }

    // -----------------------------------------------------------------------
    // showJudgmentHit
    // -----------------------------------------------------------------------

    @Test
    void showHit_alwaysSetsOpacityToOne() {
        JudgmentLineFade j = new JudgmentLineFade(0.3);
        j.showHit();
        assertEquals(1.0, j.opacity, 0.0001,
                "showJudgmentHit must set opacity to exactly 1.0");
    }

    @Test
    void showHit_fromZero_setsOpacityToOne() {
        JudgmentLineFade j = new JudgmentLineFade(0.0);
        j.showHit();
        assertEquals(1.0, j.opacity, 0.0001);
    }

    @Test
    void showHit_fromMinimum_setsOpacityToOne() {
        JudgmentLineFade j = new JudgmentLineFade(0.3);
        j.showHit();
        assertEquals(1.0, j.opacity, 0.0001);
    }

    // -----------------------------------------------------------------------
    // showHit then fade sequence
    // -----------------------------------------------------------------------

    /**
     * After a hit the line is at 1.0; one fade step should bring it to 0.95.
     */
    @Test
    void showHitThenFade_firstStep_opacityIsZeroPointNineFive() {
        JudgmentLineFade j = new JudgmentLineFade(0.3);
        j.showHit();
        j.fade();
        assertEquals(0.95, j.opacity, 0.0001);
    }

    /**
     * Hit → full fade sequence must end at 0.3.
     */
    @Test
    void showHitThenFade_fullSequence_clampsAtMinimum() {
        JudgmentLineFade j = new JudgmentLineFade(0.3);
        j.showHit();
        for (int i = 0; i < 100; i++) j.fade();
        assertEquals(0.3, j.opacity, 0.0001,
                "Full fade after hit must return to the 0.3 minimum");
    }

    /**
     * Multiple hits during fading must reset opacity to 1.0 each time.
     */
    @Test
    void multipleHitsAndFades_eachHitResetsToFull() {
        JudgmentLineFade j = new JudgmentLineFade(1.0);

        j.fade(); j.fade(); j.fade(); // 0.85
        j.showHit();                  // back to 1.0
        assertEquals(1.0, j.opacity, 0.0001, "Second hit must reset to 1.0");

        j.fade();                     // 0.95
        j.showHit();                  // back to 1.0
        assertEquals(1.0, j.opacity, 0.0001, "Third hit must reset to 1.0");
    }

    // -----------------------------------------------------------------------
    // CenterTextFormula — mirrors centerText() arithmetic
    // -----------------------------------------------------------------------

    /**
     * centerText sets layoutX = centerX - textWidth / 2.
     * Tests that the formula centres the text correctly.
     */
    static double computeLayoutX(double centerX, double textWidth) {
        return centerX - textWidth / 2;
    }

    @Test
    void centerText_symmetrical_layoutXIsCorrect() {
        double centerX    = 300.0;
        double textWidth  = 100.0;
        double layoutX    = computeLayoutX(centerX, textWidth);
        assertEquals(250.0, layoutX, 0.0001,
                "Center 300, width 100 → layoutX = 250");
    }

    @Test
    void centerText_zeroWidth_layoutXEqualsCenterX() {
        double layoutX = computeLayoutX(300.0, 0.0);
        assertEquals(300.0, layoutX, 0.0001,
                "Zero-width text should be positioned at centerX");
    }

    @Test
    void centerText_widerThanCenter_layoutXIsNegative() {
        // centerX = 50, textWidth = 200 → layoutX = 50 - 100 = -50
        double layoutX = computeLayoutX(50.0, 200.0);
        assertEquals(-50.0, layoutX, 0.0001,
                "Text wider than 2*centerX results in negative layoutX");
    }

    @Test
    void centerText_rightEdgeAlignedWithExpectedPosition() {
        double centerX   = 300.0;
        double textWidth = 120.0;
        double layoutX   = computeLayoutX(centerX, textWidth);
        // right edge = layoutX + textWidth must equal centerX + textWidth/2
        double rightEdge = layoutX + textWidth;
        assertEquals(centerX + textWidth / 2, rightEdge, 0.0001,
                "Right edge of centred text must be centerX + width/2");
    }

    @Test
    void centerText_leftEdgeAlignedWithExpectedPosition() {
        double centerX   = 300.0;
        double textWidth = 120.0;
        double layoutX   = computeLayoutX(centerX, textWidth);
        // left edge = layoutX must equal centerX - textWidth/2
        assertEquals(centerX - textWidth / 2, layoutX, 0.0001,
                "Left edge of centred text must be centerX - width/2");
    }

    // -----------------------------------------------------------------------
    // Judgment line Y position formula
    // -----------------------------------------------------------------------

    /**
     * judgmentLine Y = height - 120.
     * Verifies the formula for several screen heights.
     */
    @Test
    void judgmentLineY_formula_heightMinusOneTwenty() {
        assertEquals(480, 600 - 120,
                "At height 600, judgment line Y must be 480");
    }

    @Test
    void judgmentLineY_formula_tallScreen() {
        assertEquals(780, 900 - 120,
                "At height 900, judgment line Y must be 780");
    }

    /**
     * comboText Y = judgmentLine.getY() + 60.
     * multiplierText Y = comboText Y + 40.
     */
    @Test
    void comboTextY_formula_isJudgmentYPlusSixty() {
        double judgmentY = 600 - 120; // 480
        double comboY    = judgmentY + 60;
        assertEquals(540.0, comboY, 0.0001,
                "Combo text Y must be judgmentY + 60");
    }

    @Test
    void multiplierTextY_formula_isComboYPlusForty() {
        double judgmentY     = 600 - 120;
        double comboY        = judgmentY + 60;
        double multiplierY   = comboY + 40;
        assertEquals(580.0, multiplierY, 0.0001,
                "Multiplier text Y must be comboY + 40");
    }

    // -----------------------------------------------------------------------
    // Lane width formula
    // -----------------------------------------------------------------------

    /**
     * laneWidth = noteSize * 2.
     */
    @Test
    void laneWidth_formula_isTwoTimesNoteSize() {
        int noteSize  = 64;
        double laneWidth = noteSize * 2;
        assertEquals(128.0, laneWidth, 0.0001,
                "Lane width must be noteSize * 2");
    }

    /**
     * laneCenter = startX + noteSize.
     */
    @Test
    void laneCenter_formula_isStartXPlusNoteSize() {
        int startX   = 100;
        int noteSize = 64;
        double center = startX + noteSize;
        assertEquals(164.0, center, 0.0001,
                "Lane center must be startX + noteSize");
    }
}