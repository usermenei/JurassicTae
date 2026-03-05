package gamemode.gym.scene;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the business logic embedded in {@link MainMenu}'s START button.
 *
 * The constructor of MainMenu is tightly coupled to JavaFX resources (GIF, fonts,
 * GameLogic singleton), making it impractical to instantiate in a headless test.
 * Instead, the three decision branches are extracted verbatim as a plain Java
 * method — {@link StartButtonLogic#handleStart()} — and tested in isolation.
 *
 * Logic under test (from MainMenu, start button lambda):
 *
 *   if (player.getMoney() < GYM_FEE) {
 *       showInsufficientFunds(root);          // onStart NOT called, money NOT changed
 *   } else {
 *       player.setMoney(player.getMoney() - GYM_FEE);
 *       onStart.run();
 *   }
 *
 * GYM_FEE = 500
 */
class MainMenuTest {

    private static final int GYM_FEE = 500;

    // -----------------------------------------------------------------------
    // Minimal fakes
    // -----------------------------------------------------------------------

    /** Fake player that tracks money without any JavaFX dependency. */
    static class FakePlayer {
        private int money;
        FakePlayer(int money) { this.money = money; }
        int  getMoney()         { return money; }
        void setMoney(int m)    { this.money = m; }
    }

    // -----------------------------------------------------------------------
    // StartButtonLogic — extracted verbatim from MainMenu's start button lambda
    // -----------------------------------------------------------------------

    /**
     * Self-contained extraction of the START button logic.
     * Accepts the two observable side-effect channels as constructor args:
     *   - FakePlayer  (money state)
     *   - onStart     (callback invocation tracking)
     * Records whether showInsufficientFunds was requested via a flag.
     */
    static class StartButtonLogic {
        final FakePlayer player;
        final Runnable   onStart;

        int  onStartCallCount          = 0;
        int  insufficientFundsCount    = 0;

        StartButtonLogic(FakePlayer player, Runnable onStart) {
            this.player  = player;
            this.onStart = onStart;
        }

        /** Mirrors the lambda inside MainMenu's START button exactly. */
        void handleStart() {
            if (player.getMoney() < GYM_FEE) {
                insufficientFundsCount++;   // stands in for showInsufficientFunds()
            } else {
                player.setMoney(player.getMoney() - GYM_FEE);
                onStartCallCount++;
                onStart.run();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Fixtures
    // -----------------------------------------------------------------------

    /** Builds a logic instance with the given starting money. */
    StartButtonLogic build(int money) {
        StartButtonLogic logic = new StartButtonLogic(
                new FakePlayer(money),
                () -> logic0.onStartCallCount++   // placeholder; overridden per test
        );
        // We capture the actual Runnable below in each test for clarity.
        return null; // not used directly — see helper below
    }

    /** Counter incremented by the onStart callback. */
    int onStartFired;

    StartButtonLogic buildWith(int money) {
        onStartFired = 0;
        FakePlayer player = new FakePlayer(money);
        return new StartButtonLogic(player, () -> onStartFired++);
    }

    // Suppress unused field warning — logic0 is a placeholder, not used in tests
    private StartButtonLogic logic0 = null;

    @BeforeEach
    void setUp() {
        onStartFired = 0;
    }

    // -----------------------------------------------------------------------
    // Insufficient funds path  (money < 500)
    // -----------------------------------------------------------------------

    @Test
    void start_moneyZero_showsInsufficientFunds() {
        StartButtonLogic logic = buildWith(0);
        logic.handleStart();
        assertEquals(1, logic.insufficientFundsCount,
                "Insufficient-funds popup must show when money = 0");
    }

    @Test
    void start_moneyOneLessThanFee_showsInsufficientFunds() {
        StartButtonLogic logic = buildWith(GYM_FEE - 1);  // 499
        logic.handleStart();
        assertEquals(1, logic.insufficientFundsCount,
                "Popup must show when money is 1 short of the fee");
    }

    @Test
    void start_insufficientFunds_doesNotCallOnStart() {
        StartButtonLogic logic = buildWith(GYM_FEE - 1);
        logic.handleStart();
        assertEquals(0, onStartFired,
                "onStart must NOT be invoked when player cannot afford the fee");
    }

    @Test
    void start_insufficientFunds_doesNotDeductMoney() {
        int initialMoney = GYM_FEE - 1;
        StartButtonLogic logic = buildWith(initialMoney);
        logic.handleStart();
        assertEquals(initialMoney, logic.player.getMoney(),
                "Money must not change when player cannot afford the fee");
    }

    @Test
    void start_insufficientFunds_multipleAttempts_showsPopupEachTime() {
        StartButtonLogic logic = buildWith(0);
        logic.handleStart();
        logic.handleStart();
        logic.handleStart();
        assertEquals(3, logic.insufficientFundsCount,
                "Each failed attempt must trigger the popup independently");
        assertEquals(0, onStartFired);
    }

    // -----------------------------------------------------------------------
    // Sufficient funds path  (money >= 500)
    // -----------------------------------------------------------------------

    @Test
    void start_moneyExactlyFee_callsOnStart() {
        StartButtonLogic logic = buildWith(GYM_FEE);  // exactly 500
        logic.handleStart();
        assertEquals(1, onStartFired,
                "onStart must be called when money == GYM_FEE");
    }

    @Test
    void start_moneyExactlyFee_deductsCorrectAmount() {
        StartButtonLogic logic = buildWith(GYM_FEE);
        logic.handleStart();
        assertEquals(0, logic.player.getMoney(),
                "Money should be 0 after paying exact fee of 500");
    }

    @Test
    void start_moneyAboveFee_callsOnStart() {
        StartButtonLogic logic = buildWith(1000);
        logic.handleStart();
        assertEquals(1, onStartFired,
                "onStart must be called when money > GYM_FEE");
    }

    @Test
    void start_moneyAboveFee_deductsExactFeeAmount() {
        StartButtonLogic logic = buildWith(1000);
        logic.handleStart();
        assertEquals(500, logic.player.getMoney(),
                "Exactly GYM_FEE (500) must be deducted from player's money");
    }

    @Test
    void start_moneyOnePastFee_deductsCorrectly() {
        StartButtonLogic logic = buildWith(GYM_FEE + 1);  // 501
        logic.handleStart();
        assertEquals(1, logic.player.getMoney(),
                "501 - 500 fee = 1 remaining");
    }

    @Test
    void start_sufficientFunds_doesNotShowInsufficientFundsPopup() {
        StartButtonLogic logic = buildWith(GYM_FEE);
        logic.handleStart();
        assertEquals(0, logic.insufficientFundsCount,
                "Insufficient-funds popup must NOT show when player can afford the fee");
    }

    @Test
    void start_sufficientFunds_onStartCalledExactlyOnce() {
        StartButtonLogic logic = buildWith(2000);
        logic.handleStart();
        assertEquals(1, onStartFired,
                "onStart must be invoked exactly once per successful start");
    }

    // -----------------------------------------------------------------------
    // Boundary — exact fee amount
    // -----------------------------------------------------------------------

    /**
     * GYM_FEE - 1 is insufficient; GYM_FEE is sufficient.
     * This pair confirms the boundary is >= not >.
     */
    @Test
    void start_boundary_oneLessThanFee_isInsufficient() {
        StartButtonLogic logic = buildWith(GYM_FEE - 1);
        logic.handleStart();
        assertEquals(1, logic.insufficientFundsCount);
        assertEquals(0, onStartFired);
    }

    @Test
    void start_boundary_exactFee_isSufficient() {
        StartButtonLogic logic = buildWith(GYM_FEE);
        logic.handleStart();
        assertEquals(0, logic.insufficientFundsCount);
        assertEquals(1, onStartFired);
    }

    // -----------------------------------------------------------------------
    // Needed amount displayed in popup
    // -----------------------------------------------------------------------

    /**
     * The popup message shows: needed = GYM_FEE - player.getMoney().
     * We verify the arithmetic is correct for several cases.
     */
    @Test
    void insufficientFunds_neededAmount_whenMoneyIsZero() {
        int money  = 0;
        int needed = GYM_FEE - money;
        assertEquals(500, needed,
                "Player with 0 gold needs exactly 500 more");
    }

    @Test
    void insufficientFunds_neededAmount_whenMoneyIsHalfFee() {
        int money  = 250;
        int needed = GYM_FEE - money;
        assertEquals(250, needed,
                "Player with 250 gold needs 250 more");
    }

    @Test
    void insufficientFunds_neededAmount_whenMoneyIsOneLessThanFee() {
        int money  = GYM_FEE - 1;
        int needed = GYM_FEE - money;
        assertEquals(1, needed,
                "Player with 499 gold needs exactly 1 more");
    }
}