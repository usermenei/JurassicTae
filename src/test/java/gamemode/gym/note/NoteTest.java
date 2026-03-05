package gamemode.gym.note;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Note}.
 *
 * All position logic in Note uses:
 *   - view.getY() / view.setY()  — pure double property, unaffected by image content
 *   - this.size                  — a plain int field, not derived from the image
 *
 * Passing null for the Image is therefore safe: ImageView stores position
 * independently of its image content, and no rendering is performed in tests.
 *
 * Constants used throughout:
 *   NOTE_SIZE = 50   (width and height in pixels)
 *   START_X   = 100  (base X offset)
 *   JUDGMENT_Y = 480  (Y coordinate of the judgment line)
 */
class NoteTest {

    private static final int    NOTE_SIZE   = 50;
    private static final int    START_X     = 100;
    private static final double JUDGMENT_Y  = 480.0;

    // -----------------------------------------------------------------------
    // Constructor — initial position
    // -----------------------------------------------------------------------

    /**
     * Lane 0: X = startX + 0 * size = startX.
     */
    @Test
    void constructor_lane0_xEqualsStartX() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        assertEquals(START_X, note.getView().getX(), 0.001,
                "Lane 0 X must equal startX");
    }

    /**
     * Lane 1: X = startX + 1 * size.
     */
    @Test
    void constructor_lane1_xEqualsStartXPlusSize() {
        Note note = new Note(null, 1, NOTE_SIZE, START_X);
        assertEquals(START_X + NOTE_SIZE, note.getView().getX(), 0.001,
                "Lane 1 X must equal startX + size");
    }

    /**
     * Initial Y must be -size so the note starts just above the screen.
     */
    @Test
    void constructor_initialY_isNegativeSize() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        assertEquals(-NOTE_SIZE, note.getView().getY(), 0.001,
                "Initial Y must be -size");
    }

    /**
     * getLane() must return exactly the lane passed to the constructor.
     */
    @Test
    void constructor_getLane_lane0() {
        assertEquals(0, new Note(null, 0, NOTE_SIZE, START_X).getLane());
    }

    @Test
    void constructor_getLane_lane1() {
        assertEquals(1, new Note(null, 1, NOTE_SIZE, START_X).getLane());
    }

    /**
     * Different startX values must shift both lanes proportionally.
     */
    @Test
    void constructor_differentStartX_shiftsPosition() {
        int altStartX = 200;
        Note lane0 = new Note(null, 0, NOTE_SIZE, altStartX);
        Note lane1 = new Note(null, 1, NOTE_SIZE, altStartX);

        assertEquals(200.0, lane0.getView().getX(), 0.001);
        assertEquals(250.0, lane1.getView().getX(), 0.001);
    }

    /**
     * Different note sizes must affect X placement of lane 1.
     */
    @Test
    void constructor_differentNoteSize_affectsLane1X() {
        int bigSize = 80;
        Note note = new Note(null, 1, bigSize, START_X);
        assertEquals(START_X + bigSize, note.getView().getX(), 0.001,
                "Lane 1 X = startX + size, so larger size shifts it further right");
    }

    // -----------------------------------------------------------------------
    // update(double speed)
    // -----------------------------------------------------------------------

    /**
     * A single update must move the note down by exactly the given speed.
     */
    @Test
    void update_singleCall_movesDownBySpeed() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double before = note.getView().getY();

        note.update(15.0);

        assertEquals(before + 15.0, note.getView().getY(), 0.001);
    }

    /**
     * Multiple update calls must accumulate movement correctly.
     */
    @Test
    void update_multipleCalls_accumulatesMovement() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double before = note.getView().getY();

        note.update(10.0);
        note.update(10.0);
        note.update(10.0);

        assertEquals(before + 30.0, note.getView().getY(), 0.001,
                "Three updates of 10 should total 30 pixels");
    }

    /**
     * Fractional speed must be applied exactly.
     */
    @Test
    void update_fractionalSpeed_appliedExactly() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double before = note.getView().getY();

        note.update(3.75);

        assertEquals(before + 3.75, note.getView().getY(), 0.001);
    }

    /**
     * Speed of zero must leave Y completely unchanged.
     */
    @Test
    void update_zeroSpeed_doesNotMoveNote() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double before = note.getView().getY();

        note.update(0.0);

        assertEquals(before, note.getView().getY(), 0.001,
                "Speed 0 must not change Y");
    }

    /**
     * X position must be unaffected by update() — only Y changes.
     */
    @Test
    void update_doesNotChangeX() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double beforeX = note.getView().getX();

        note.update(100.0);

        assertEquals(beforeX, note.getView().getX(), 0.001,
                "update() must not modify X");
    }

    // -----------------------------------------------------------------------
    // isOutOfScreen(int height)
    // -----------------------------------------------------------------------

    /**
     * A freshly-constructed note (Y = -size) is not off-screen.
     */
    @Test
    void isOutOfScreen_freshNote_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        assertFalse(note.isOutOfScreen(600),
                "A newly spawned note should not be off-screen");
    }

    /**
     * isOutOfScreen uses Y > height (strictly greater than).
     * Y == height means the top edge sits exactly at the boundary — still on-screen.
     */
    @Test
    void isOutOfScreen_yEqualsHeight_returnsFalse() {
        int height = 600;
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // Move top Y to exactly height: delta = height - (-NOTE_SIZE)
        note.update(height + NOTE_SIZE);
        assertFalse(note.isOutOfScreen(height),
                "Y == height is NOT off-screen (boundary is strictly >)");
    }

    /**
     * Y == height + 1 is strictly past the boundary — off-screen.
     */
    @Test
    void isOutOfScreen_yOnePixelPastHeight_returnsTrue() {
        int height = 600;
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        note.update(height + NOTE_SIZE + 1);
        assertTrue(note.isOutOfScreen(height),
                "Y == height + 1 must be off-screen");
    }

    /**
     * A note well below the screen must be off-screen.
     */
    @Test
    void isOutOfScreen_noteWellBelowScreen_returnsTrue() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        note.update(2000.0);
        assertTrue(note.isOutOfScreen(600));
    }

    /**
     * A note near the top but not yet at the boundary is not off-screen.
     */
    @Test
    void isOutOfScreen_noteNearTopOfScreen_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        note.update(10.0); // Y = -40, still well above any screen boundary
        assertFalse(note.isOutOfScreen(600));
    }

    // -----------------------------------------------------------------------
    // isHittable(double judgmentY)
    //
    // Logic: noteBottom = Y + size;  distance = |noteBottom - judgmentY|
    //        hittable   = distance < 70
    // -----------------------------------------------------------------------

    /**
     * Bottom edge exactly at judgmentY → distance = 0 → hittable.
     */
    @Test
    void isHittable_bottomExactlyAtJudgmentY_returnsTrue() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y  →  Y = JUDGMENT_Y - NOTE_SIZE
        note.update(JUDGMENT_Y - NOTE_SIZE - (-NOTE_SIZE)); // delta from initial Y
        assertTrue(note.isHittable(JUDGMENT_Y),
                "Bottom exactly at judgmentY (distance 0) must be hittable");
    }

    /**
     * Distance = 1 (bottom one pixel past judgmentY) → hittable.
     */
    @Test
    void isHittable_distanceOne_returnsTrue() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y + 1  →  Y = JUDGMENT_Y + 1 - NOTE_SIZE
        double targetY = JUDGMENT_Y + 1 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertTrue(note.isHittable(JUDGMENT_Y));
    }

    /**
     * Distance = 69 is the last value that satisfies < 70 → hittable.
     */
    @Test
    void isHittable_distanceSixtyNine_returnsTrue() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y + 69
        double targetY = JUDGMENT_Y + 69 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertTrue(note.isHittable(JUDGMENT_Y),
                "Distance 69 must be hittable (< 70 tolerance)");
    }

    /**
     * Distance = 70 fails the strict < 70 check → not hittable.
     */
    @Test
    void isHittable_distanceExactlySeventyX_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y + 70  →  distance = 70, not < 70
        double targetY = JUDGMENT_Y + 70 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertFalse(note.isHittable(JUDGMENT_Y),
                "Distance exactly 70 must NOT be hittable (tolerance is strictly < 70)");
    }

    /**
     * Distance = 71 → not hittable.
     */
    @Test
    void isHittable_distanceSeventyOne_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        double targetY = JUDGMENT_Y + 71 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertFalse(note.isHittable(JUDGMENT_Y),
                "Distance 71 must not be hittable");
    }

    /**
     * Note far above the judgment line is not hittable.
     */
    @Test
    void isHittable_noteFarAboveJudgmentLine_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // Default Y = -50, bottom = 0; JUDGMENT_Y = 480 → distance = 480
        assertFalse(note.isHittable(JUDGMENT_Y),
                "A note near the top of the screen must not be hittable");
    }

    /**
     * Note well below the judgment line (bottom 200px past it) is not hittable.
     */
    @Test
    void isHittable_noteWellBelowJudgmentLine_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y + 200
        double targetY = JUDGMENT_Y + 200 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertFalse(note.isHittable(JUDGMENT_Y),
                "A note 200px below the judgment line must not be hittable");
    }

    /**
     * isHittable checks the BOTTOM edge (Y + size), not the top edge.
     * With top at judgmentY, bottom = judgmentY + size (distance = size = 50 < 70).
     * This confirms the bottom-edge formula is used.
     */
    @Test
    void isHittable_usesBottomEdge_notTopEdge() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // Place TOP at judgmentY → bottom = judgmentY + NOTE_SIZE = 480 + 50 = 530
        // distance = |530 - 480| = 50 < 70 → hittable
        double targetY = JUDGMENT_Y; // top Y = judgmentY
        note.update(targetY - (-NOTE_SIZE));
        assertTrue(note.isHittable(JUDGMENT_Y),
                "With top at judgmentY, bottom is NOTE_SIZE pixels below; distance=50 < 70");
    }

    /**
     * Symmetry: distance is computed with Math.abs, so bottom 69 pixels
     * ABOVE judgmentY is also hittable.
     */
    @Test
    void isHittable_bottomAboveJudgmentY_withinTolerance_returnsTrue() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y - 69  →  distance = 69 < 70
        double targetY = JUDGMENT_Y - 69 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertTrue(note.isHittable(JUDGMENT_Y),
                "Bottom 69px ABOVE judgmentY must also be hittable (abs distance)");
    }

    /**
     * Bottom 70 pixels above judgmentY is NOT hittable (abs distance = 70, not < 70).
     */
    @Test
    void isHittable_bottomAboveJudgmentY_atTolerance_returnsFalse() {
        Note note = new Note(null, 0, NOTE_SIZE, START_X);
        // bottom = JUDGMENT_Y - 70  →  distance = 70, not < 70
        double targetY = JUDGMENT_Y - 70 - NOTE_SIZE;
        note.update(targetY - (-NOTE_SIZE));
        assertFalse(note.isHittable(JUDGMENT_Y),
                "Bottom exactly 70px above judgmentY must NOT be hittable");
    }
}