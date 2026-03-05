package gamemode.gym.note;

import gamemode.gym.system.ComboManager;
import gamemode.gym.system.GameEffect;
import gamemode.gym.system.ScoreManager;
import gamemode.gym.ui.GameUI;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HitController} using plain Java fakes (no Mockito).
 *
 * The key design choice: FakeNoteManager.checkHit() is controlled by a flag
 * so we can independently test the hit and miss branches without needing
 * real Note objects on screen.
 */
class HitControllerTest {

    // -----------------------------------------------------------------------
    // Fakes
    // -----------------------------------------------------------------------

    static class FakeGameUI extends GameUI {
        int showJudgmentCount = 0;
        int animateComboCount = 0;
        private final Pane pane = new Pane();
        private final javafx.scene.Scene fakeScene =
                new javafx.scene.Scene(new Pane(), 800, 600);

        FakeGameUI() {
            super(800, 600, 64, 100, blankImage(), () -> {});
        }

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

        @Override public void showJudgmentHit()           { showJudgmentCount++; }
        @Override public void animateCombo()              { animateComboCount++; }
        @Override public void fadeJudgmentLine()          {}
        @Override public Pane getGameLayer()              { return pane; }
        @Override public javafx.scene.Scene getScene()   { return fakeScene; }
    }

    /**
     * Fake NoteManager whose checkHit() returns a configurable value.
     * Also records the lane and judgmentY it was called with.
     */
    static class FakeNoteManager extends NoteManager {
        boolean hitResult    = false;
        int     checkedLane  = -1;
        double  checkedJudgY = -1;
        int     checkCallCount = 0;

        FakeNoteManager() {
            super(new Pane(), 600);
        }

        @Override
        public boolean checkHit(int lane, double judgmentY) {
            checkedLane    = lane;
            checkedJudgY   = judgmentY;
            checkCallCount++;
            return hitResult;
        }
    }

    /**
     * Fake ComboManager — records addCombo calls and exposes a controllable multiplier.
     */
    static class FakeComboManager extends ComboManager {
        int    addComboCount = 0;
        double multiplierToReturn = 1.0;

        FakeComboManager() {
            super(new Text(), new Text());
        }

        @Override public void   addCombo()          { addComboCount++; }
        @Override public double getMultiplier()     { return multiplierToReturn; }
        @Override public void   resetCombo()        {}
    }

    /**
     * Fake ScoreManager — records addScore calls with the exact arguments used.
     */
    static class FakeScoreManager extends ScoreManager {
        int    addCallCount  = 0;
        int    lastBase      = -1;
        double lastMultiplier = -1;

        FakeScoreManager() {
            super(new Text());
        }

        @Override
        public void addScore(int baseAmount, double multiplier) {
            addCallCount++;
            lastBase       = baseAmount;
            lastMultiplier = multiplier;
        }

        @Override public void subtractScore(int amount) {}
    }

    /**
     * Fake GameEffect — records playRandomNoLane calls.
     */
    static class FakeGameEffect extends GameEffect {
        int playCount = 0;

        FakeGameEffect() {
            super("/gamemode/gym/hit.gif", 150, 3);
        }

        @Override
        public void playRandomNoLane(
                Pane layer,
                double sceneWidth, double sceneHeight,
                double laneStartX, double laneWidth
        ) {
            playCount++;
        }
    }

    // -----------------------------------------------------------------------
    // Fixtures
    // -----------------------------------------------------------------------

    FakeGameUI        fakeUI;
    FakeNoteManager   fakeNoteManager;
    FakeComboManager  fakeCombo;
    FakeScoreManager  fakeScore;
    FakeGameEffect    fakeEffect;

