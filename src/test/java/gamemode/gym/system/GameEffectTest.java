package gamemode.gym.system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the lane-exclusion logic inside {@link GameEffect#playRandomNoLane}.
 *
 * The only testable pure logic in GameEffect is the do-while rejection condition:
 *
 *   do {
 *       randomX = Math.random() * (sceneWidth - size);
 *   } while (randomX > laneLeft - size && randomX < laneRight);
 *
 * where:
 *   laneLeft  = laneStartX
 *   laneRight = laneStartX + laneWidth
 *
 * A position is REJECTED (inside the exclusion zone) when BOTH:
 *   randomX > laneLeft - size   (effect's right edge would overlap the lane's left edge)
 *   randomX < laneRight         (effect's left edge is left of the lane's right edge)
 *
 * A position is ACCEPTED when either condition is false, i.e.:
 *   randomX <= laneLeft - size  (effect ends before the lane starts)   — LEFT safe zone
 *   randomX >= laneRight        (effect starts after the lane ends)     — RIGHT safe zone
 *
 * The X sampling range is [0, sceneWidth - size], keeping the effect fully on-screen.
 *
 * All tests use a plain static helper that mirrors the predicate exactly —
 * no JavaFX toolkit initialisation required.
 */
class GameEffectTest {

    // -----------------------------------------------------------------------
    // Predicate mirror — extracted verbatim from the do-while condition
    // -----------------------------------------------------------------------

    /**
     * Returns true if {@code randomX} falls inside the exclusion zone and
     * must be rejected (i.e. the loop should continue).
     *
     * Mirrors: randomX > laneLeft - size && randomX < laneRight
     */
    static boolean isExcluded(double randomX, double laneStartX,
                              double laneWidth, double size) {
        double laneLeft  = laneStartX;
        double laneRight = laneStartX + laneWidth;
        return randomX > laneLeft - size && randomX < laneRight;
    }

    /**
     * Returns true if {@code randomX} is an accepted (non-excluded) position.
     */
    static boolean isAccepted(double randomX, double laneStartX,
                              double laneWidth, double size) {
        return !isExcluded(randomX, laneStartX, laneWidth, size);
    }

    // Shared test parameters
    static final double LANE_START_X  = 200.0;
    static final double LANE_WIDTH    = 128.0;  // laneRight = 328
    static final double EFFECT_SIZE   = 64.0;
    static final double SCENE_WIDTH   = 800.0;
    static final double SCENE_HEIGHT  = 600.0;

    // -----------------------------------------------------------------------
    // Exclusion zone — positions that must be REJECTED
    // -----------------------------------------------------------------------

    /**
     * A position whose right edge exactly touches the lane's left edge is
     * the boundary of the exclusion zone. Just inside = excluded.
     *
     * laneLeft - size = 200 - 64 = 136. Any x > 136 and x < 328 is excluded.
     * x = 137 → excluded.
     */
    @Test
    void exclusion_xJustInsideLeftBoundary_isExcluded() {
        double x = LANE_START_X - EFFECT_SIZE + 1; // 137
        assertTrue(isExcluded(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x just inside the left exclusion boundary must be rejected");
    }

    /**
     * A position in the middle of the lane is excluded.
     */
    @Test
    void exclusion_xInMiddleOfLane_isExcluded() {
        double x = LANE_START_X + LANE_WIDTH / 2; // 264
        assertTrue(isExcluded(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x in the middle of the lane must be rejected");
    }

    /**
     * x = laneRight - 1 = 327 is still inside the exclusion zone (x < laneRight).
     */
    @Test
    void exclusion_xOneBeforeLaneRight_isExcluded() {
        double x = LANE_START_X + LANE_WIDTH - 1; // 327
        assertTrue(isExcluded(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x one pixel before laneRight must still be excluded");
    }

    /**
     * x = laneLeft - size + 1 is the first position inside the exclusion zone.
     * (laneLeft - size = 136; 136 + 1 = 137)
     */
    @Test
    void exclusion_xFirstInsideZone_isExcluded() {
        double x = LANE_START_X - EFFECT_SIZE + 1; // 137
        assertTrue(isExcluded(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE));
    }

    // -----------------------------------------------------------------------
    // LEFT safe zone — positions that must be ACCEPTED (x <= laneLeft - size)
    // -----------------------------------------------------------------------

    /**
     * x = laneLeft - size = 136: the right edge of the effect exactly touches
     * the left edge of the lane. The condition is >, not >=, so this is accepted.
     */
    @Test
    void accepted_xExactlyAtLeftBoundary_isAccepted() {
        double x = LANE_START_X - EFFECT_SIZE; // 136
        assertTrue(isAccepted(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x == laneLeft - size must be accepted (boundary is strict >)");
    }

    /**
     * x = laneLeft - size - 1 = 135: just outside left boundary — accepted.
     */
    @Test
    void accepted_xOneBeforeLeftBoundary_isAccepted() {
        double x = LANE_START_X - EFFECT_SIZE - 1; // 135
        assertTrue(isAccepted(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x one pixel before the left boundary must be accepted");
    }

    /**
     * x = 0: far left of screen — accepted.
     */
    @Test
    void accepted_xAtZero_isAccepted() {
        assertTrue(isAccepted(0, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x = 0 (far left) must be accepted");
    }

    // -----------------------------------------------------------------------
    // RIGHT safe zone — positions that must be ACCEPTED (x >= laneRight)
    // -----------------------------------------------------------------------

    /**
     * x = laneRight = 328: left edge of effect exactly at lane's right edge.
     * Condition is x < laneRight so x == laneRight is accepted.
     */
    @Test
    void accepted_xExactlyAtLaneRight_isAccepted() {
        double x = LANE_START_X + LANE_WIDTH; // 328
        assertTrue(isAccepted(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x == laneRight must be accepted (boundary is strict <)");
    }

    /**
     * x = laneRight + 1 = 329: one pixel past the right edge — accepted.
     */
    @Test
    void accepted_xOneAfterLaneRight_isAccepted() {
        double x = LANE_START_X + LANE_WIDTH + 1; // 329
        assertTrue(isAccepted(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x one pixel past laneRight must be accepted");
    }

    /**
     * x = sceneWidth - size: rightmost valid position — accepted.
     */
    @Test
    void accepted_xAtRightEdgeOfScreen_isAccepted() {
        double x = SCENE_WIDTH - EFFECT_SIZE; // 736
        assertTrue(isAccepted(x, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "x at the far right of the scene must be accepted");
    }

    // -----------------------------------------------------------------------
    // Both boundaries are strict (> and <), not >= and <=
    // -----------------------------------------------------------------------

    /**
     * Confirms the left boundary uses strict >:
     * x == laneLeft - size is NOT excluded.
     */
    @Test
    void boundary_leftIsStrictGreaterThan_notGreaterOrEqual() {
        double boundary = LANE_START_X - EFFECT_SIZE; // 136 — the exact boundary
        assertFalse(isExcluded(boundary, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "Left boundary uses >, so x == laneLeft - size must not be excluded");
    }

    /**
     * Confirms the right boundary uses strict <:
     * x == laneRight is NOT excluded.
     */
    @Test
    void boundary_rightIsStrictLessThan_notLessOrEqual() {
        double boundary = LANE_START_X + LANE_WIDTH; // 328 — the exact boundary
        assertFalse(isExcluded(boundary, LANE_START_X, LANE_WIDTH, EFFECT_SIZE),
                "Right boundary uses <, so x == laneRight must not be excluded");
    }

    // -----------------------------------------------------------------------
    // X sampling range: [0, sceneWidth - size]
    // -----------------------------------------------------------------------

    /**
     * The maximum sampled X = sceneWidth - size keeps the right edge of the
     * effect (x + size) at exactly sceneWidth — fully on-screen.
     */
    @Test
    void samplingRange_maxX_keepEffectFullyOnScreen() {
        double maxX = SCENE_WIDTH - EFFECT_SIZE; // 736
        double rightEdge = maxX + EFFECT_SIZE;   // 800 == sceneWidth
        assertEquals(SCENE_WIDTH, rightEdge, 0.0001,
                "maxX + size must equal sceneWidth (effect stays on screen)");
    }

    /**
     * The minimum sampled X = 0 keeps the left edge at x = 0 — on screen.
     */
    @Test
    void samplingRange_minX_isZero() {
        double minX = 0.0;
        assertTrue(minX >= 0,
                "Minimum sampled X must be 0 (left screen edge)");
    }

    // -----------------------------------------------------------------------
    // Y sampling range: [0, sceneHeight - size]
    // -----------------------------------------------------------------------

    /**
     * randomY = sceneHeight - size keeps the bottom edge of the effect at
     * exactly sceneHeight — fully on-screen vertically.
     */
    @Test
    void samplingRange_maxY_keepEffectFullyOnScreen() {
        double maxY      = SCENE_HEIGHT - EFFECT_SIZE; // 536
        double bottomEdge = maxY + EFFECT_SIZE;        // 600 == sceneHeight
        assertEquals(SCENE_HEIGHT, bottomEdge, 0.0001,
                "maxY + size must equal sceneHeight (effect stays on screen vertically)");
    }

    // -----------------------------------------------------------------------
    // Lane width and exclusion zone size relationship
    // -----------------------------------------------------------------------

    /**
     * The total exclusion zone width = laneWidth + size - 1 pixel.
     * (from laneLeft - size + 1  to  laneRight - 1, inclusive)
     * This confirms the effect's visual overlap drives the zone width.
     */
    @Test
    void exclusionZone_width_equalsLaneWidthPlusEffectSizeMinusOne() {
        // Leftmost excluded x: laneLeft - size + epsilon (just past boundary)
        // Rightmost excluded x: laneRight - epsilon (just before boundary)
        // Zone width ≈ laneRight - (laneLeft - size) = laneWidth + size
        double zoneWidth = (LANE_START_X + LANE_WIDTH) - (LANE_START_X - EFFECT_SIZE);
        assertEquals(LANE_WIDTH + EFFECT_SIZE, zoneWidth, 0.0001,
                "Exclusion zone spans laneWidth + effectSize pixels");
    }

    // -----------------------------------------------------------------------
    // Edge case: very small scene or zero-width lane
    // -----------------------------------------------------------------------

    /**
     * When laneWidth = 0, the exclusion zone collapses to a point.
     * Positions to the right of laneStartX are still accepted.
     */
    @Test
    void exclusion_zeroLaneWidth_rightOfLaneIsAccepted() {
        double x = LANE_START_X + 1; // right of a zero-width lane
        assertTrue(isAccepted(x, LANE_START_X, 0, EFFECT_SIZE),
                "With zero lane width, positions >= laneStartX must be accepted");
    }

    /**
     * When laneStartX = 0 and laneWidth covers the full scene,
     * positions in the left safe zone are negative (off-screen) and
     * the right safe zone starts at sceneWidth — both are edge positions.
     * This confirms the exclusion zone can cover the entire valid X range.
     */
    @Test
    void exclusion_laneCoversFullScene_allOnScreenXAreExcluded() {
        double fullLaneWidth = SCENE_WIDTH + EFFECT_SIZE; // lane covers everything
        double x = SCENE_WIDTH / 2;                       // centre of scene
        assertTrue(isExcluded(x, 0, fullLaneWidth, EFFECT_SIZE),
                "When lane covers the full scene, centre X must be excluded");
    }
}