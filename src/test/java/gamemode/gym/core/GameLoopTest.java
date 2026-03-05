package gamemode.gym.core;

import gamemode.gym.system.GameEffect;
import gamemode.gym.ui.GameUI;
import gamemode.gym.note.NoteManager;
import gamemode.gym.system.ComboManager;
import gamemode.gym.system.ScoreManager;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GameLoop} using plain Java fakes (no Mockito).
 *
 * Each dependency is subclassed with a minimal fake that records calls
 * and returns controlled values. GameLoop.handle() is driven directly.
 *
 * Fixes vs. the original test:
 *  1. FakeNoteManager  — updateNotes(double) not updateNotes(int): real param is double.
 *  2. FakeScoreManager — super(Text) required; pass a real Text node.
 *  3. FakeComboManager — super(Text, Text) required; pass real Text nodes.
 *  4. FakeGameEffect   — playRandomNoLane uses double laneStartX/laneWidth, not int.
 *  5. FakeGameUI       — super(int,int,int,int,Image,Runnable) required; pass sentinel
 *                        values and a blank in-memory PNG so no file I/O is needed.
 */
class GameLoopTest {

    // -----------------------------------------------------------------------
    // Fakes
    // -----------------------------------------------------------------------

    /**
     * Fake GameUI.
     * Satisfies the mandatory super constructor with sentinel dimensions and a
     * 1×1 blank PNG, then overrides every method GameLoop actually calls so no
     * live JavaFX rendering is triggered.
     */
    static class FakeGameUI extends GameUI {
        int fadeCount = 0;
        private final Pane  pane   = new Pane();
        private final javafx.scene.Scene fakeScene =
                new javafx.scene.Scene(new Pane(), 800, 600);

        FakeGameUI() {
            super(800, 600, 64, 100, blankImage(), () -> {});
        }

        /** Builds a minimal valid 1×1 transparent PNG entirely in memory. */
        private static Image blankImage() {
            byte[] png = {
                    (byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,
                    0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,
                    0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,
                    0x08,0x02,0x00,0x00,0x00,(byte)0x90,0x77,0x53,(byte)0xDE,
                    0x00,0x00,0x00,0x0C,0x49,0x44,0x41,0x54,
                    0x08,(byte)0xD7,0x63,(byte)0xF8,(byte)0xCF,(byte)0xC0,0x00,0x00,
                    0x00,0x02,0x00,0x01,(byte)0xE2,0x21,(byte)0xBC,0x33,
                    0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44,(byte)0xAE,0x42,0x60,(byte)0x82
            };
            return new Image(new java.io.ByteArrayInputStream(png));
        }

        @Override public void fadeJudgmentLine()       { fadeCount++; }
        @Override public Pane getGameLayer()           { return pane; }
        @Override public javafx.scene.Scene getScene() { return fakeScene; }
    }

    /**
     * Fake NoteManager.
     * FIX: updateNotes signature is {@code double speed} in the real class, not {@code int}.
     */
    static class FakeNoteManager extends NoteManager {
        int penaltyToReturn = 0;
        int updateCallCount = 0;
        // Each entry: [lane, noteSize, startX]
        java.util.List<int[]> spawnCalls = new java.util.ArrayList<>();

        FakeNoteManager() {
            super(new Pane(), 600);
        }

        @Override
        public int updateNotes(double speed) {   // double — matches real source
            updateCallCount++;
            return penaltyToReturn;
        }

        @Override
        public void spawnNote(int lane, Image img, int noteSize, int startX) {
            spawnCalls.add(new int[]{lane, noteSize, startX});
        }
    }

    /**
     * Fake ComboManager.
     * FIX: must call super(Text, Text) — ComboManager has no no-arg constructor.
     */
    static class FakeComboManager extends ComboManager {
        int resetCount = 0;

        FakeComboManager() {
            super(new Text(), new Text());
        }

        @Override
        public void resetCombo() {
            resetCount++;
        }
    }