    HitController buildController() {
        return new HitController(
                fakeUI, fakeNoteManager, fakeCombo, fakeScore, fakeEffect,
                /* noteSize */ 64, /* startX */ 100
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
    // Tests — always-on behaviour (fires regardless of hit/miss)
    // -----------------------------------------------------------------------

    /**
     * showJudgmentHit() must be called on every key press, hit or miss.
     */
    @Test
    void handleKeyPress_alwaysShowsJudgmentHit_onHit() {
        fakeNoteManager.hitResult = true;
        buildController().handleKeyPress(0);
        assertEquals(1, fakeUI.showJudgmentCount,
                "showJudgmentHit must fire on a successful hit");
    }

    @Test
    void handleKeyPress_alwaysShowsJudgmentHit_onMiss() {
        fakeNoteManager.hitResult = false;
        buildController().handleKeyPress(0);
        assertEquals(1, fakeUI.showJudgmentCount,
                "showJudgmentHit must fire even when the player misses");
    }

    /**
     * checkHit() must always be called, for both lanes, passing judgmentY = 800.
     */
    @Test
    void handleKeyPress_alwaysCallsCheckHit_lane0() {
        buildController().handleKeyPress(0);
        assertEquals(1,   fakeNoteManager.checkCallCount);
        assertEquals(0,   fakeNoteManager.checkedLane);
        assertEquals(800, fakeNoteManager.checkedJudgY, 0.001);
    }

    @Test
    void handleKeyPress_alwaysCallsCheckHit_lane1() {
        buildController().handleKeyPress(1);
        assertEquals(1,   fakeNoteManager.checkCallCount);
        assertEquals(1,   fakeNoteManager.checkedLane);
        assertEquals(800, fakeNoteManager.checkedJudgY, 0.001);
    }

    // -----------------------------------------------------------------------
    // Tests — miss path (checkHit returns false)
    // -----------------------------------------------------------------------

    /**
     * On a miss, none of the hit-only actions should fire.
     */
    @Test
    void handleKeyPress_miss_doesNotAddCombo() {
        fakeNoteManager.hitResult = false;
        buildController().handleKeyPress(0);
        assertEquals(0, fakeCombo.addComboCount, "combo must not increment on a miss");
    }

    @Test
    void handleKeyPress_miss_doesNotAddScore() {
        fakeNoteManager.hitResult = false;
        buildController().handleKeyPress(0);
        assertEquals(0, fakeScore.addCallCount, "score must not increase on a miss");
    }

    @Test
    void handleKeyPress_miss_doesNotAnimateCombo() {
        fakeNoteManager.hitResult = false;
        buildController().handleKeyPress(0);
        assertEquals(0, fakeUI.animateComboCount, "combo animation must not play on a miss");
    }

    @Test
    void handleKeyPress_miss_doesNotPlayHitEffect() {
        fakeNoteManager.hitResult = false;
        buildController().handleKeyPress(0);
        assertEquals(0, fakeEffect.playCount, "hit effect must not play on a miss");
    }

    // -----------------------------------------------------------------------
    // Tests — hit path (checkHit returns true)
    // -----------------------------------------------------------------------

    /**
     * On a hit, addCombo() must be called exactly once.
     */
    @Test
    void handleKeyPress_hit_incrementsCombo() {
        fakeNoteManager.hitResult = true;
        buildController().handleKeyPress(0);
        assertEquals(1, fakeCombo.addComboCount, "combo must increment on a hit");
    }

    /**
     * On a hit, addScore() must be called with base 100 and the current multiplier.
     */
    @Test
    void handleKeyPress_hit_addsScoreWithBaseHundred() {
        fakeNoteManager.hitResult        = true;
        fakeCombo.multiplierToReturn     = 1.0;
        buildController().handleKeyPress(0);

        assertEquals(1,   fakeScore.addCallCount);
        assertEquals(100, fakeScore.lastBase,       "base score must be 100");
    }

    /**
     * The multiplier forwarded to addScore must come from comboManager.getMultiplier().
     */
    @Test
    void handleKeyPress_hit_forwardsMultiplierFromComboManager() {
        fakeNoteManager.hitResult    = true;
        fakeCombo.multiplierToReturn = 2.5;
        buildController().handleKeyPress(0);

        assertEquals(2.5, fakeScore.lastMultiplier, 0.001,
                "addScore must receive the multiplier returned by comboManager");
    }

    /**
     * On a hit, animateCombo() must be called exactly once.
     */
    @Test
    void handleKeyPress_hit_animatesCombo() {
        fakeNoteManager.hitResult = true;
        buildController().handleKeyPress(0);
        assertEquals(1, fakeUI.animateComboCount, "combo animation must play on a hit");
    }

    /**
     * On a hit, the hit effect must play exactly once.
     */
    @Test
    void handleKeyPress_hit_playsHitEffect() {
        fakeNoteManager.hitResult = true;
        buildController().handleKeyPress(0);
        assertEquals(1, fakeEffect.playCount, "hit effect must play on a hit");
    }

    /**
     * Multiple consecutive hits must accumulate combo and score calls correctly.
     */
    @Test
    void handleKeyPress_multipleHits_accumulatesCorrectly() {
        fakeNoteManager.hitResult = true;
        HitController controller  = buildController();

        controller.handleKeyPress(0);
        controller.handleKeyPress(1);
        controller.handleKeyPress(0);

        assertEquals(3, fakeCombo.addComboCount);
        assertEquals(3, fakeScore.addCallCount);
        assertEquals(3, fakeEffect.playCount);
        assertEquals(3, fakeUI.showJudgmentCount);
    }

    /**
     * Interleaved hits and misses must only increment hit-path counters for hits.
     */
    @Test
    void handleKeyPress_mixedHitsAndMisses_onlyHitsIncrementCounters() {
        HitController controller = buildController();

        fakeNoteManager.hitResult = true;
        controller.handleKeyPress(0);   // hit

        fakeNoteManager.hitResult = false;
        controller.handleKeyPress(1);   // miss

        fakeNoteManager.hitResult = true;
        controller.handleKeyPress(0);   // hit

        // showJudgmentHit fires on every press
        assertEquals(3, fakeUI.showJudgmentCount);

        // Hit-only actions should only have fired twice
        assertEquals(2, fakeCombo.addComboCount);
        assertEquals(2, fakeScore.addCallCount);
        assertEquals(2, fakeEffect.playCount);
        assertEquals(2, fakeUI.animateComboCount);
    }
}