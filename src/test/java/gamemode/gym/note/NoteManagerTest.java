package gamemode.gym.note;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NoteManager}.
 *
 * Key fixes vs. previous version:
 *
 *  1. SCREEN_HEIGHT raised to 1000.
 *     checkHit tests use judgmentY = 800.  A note hittable at Y=800 has its
 *     top at Y = 800 - NOTE_SIZE = 750.  With HEIGHT = 600 that note would
 *     already satisfy isOutOfScreen(600) (Y=750 > 600) and be culled by
 *     updateNotes() before checkHit() ever sees it.  Using HEIGHT = 1000
 *     keeps all hittable notes safely on-screen.
 *
 *  2. isOutOfScreen boundary test corrected.
 *     Note.isOutOfScreen uses strictly-greater-than (Y > height).
 *     The test that expected Y == height to be "off screen" was wrong;
 *     Y == height is still on-screen.  The boundary test now uses Y = height + 1.
 *
 *  3. moveNotesTo helper is correct as-is (targetTopY - (-NOTE_SIZE) gives the
 *     right delta) — no change needed there.
 */
class NoteManagerTest {

    // HEIGHT must be larger than any judgmentY used in checkHit tests (800)
    // plus NOTE_SIZE, so notes positioned for hitting are never culled first.
    private static final int HEIGHT    = 1000;
    private static final int NOTE_SIZE = 50;
    private static final int START_X   = 100;
    private static final int JUDGMENT_Y = 800;

    private Pane        root;
    private NoteManager manager;

    @BeforeEach
    void setUp() {
        root    = new Pane();
        manager = new NoteManager(root, HEIGHT);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void spawn(int lane) {
        manager.spawnNote(lane, null, NOTE_SIZE, START_X);
    }

    /**
     * Moves all currently-spawned notes so their TOP edge is at targetTopY.
     * Initial top Y = -NOTE_SIZE, so delta = targetTopY - (-NOTE_SIZE).
     */
    private void moveNotesTo(double targetTopY) {
        double delta = targetTopY - (-NOTE_SIZE);
        manager.updateNotes(delta);
    }

    // -----------------------------------------------------------------------
    // spawnNote
    // -----------------------------------------------------------------------

    @Test
    void spawnNote_addsNoteViewToPane() {
        spawn(0);
        assertEquals(1, root.getChildren().size(),
                "Spawning one note should add one ImageView to the pane");
    }

    @Test
    void spawnNote_multipleNotes_allAddedToPane() {
        spawn(0);
        spawn(1);
        spawn(0);
        assertEquals(3, root.getChildren().size());
    }

    // -----------------------------------------------------------------------
    // updateNotes — no penalty cases
    // -----------------------------------------------------------------------

    @Test
    void updateNotes_returnsZeroPenalty_whenNoNotesFallOff() {
        spawn(0);
        int penalty = manager.updateNotes(10);
        assertEquals(0, penalty,
                "No penalty when notes are still on screen");
    }

    @Test
    void updateNotes_returnsZero_whenNoNotesExist() {
        int penalty = manager.updateNotes(10);
        assertEquals(0, penalty);
    }

    // -----------------------------------------------------------------------
    // updateNotes — off-screen removal
    // -----------------------------------------------------------------------

    /**
     * isOutOfScreen is Y > HEIGHT (strictly greater).
     * Moving a note so Y = HEIGHT + 1 guarantees it is off-screen.
     */
    @Test
    void updateNotes_noteOffScreen_returnsPenaltyOfThirty() {
        spawn(0);
        // Move top Y to HEIGHT + 1  →  Y > HEIGHT  →  off screen
        int penalty = manager.updateNotes(HEIGHT + NOTE_SIZE + 1);
        assertEquals(30, penalty,
                "One off-screen note should produce a penalty of 30");
    }

    @Test
    void updateNotes_noteOffScreen_removesViewFromPane() {
        spawn(0);
        manager.updateNotes(HEIGHT + NOTE_SIZE + 1);
        assertEquals(0, root.getChildren().size(),
                "Off-screen note's ImageView must be removed from the pane");
    }

    /**
     * FIX: isOutOfScreen uses Y > height (strictly greater than).
     * Y == HEIGHT is NOT off-screen; only Y > HEIGHT is.
     */
    @Test
    void updateNotes_noteYEqualsHeight_isNotYetOffScreen() {
        spawn(0);
        // Move top Y to exactly HEIGHT  →  Y == HEIGHT  →  NOT off screen
        manager.updateNotes(HEIGHT + NOTE_SIZE); // startY=-50, delta moves to Y=HEIGHT
        assertEquals(0, manager.updateNotes(0),
                "Note with Y == HEIGHT is not yet off-screen (boundary is strictly >)");
        assertEquals(1, root.getChildren().size(),
                "Note at Y == HEIGHT should still be in the pane");
    }

    @Test
    void updateNotes_noteYOneAboveHeight_isOffScreen() {
        spawn(0);
        // Move top Y to HEIGHT + 1  →  Y > HEIGHT  →  off screen
        int penalty = manager.updateNotes(HEIGHT + NOTE_SIZE + 1);
        assertEquals(30, penalty, "Note at Y == HEIGHT + 1 must be off-screen");
        assertEquals(0, root.getChildren().size());
    }

    @Test
    void updateNotes_twoNotesOffScreen_returnsSixtyPenalty() {
        spawn(0);
        spawn(1);
        int penalty = manager.updateNotes(HEIGHT + NOTE_SIZE + 1);
        assertEquals(60, penalty,
                "Two off-screen notes should produce penalty 60 (30 each)");
    }

    @Test
    void updateNotes_twoNotesOffScreen_removesAllViews() {
        spawn(0);
        spawn(1);
        manager.updateNotes(HEIGHT + NOTE_SIZE + 1);
        assertEquals(0, root.getChildren().size());
    }

    /**
     * One note crosses the boundary this frame; the other stays on screen.
     */
    @Test
    void updateNotes_oneOffScreenOneOnScreen_returnsThirtyPenalty() {
        spawn(0);
        // Push lane-0 note to one pixel below the boundary
        manager.updateNotes(HEIGHT + NOTE_SIZE - 1); // Y = HEIGHT - 1, still on-screen

        spawn(1); // new note starts at Y = -NOTE_SIZE

        // Move both by 2: lane-0 goes to HEIGHT + 1 (off), lane-1 stays near top
        int penalty = manager.updateNotes(2);

        assertEquals(30, penalty,
                "Only the note that crossed HEIGHT should produce a penalty");
        assertEquals(1, root.getChildren().size(),
                "The on-screen note's view should still be in the pane");
    }

    @Test
    void updateNotes_removedNote_doesNotAccumulatePenaltyAgain() {
        spawn(0);
        manager.updateNotes(HEIGHT + NOTE_SIZE + 1); // removes note
        int secondPenalty = manager.updateNotes(10);
        assertEquals(0, secondPenalty);
    }

    // -----------------------------------------------------------------------
    // checkHit — miss (no matching note)
    // -----------------------------------------------------------------------

    @Test
    void checkHit_noNotes_returnsFalse() {
        assertFalse(manager.checkHit(0, JUDGMENT_Y));
    }

    @Test
    void checkHit_noteInWrongLane_returnsFalse() {
        spawn(1);
        // bottom = JUDGMENT_Y  →  top = JUDGMENT_Y - NOTE_SIZE (hittable distance 0)
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE);
        assertFalse(manager.checkHit(0, JUDGMENT_Y),
                "A note in lane 1 should not register as a hit for lane 0");
    }

