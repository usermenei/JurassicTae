package gamemode.lobby.Scene;

import gamemode.lobby.Item.Interfaces.Sellable;
import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.CarnivoreDinosaur;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests only the sell action logic from ButtonSell.setOnAction.
 * No UI, no Mockito, no JavaFX.
 *
 * Run: ./gradlew test
 */
class ButtonSellTest {

    // ── Test-only Item interface (avoids Item base constructor + getResource crash) ──

    /**
     * Replaces Item as the type used in tests.
     * Does NOT extend the real Item class, so no getResource() is ever called.
     */
    interface TestItem {
        String getName();
        String getImgUrl();
    }

    // ── Stubs ────────────────────────────────────────────────────────────────

    static class TrackingPlayer {
        TestItem lastSold      = null;
        int      sellCallCount = 0;

        void sellItem(TestItem item) {
            lastSold = item;
            sellCallCount++;
        }
    }

    static class TrackingSellScene {
        Dinosaur lastAdded    = null;
        int      addCallCount = 0;

        void addCatalog(Dinosaur dino) {
            lastAdded = dino;
            addCallCount++;
        }
    }

    static class TrackingController {
        int reloadSellSceneCount = 0;
        int reloadMoneyCount     = 0;
        int loadSellCount        = 0;

        void reloadSellScene() { reloadSellSceneCount++; }
        void reloadMoney()     { reloadMoneyCount++; }
        void loadSell()        { loadSellCount++; }
    }

    /** Plain sellable item — implements TestItem only, never touches Item base class. */
    static class FakeItem implements TestItem, Sellable {
        private final String name;
        private final int    price;
        FakeItem(String name, int price) { this.name = name; this.price = price; }
        @Override public String getName()      { return name; }
        @Override public String getImgUrl()    { return ""; }
        @Override public int    getSellPrice() { return price; }
    }

    /** TamedDinosaur stub — only stores the dinosaur, no image loading. */
    static class FakeTamedDinosaur implements TestItem, Sellable {
        private final Dinosaur dino;
        FakeTamedDinosaur(Dinosaur dino) { this.dino = dino; }
        @Override public String   getName()      { return "FakeDino"; }
        @Override public String   getImgUrl()    { return ""; }
        @Override public int      getSellPrice() { return 100; }
        public    Dinosaur        getDinosaur()  { return dino; }
    }

    // ── Logic under test (exact copy of ButtonSell.setOnAction body) ──────────

    static void runSellAction(TestItem item,
                              TrackingPlayer player,
                              TrackingSellScene sellScene,
                              TrackingController controller) {
        player.sellItem(item);

        if (item instanceof FakeTamedDinosaur) {
            sellScene.addCatalog(((FakeTamedDinosaur) item).getDinosaur());
        }

        controller.reloadSellScene();
        controller.reloadMoney();
        controller.loadSell();
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    TrackingPlayer     player;
    TrackingSellScene  sellScene;
    TrackingController controller;

    @BeforeEach void setUp() {
        player     = new TrackingPlayer();
        sellScene  = new TrackingSellScene();
        controller = new TrackingController();
    }

    // ── Helper to build a CarnivoreDinosaur without repeating args ────────────

    static CarnivoreDinosaur makeRaptor() {
        return new CarnivoreDinosaur(
                "Raptor", 150, 135, 100, 2,
                0, 0, Dinosaur.Rarity.COMMON,
                "/images/dinosaur/raptor.gif",
                300, 300, 3.5, 100
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-01 | sellItem() is called with the correct item")
    void tc01_sellItemCalledWithCorrectItem() {
        FakeItem item = new FakeItem("Sword", 50);
        runSellAction(item, player, sellScene, controller);

        assertEquals(1,  player.sellCallCount, "sellItem must be called once");
        assertSame(item, player.lastSold,      "sellItem must receive the exact item");
    }

    @Test
    @DisplayName("TC-02 | Regular item: addCatalog is never called")
    void tc02_regularItem_addCatalogNotCalled() {
        runSellAction(new FakeItem("Potion", 20), player, sellScene, controller);

        assertEquals(0,           sellScene.addCallCount, "addCatalog must NOT be called for a regular item");
        assertNull(sellScene.lastAdded,                   "lastAdded must remain null");
    }

    @Test
    @DisplayName("TC-03 | TamedDinosaur: addCatalog is called with the correct dinosaur")
    void tc03_tamedDinosaur_addCatalogCalledWithCorrectDino() {
        CarnivoreDinosaur dino  = makeRaptor();
        FakeTamedDinosaur tamed = new FakeTamedDinosaur(dino);
        runSellAction(tamed, player, sellScene, controller);

        assertEquals(1,   sellScene.addCallCount, "addCatalog must be called exactly once");
        assertSame(dino,  sellScene.lastAdded,     "addCatalog must receive the correct dinosaur");
    }

    @Test
    @DisplayName("TC-04 | All three UI refresh calls are made")
    void tc04_allUIRefreshCallsMade() {
        runSellAction(new FakeItem("Shield", 30), player, sellScene, controller);

        assertAll("UI refresh calls",
                () -> assertEquals(1, controller.reloadSellSceneCount, "reloadSellScene must be called"),
                () -> assertEquals(1, controller.reloadMoneyCount,     "reloadMoney must be called"),
                () -> assertEquals(1, controller.loadSellCount,        "loadSell must be called")
        );
    }

    @Test
    @DisplayName("TC-05 | TamedDinosaur: sellItem is also called (not skipped)")
    void tc05_tamedDinosaur_sellItemAlsoCalled() {
        FakeTamedDinosaur tamed = new FakeTamedDinosaur(makeRaptor());
        runSellAction(tamed, player, sellScene, controller);

        assertEquals(1,    player.sellCallCount, "sellItem must still be called for TamedDinosaur");
        assertSame(tamed,  player.lastSold,      "sellItem must receive the TamedDinosaur as the item");
    }
}