    /**
     * Fake ScoreManager.
     * FIX: must call super(Text) — ScoreManager has no no-arg constructor.
     * We override subtractScore so the live Text node is never mutated.
     */
    static class FakeScoreManager extends ScoreManager {
        int totalSubtracted   = 0;
        int subtractCallCount = 0;

        FakeScoreManager() {
            super(new Text());
        }

        @Override
        public void subtractScore(int amount) {
            subtractCallCount++;
            totalSubtracted += amount;
            // do NOT call super — avoids touching the Text node on a non-FX thread
        }
    }

    /**
     * Fake GameEffect.
     * FIX: playRandomNoLane parameters laneStartX and laneWidth are {@code double},
     * not {@code int}, matching the real GameEffect source.
     * The super constructor attempts to load an image; if the resource is absent
     * the Image is broken but never used since we override the only called method.
     */
    static class FakeGameEffect extends GameEffect {
        int playCount = 0;

        FakeGameEffect() {
            super("/gamemode/gym/miss.gif", 150, 3);
        }

        @Override
        public void playRandomNoLane(
                Pane layer,
                double sceneWidth,
                double sceneHeight,
                double laneStartX,   // double — matches real source
                double laneWidth     // double — matches real source
        ) {
            playCount++;
        }
    }

    // -----------------------------------------------------------------------
    // Test fixtures
    // -----------------------------------------------------------------------

    FakeGameUI        fakeUI;
    FakeNoteManager   fakeNoteManager;
    FakeComboManager  fakeCombo;
    FakeScoreManager  fakeScore;
    FakeGameEffect    fakeEffect;

    GameLoop buildLoop() {
        return new GameLoop(
                fakeUI, fakeNoteManager, fakeCombo, fakeScore, fakeEffect,
                /* noteSize */ 64, /* startX */ 100,
                /* red */ null, /* blue */ null
        );
    }

    @BeforeEach
    void setUp() {
        fakeUI          = new FakeGameUI();
        fakeNoteManager = new FakeNoteManager();
        fakeCombo       = new FakeComboManager();
        fakeScore       = new FakeScoreManager();
        fakeEffect      = new FakeGameEffect();
    }

    // -----------------------------------------------------------------------
    // Tests
    // -----------------------------------------------------------------------

    /**
     * handle() must call fadeJudgmentLine() on every frame regardless of
     * whether notes are missed or spawned.
     */
    @Test
    void handle_alwaysCallsFadeJudgmentLine() {
        GameLoop loop = buildLoop();
        long now = 1_000_000_000L;

        loop.handle(now);
        loop.handle(now + 100_000_000L);
        loop.handle(now + 200_000_000L);

        assertEquals(3, fakeUI.fadeCount,
                "fadeJudgmentLine should be called once per handle() invocation");
    }

    /**
     * handle() must call updateNotes() on every frame.
     */
    @Test
    void handle_alwaysCallsUpdateNotes() {
        GameLoop loop = buildLoop();
        long now = 1_000_000_000L;

        loop.handle(now);
        loop.handle(now + 100_000_000L);

        assertEquals(2, fakeNoteManager.updateCallCount,
                "updateNotes should be called once per handle() invocation");
    }

    /**
     * When updateNotes returns 0 (no missed note), the miss path must not fire.
     */
    @Test
    void handle_noPenalty_doesNotTriggerMissLogic() {
        fakeNoteManager.penaltyToReturn = 0;
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        assertEquals(0, fakeCombo.resetCount,         "combo should not reset");
        assertEquals(0, fakeScore.subtractCallCount,  "score should not be subtracted");
        assertEquals(0, fakeEffect.playCount,          "miss effect should not play");
    }

    /**
     * When updateNotes returns a positive penalty, all three miss-handling
     * steps must fire: combo reset, score subtract, and miss effect.
     */
    @Test
    void handle_withPenalty_triggersAllMissLogic() {
        fakeNoteManager.penaltyToReturn = 10;
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        assertEquals(1,  fakeCombo.resetCount,        "combo should be reset once");
        assertEquals(1,  fakeScore.subtractCallCount, "score should be subtracted once");
        assertEquals(10, fakeScore.totalSubtracted,   "subtracted amount must equal penalty");
        assertEquals(1,  fakeEffect.playCount,         "miss effect should play once");
    }