    @Test
    void checkHit_noteInCorrectLane_butNotHittable_returnsFalse() {
        spawn(0);
        // Note stays at Y = -NOTE_SIZE (top of screen): bottom = 0, far from JUDGMENT_Y
        assertFalse(manager.checkHit(0, JUDGMENT_Y),
                "A note near the top of the screen should not be hittable");
    }

    // -----------------------------------------------------------------------
    // checkHit — hit
    // -----------------------------------------------------------------------

    @Test
    void checkHit_hittableNoteInCorrectLane_returnsTrue() {
        spawn(0);
        // bottom exactly at JUDGMENT_Y  →  distance = 0 < 70
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE);
        assertTrue(manager.checkHit(0, JUDGMENT_Y));
    }

    @Test
    void checkHit_hit_removesNoteFromPane() {
        spawn(0);
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE);
        manager.checkHit(0, JUDGMENT_Y);
        assertEquals(0, root.getChildren().size(),
                "Hit note's ImageView must be removed from the pane");
    }

    @Test
    void checkHit_hit_noteCanNoLongerBeHitAgain() {
        spawn(0);
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE);
        manager.checkHit(0, JUDGMENT_Y);
        assertFalse(manager.checkHit(0, JUDGMENT_Y),
                "The same note must not register a second hit after being removed");
    }

    /**
     * Only the first matching note in the lane is consumed per call.
     */
    @Test
    void checkHit_twoHittableNotesInSameLane_onlyFirstIsConsumed() {
        spawn(0);
        spawn(0);
        // moveNotesTo moves ALL current notes together
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE);

        assertTrue(manager.checkHit(0, JUDGMENT_Y),  "first hit should succeed");
        assertEquals(1, root.getChildren().size(),    "one note view should remain");
        assertTrue(manager.checkHit(0, JUDGMENT_Y),  "second hit should also succeed");
        assertEquals(0, root.getChildren().size(),    "no note views should remain");
    }

    /**
     * A hit in lane 0 must not consume a note in lane 1.
     */
    @Test
    void checkHit_hitInLane0_doesNotConsumeNoteInLane1() {
        spawn(0);
        spawn(1);
        moveNotesTo(JUDGMENT_Y - NOTE_SIZE); // both notes hittable

        manager.checkHit(0, JUDGMENT_Y); // hits lane 0

        assertEquals(1, root.getChildren().size(),
                "Lane 1 note must survive a lane 0 hit");
        assertTrue(manager.checkHit(1, JUDGMENT_Y),
                "Lane 1 note should still register as a hit");
    }

    /**
     * Distance = 69 is within the < 70 tolerance.
     */
    @Test
    void checkHit_noteJustWithinTolerance_returnsTrue() {
        spawn(0);
        // bottom = JUDGMENT_Y + 69  →  top = JUDGMENT_Y + 69 - NOTE_SIZE
        moveNotesTo(JUDGMENT_Y + 69 - NOTE_SIZE);
        assertTrue(manager.checkHit(0, JUDGMENT_Y),
                "Note with bottom 69px from judgmentY should be hittable");
    }

    /**
     * Distance = 70 is NOT within the < 70 tolerance (boundary is exclusive).
     */
    @Test
    void checkHit_noteAtExactTolerance_returnsFalse() {
        spawn(0);
        // bottom = JUDGMENT_Y + 70  →  distance = 70, not < 70
        moveNotesTo(JUDGMENT_Y + 70 - NOTE_SIZE);
        assertFalse(manager.checkHit(0, JUDGMENT_Y),
                "Note with bottom exactly 70px from judgmentY should NOT be hittable");
    }
}