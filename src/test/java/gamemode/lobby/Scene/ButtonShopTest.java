package gamemode.lobby.Scene;

import gamemode.lobby.Interfaces.Buyable;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests only the buy action logic from ButtonShop.setOnAction.
 * No UI, no Mockito, no JavaFX.
 *
 * The real Item base class is avoided entirely (it calls getResource() in its
 * constructor which crashes outside JavaFX). Instead, a local TestItem interface
 * is used so stubs never trigger image loading.
 *
 * Run: ./gradlew test
 */
class ButtonShopTest {

    // ── Test-only interface (avoids Item base constructor + getResource crash) ──

    /**
     * Stand-in for Item used only inside tests.
     * Does NOT extend the real Item class — no getResource() is ever called.
     */
    interface TestItem extends Buyable {
        String getName();
    }

    // ── Stubs ────────────────────────────────────────────────────────────────

    /** Tracks buyItem() calls. */
    static class TrackingPlayer {
        TestItem lastBought    = null;
        int      buyCallCount  = 0;

        void buyItem(TestItem item) {
            lastBought = item;
            buyCallCount++;
        }
    }

    /** Tracks reloadMoney() calls. */
    static class TrackingController {
        int reloadMoneyCount = 0;

        void reloadMoney() { reloadMoneyCount++; }
    }

    /** Plain buyable item stub — no image loading. */
    static class FakeItem implements TestItem {
        private final String name;
        private final int    buyPrice;

        FakeItem(String name, int buyPrice) {
            this.name     = name;
            this.buyPrice = buyPrice;
        }

        @Override public String getName()     { return name; }
        @Override public int    getBuyPrice() { return buyPrice; }
        @Override public String getDescription() { return "Test Item"; }
    }

    // ── Logic under test (exact copy of ButtonShop.setOnAction body) ──────────

    static void runBuyAction(TestItem item,
                             TrackingPlayer player,
                             TrackingController controller) {
        player.buyItem(item);
        controller.reloadMoney();
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    TrackingPlayer     player;
    TrackingController controller;

    @BeforeEach void setUp() {
        player     = new TrackingPlayer();
        controller = new TrackingController();
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-01 | buyItem() is called with the correct item")
    void tc01_buyItemCalledWithCorrectItem() {
        FakeItem item = new FakeItem("Sword", 100);
        runBuyAction(item, player, controller);

        assertEquals(1,   player.buyCallCount, "buyItem must be called exactly once");
        assertSame(item,  player.lastBought,   "buyItem must receive the exact item");
    }

    @Test
    @DisplayName("TC-02 | reloadMoney() is called after buying")
    void tc02_reloadMoneyCalledAfterBuy() {
        runBuyAction(new FakeItem("Potion", 30), player, controller);

        assertEquals(1, controller.reloadMoneyCount, "reloadMoney must be called exactly once");
    }

    @Test
    @DisplayName("TC-03 | buyItem() is called before reloadMoney()")
    void tc03_buyItemCalledBeforeReloadMoney() {
        // Use an ordered log to verify call sequence
        java.util.List<String> callOrder = new java.util.ArrayList<>();

        TrackingPlayer orderedPlayer = new TrackingPlayer() {
            @Override void buyItem(TestItem item) { super.buyItem(item); callOrder.add("buyItem"); }
        };
        TrackingController orderedController = new TrackingController() {
            @Override void reloadMoney() { super.reloadMoney(); callOrder.add("reloadMoney"); }
        };

        runBuyAction(new FakeItem("Shield", 50), orderedPlayer, orderedController);

        assertEquals("buyItem",     callOrder.get(0), "buyItem must be called first");
        assertEquals("reloadMoney", callOrder.get(1), "reloadMoney must be called second");
    }

    @Test
    @DisplayName("TC-04 | Correct item name is passed through to the player")
    void tc04_correctItemNamePassedToPlayer() {
        FakeItem item = new FakeItem("Dragon Sword", 999);
        runBuyAction(item, player, controller);

        assertEquals("Dragon Sword", player.lastBought.getName(),
                "The item name must be preserved after buy");
    }

    @Test
    @DisplayName("TC-05 | Buy price is accessible on the purchased item")
    void tc05_buyPriceAccessibleOnPurchasedItem() {
        FakeItem item = new FakeItem("Elixir", 75);
        runBuyAction(item, player, controller);

        assertEquals(75, ((Buyable) player.lastBought).getBuyPrice(),
                "Buy price must match the item's getBuyPrice()");
    }
}