    /**
     * Score subtracted must exactly equal the penalty value returned by updateNotes.
     */
    @Test
    void handle_subtractsExactPenaltyFromScore() {
        fakeNoteManager.penaltyToReturn = 25;
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        assertEquals(25, fakeScore.totalSubtracted);
    }

    /**
     * Multiple miss frames accumulate penalties across all counters correctly.
     */
    @Test
    void handle_multipleMissFrames_accumulatesPenalties() {
        fakeNoteManager.penaltyToReturn = 10;
        GameLoop loop = buildLoop();
        long base = 1_000_000_000L;

        // Keep timestamps within 500 ms so no additional note spawn fires.
        loop.handle(base);
        loop.handle(base + 100_000_000L);
        loop.handle(base + 200_000_000L);

        assertEquals(3,  fakeCombo.resetCount);
        assertEquals(3,  fakeScore.subtractCallCount);
        assertEquals(30, fakeScore.totalSubtracted);
        assertEquals(3,  fakeEffect.playCount);
    }

    /**
     * A note must be spawned on the very first handle() call because
     * lastSpawn starts at 0 and (now - 0) is always > 500 ms.
     */
    @Test
    void handle_firstCall_spawnsANote() {
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        assertEquals(1, fakeNoteManager.spawnCalls.size(),
                "Exactly one note should be spawned on the first frame");
    }

    /**
     * Two calls within the 500 ms window must only spawn one note total.
     */
    @Test
    void spawnLogic_withinInterval_doesNotSpawnSecondNote() {
        GameLoop loop = buildLoop();
        long base = 1_000_000_000L;

        loop.handle(base);                 // spawns
        loop.handle(base + 100_000_000L); // 100 ms later — must not spawn again

        assertEquals(1, fakeNoteManager.spawnCalls.size(),
                "Second call within 500 ms should NOT spawn another note");
    }

    /**
     * A call just past the 500 ms interval must trigger a second spawn.
     */
    @Test
    void spawnLogic_afterInterval_spawnsNewNote() {
        GameLoop loop = buildLoop();
        long base = 1_000_000_000L;

        loop.handle(base);                 // first spawn
        loop.handle(base + 500_000_001L); // just past 500 ms → second spawn

        assertEquals(2, fakeNoteManager.spawnCalls.size(),
                "Call after 500 ms interval should spawn a second note");
    }

    /**
     * The noteSize and startX forwarded to spawnNote must match the values
     * supplied to the GameLoop constructor.
     */
    @Test
    void spawnLogic_passesCorrectNoteSizeAndStartX() {
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        int[] call = fakeNoteManager.spawnCalls.get(0);
        assertEquals(64,  call[1], "noteSize should be 64");
        assertEquals(100, call[2], "startX should be 100");
    }

    /**
     * The spawned lane must always be 0 or 1.
     */
    @Test
    void spawnLogic_laneIsZeroOrOne() {
        GameLoop loop = buildLoop();
        long base = 1_000_000_000L;

        for (int i = 0; i < 20; i++) {
            loop.handle(base + (long) i * 600_000_000L);
        }

        for (int[] call : fakeNoteManager.spawnCalls) {
            int lane = call[0];
            assertTrue(lane == 0 || lane == 1,
                    "Lane must be 0 or 1, got: " + lane);
        }
    }

    /**
     * Spawn logic and miss logic are independent: a frame that triggers both
     * should execute both paths without interference.
     */
    @Test
    void handle_spawnAndMissOnSameFrame_bothPathsExecute() {
        fakeNoteManager.penaltyToReturn = 5;
        GameLoop loop = buildLoop();

        loop.handle(1_000_000_000L);

        assertEquals(1, fakeNoteManager.spawnCalls.size(), "note should be spawned");
        assertEquals(1, fakeCombo.resetCount,               "combo should reset");
        assertEquals(5, fakeScore.totalSubtracted,          "score should be subtracted");
        assertEquals(1, fakeEffect.playCount,                "miss effect should play");
    }
}