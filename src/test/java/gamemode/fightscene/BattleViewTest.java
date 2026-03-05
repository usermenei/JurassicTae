package gamemode.fightscene;

import gamemode.forest.entity.CarnivoreDinosaur;
import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Player.Player;
import javafx.application.Platform;

import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for {@link BattleView}.
 *
 * <p>Because {@link BattleView} is a JavaFX {@link javafx.scene.layout.BorderPane},
 * the JavaFX toolkit must be initialised once before any test runs
 * ({@link #initJavaFX()}).</p>
 *
 * <p>The hard dependency on {@link gamemode.lobby.logic.GameController} (a singleton
 * with a private constructor) is removed by using the new {@link BattleCallback}
 * interface. Each test creates a lightweight {@link StubCallback} that simply records
 * which method was called — no singleton, no Mockito.</p>
 *
 * <h2>What is tested</h2>
 * <ul>
 *   <li>Constructor initialises without throwing.</li>
 *   <li>{@link BattleView#updateEnemyHp()} executes without throwing.</li>
 *   <li>{@link BattleView#updatePlayerHp(int)} executes without throwing.</li>
 *   <li>{@link BattleView#onEnemyDefeated()} calls {@link BattleCallback#onEnemyDefeated}.</li>
 *   <li>{@link BattleView#onEnemyCaught()} calls {@link BattleCallback#onEnemyCaught}.</li>
 *   <li>{@link BattleView#onEscape()} calls {@link BattleCallback#returnToWorld}.</li>
 *   <li>{@link BattleView#onPlayerDefeated()} calls {@link BattleCallback#returnToMain}.</li>
 * </ul>
 *
 * <p>Run with: {@code ./gradlew test}</p>
 */
class BattleViewTest {

    // ── Stubs ─────────────────────────────────────────────────────────────────

    /**
     * Minimal enemy for testing — a {@link CarnivoreDinosaur} with fixed stats.
     * Avoids any image-loading that could fail without a full JavaFX environment.
     */
    static class TestDinosaur extends CarnivoreDinosaur {
        TestDinosaur() {
            super(
                    "Raptor", 150, 135, 100, 2,
                    0, 0, Dinosaur.Rarity.COMMON,
                    "/images/dinosaur/raptor.gif",
                    300, 300, 3.5, 100
            );
        }
    }

    /**
     * Lightweight stub that implements {@link BattleCallback} by recording
     * which methods were invoked.
     *
     * <p>Each boolean field starts as {@code false} and is flipped to {@code true}
     * the moment the corresponding callback method is called. Tests assert on
     * these flags instead of depending on {@link gamemode.lobby.logic.GameController}.</p>
     */
    static class StubCallback implements BattleCallback {
        boolean defeatedCalled = false;
        boolean caughtCalled   = false;
        boolean worldCalled    = false;
        boolean mainCalled     = false;

        /** Stub player — always returns fixed HP / maxHp values. */
        final Player stubPlayer = new Player(0,0) {
            @Override public int getHp()    { return 100; }
            @Override public int getMaxHp() { return 100; }
        };

        @Override public Player getPlayer()                  { return stubPlayer; }
        @Override public void   onEnemyDefeated(Dinosaur e)  { defeatedCalled = true; }
        @Override public void   onEnemyCaught(Dinosaur e)    { caughtCalled   = true; }
        @Override public void   returnToWorld()               { worldCalled    = true; }
        @Override public void   returnToMain()                { mainCalled     = true; }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Initialises the JavaFX toolkit once before any test runs.
     * If the toolkit is already running (e.g. from another test class), the
     * {@link IllegalStateException} is silently swallowed.
     */
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // already initialised — safe to ignore
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Creates a fresh {@link BattleView} + {@link StubCallback} pair on the
     * JavaFX Application Thread and returns both. Used by every test to avoid
     * boilerplate.
     */
    record ViewAndStub(BattleView view, StubCallback stub) {}

    private ViewAndStub make() throws Exception {
        StubCallback stub = new StubCallback();
        TestDinosaur dino = new TestDinosaur();

        BattleView[] holder = new BattleView[1];
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            holder[0] = new BattleView(dino, stub);
            latch.countDown();
        });

        latch.await(); // wait until FX thread finishes

        return new ViewAndStub(holder[0], stub);
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    /**
     * TC-01: The {@link BattleCallback} constructor must not throw and must
     * return a non-null {@link BattleView}.
     */
    @Test
    @DisplayName("TC-01 | BattleView initialises without errors")
    void tc01_initializesWithoutErrors() throws Exception {
        ViewAndStub vs = make();
        assertNotNull(vs.view(), "BattleView must not be null after construction");
    }

    /**
     * TC-02: {@link BattleView#updateEnemyHp()} must not throw any exception.
     */
    @Test
    @DisplayName("TC-02 | updateEnemyHp() executes without errors")
    void tc02_updateEnemyHpNoThrow() throws Exception {
        ViewAndStub vs = make();
        assertDoesNotThrow(vs.view()::updateEnemyHp);
    }

    /**
     * TC-03: {@link BattleView#updatePlayerHp(int)} must not throw for any valid HP value.
     */
    @Test
    @DisplayName("TC-03 | updatePlayerHp() executes without errors")
    void tc03_updatePlayerHpNoThrow() throws Exception {
        ViewAndStub vs = make();
        assertDoesNotThrow(() -> vs.view().updatePlayerHp(50));
    }

    /**
     * TC-04: {@link BattleView#onEnemyDefeated()} must invoke
     * {@link BattleCallback#onEnemyDefeated(Dinosaur)}.
     */
    @Test
    @DisplayName("TC-04 | onEnemyDefeated() notifies callback")
    void tc04_onEnemyDefeatedNotifiesCallback() throws Exception {
        ViewAndStub vs = make();
        vs.view().onEnemyDefeated();
        assertTrue(vs.stub().defeatedCalled, "defeatedCalled must be true");
    }

    /**
     * TC-05: {@link BattleView#onEnemyCaught()} must invoke
     * {@link BattleCallback#onEnemyCaught(Dinosaur)}.
     */
    @Test
    @DisplayName("TC-05 | onEnemyCaught() notifies callback")
    void tc05_onEnemyCaughtNotifiesCallback() throws Exception {
        ViewAndStub vs = make();
        vs.view().onEnemyCaught();
        assertTrue(vs.stub().caughtCalled, "caughtCalled must be true");
    }

    /**
     * TC-06: {@link BattleView#onEscape()} must invoke
     * {@link BattleCallback#returnToWorld()}.
     */
    @Test
    @DisplayName("TC-06 | onEscape() calls returnToWorld on callback")
    void tc06_onEscapeCallsReturnToWorld() throws Exception {
        ViewAndStub vs = make();
        vs.view().onEscape();
        assertTrue(vs.stub().worldCalled, "worldCalled must be true");
    }

    /**
     * TC-07: {@link BattleView#onPlayerDefeated()} must invoke
     * {@link BattleCallback#returnToMain()}.
     */
    @Test
    @DisplayName("TC-07 | onPlayerDefeated() calls returnToMain on callback")
    void tc07_onPlayerDefeatedCallsReturnToMain() throws Exception {
        ViewAndStub vs = make();
        vs.view().onPlayerDefeated();
        assertTrue(vs.stub().mainCalled, "mainCalled must be true");
    }
}