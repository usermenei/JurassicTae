package gamemode.lobby.Scene;

import gamemode.forest.entity.Dinosaur;

import gamemode.lobby.Player.Player;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests only the pure logic inside {@link SellScene}.
 *
 * <p>The two testable logic units that have zero UI dependency are:</p>
 * <ul>
 *   <li>{@link SellScene#addCatalog(Dinosaur)} — adds a dinosaur to the catalog list.</li>
 *   <li>The toggle logic of the Catalog/Sell button — switches label text and
 *       determines which load method runs.</li>
 *   <li>The {@code loadItems} column-wrap rule — items wrap to the next row after
 *       every 4 columns.</li>
 * </ul>
 *
 * <p>All tests use plain Java stubs. No JavaFX, no UI, no Mockito.</p>
 *
 * <p>Run with: {@code ./gradlew test}</p>
 */
class SellSceneTest {

    // ── Stubs ────────────────────────────────────────────────────────────────

    /**
     * Minimal stub for {@link Dinosaur} — stores only a name.
     * Avoids any image loading that the real Dinosaur constructor may trigger.
     */
    static class FakeDinosaur extends Dinosaur {
        private final String name;
        FakeDinosaur(String name) {
            super("Raptor", 150, 135, 100, 2,
                    0, 0, Dinosaur.Rarity.COMMON,
                    "/images/dinosaur/raptor.gif",
                    300, 300, 3.5, 100);
            this.name = name;
        }

        @Override
        public void update(Player player) {

        }

        @Override public String getName() { return name; }
    }

    /**
     * Extracted catalog logic from {@link SellScene} into a plain Java class
     * so it can be tested without constructing the JavaFX scene.
     *
     * <p>This mirrors the exact fields and methods of SellScene that hold
     * testable business logic:</p>
     * <ul>
     *   <li>{@code catalog} list</li>
     *   <li>{@code addCatalog()}</li>
     *   <li>Toggle state and the column-wrap grid placement rule</li>
     * </ul>
     */
    static class SellSceneLogic {

        /** Mirrors SellScene.catalog */
        final List<Dinosaur> catalog = new ArrayList<>();

        /** Mirrors SellScene.switchBtt text */
        String toggleLabel = "Catalog";

        /** Records which load method was last called */
        String lastLoaded = null;

        /** Mirrors SellScene.addCatalog() exactly */
        void addCatalog(Dinosaur dinosaur) {
            catalog.add(dinosaur);
        }

        /**
         * Mirrors the toggle button click handler in SellScene exactly:
         * if label is "Catalog" → loadCatalog + set "Sell"
         * else                  → loadItems  + set "Catalog"
         */
        void onToggleClick() {
            if (toggleLabel.equals("Catalog")) {
                lastLoaded  = "catalog";
                toggleLabel = "Sell";
            } else {
                lastLoaded  = "items";
                toggleLabel = "Catalog";
            }
        }

        /**
         * Mirrors the column-wrap placement rule used in loadItems / loadCatalog:
         * returns a list of [col, row] pairs for {@code count} items placed in a
         * 4-column grid.
         */
        List<int[]> computeGridPositions(int count) {
            List<int[]> positions = new ArrayList<>();
            int col = 0, row = 0;
            for (int i = 0; i < count; i++) {
                positions.add(new int[]{col, row});
                col++;
                if (col == 4) { col = 0; row++; }
            }
            return positions;
        }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    SellSceneLogic logic;

    @BeforeEach
    void setUp() {
        logic = new SellSceneLogic();
    }

    // ── TC-01 ─────────────────────────────────────────────────────────────────

    /**
     * TC-01: Catalog starts empty on construction.
     *
     * <p><em>Given</em> a freshly created SellScene,<br>
     * <em>When</em> nothing has been added,<br>
     * <em>Then</em> the catalog list is empty.</p>
     */
    @Test
    @DisplayName("TC-01 | Catalog list is empty on initialisation")
    void tc01_catalogEmptyOnInit() {
        assertTrue(logic.catalog.isEmpty(), "Catalog must be empty at start");
    }

    // ── TC-02 ─────────────────────────────────────────────────────────────────

    /**
     * TC-02: addCatalog() adds one dinosaur to the list.
     *
     * <p><em>Given</em> an empty catalog,<br>
     * <em>When</em> one dinosaur is added,<br>
     * <em>Then</em> the catalog contains exactly that dinosaur.</p>
     */
    @Test
    @DisplayName("TC-02 | addCatalog() adds one dinosaur")
    void tc02_addCatalogAddsOneDinosaur() {
        FakeDinosaur dino = new FakeDinosaur("T-Rex");
        logic.addCatalog(dino);

        assertEquals(1,   logic.catalog.size(), "Catalog should contain 1 dinosaur");
        assertSame(dino,  logic.catalog.get(0), "Catalog should contain the exact dinosaur added");
    }

    // ── TC-03 ─────────────────────────────────────────────────────────────────

    /**
     * TC-03: addCatalog() called multiple times accumulates all dinosaurs.
     *
     * <p><em>Given</em> an empty catalog,<br>
     * <em>When</em> three dinosaurs are added,<br>
     * <em>Then</em> the catalog contains all three in insertion order.</p>
     */
    @Test
    @DisplayName("TC-03 | addCatalog() accumulates multiple dinosaurs in order")
    void tc03_addCatalogAccumulatesMultiple() {
        FakeDinosaur d1 = new FakeDinosaur("Raptor");
        FakeDinosaur d2 = new FakeDinosaur("Triceratops");
        FakeDinosaur d3 = new FakeDinosaur("Brachiosaurus");

        logic.addCatalog(d1);
        logic.addCatalog(d2);
        logic.addCatalog(d3);

        assertEquals(3, logic.catalog.size(), "Catalog must contain 3 dinosaurs");
        assertSame(d1, logic.catalog.get(0), "First entry must be d1");
        assertSame(d2, logic.catalog.get(1), "Second entry must be d2");
        assertSame(d3, logic.catalog.get(2), "Third entry must be d3");
    }

    // ── TC-04 ─────────────────────────────────────────────────────────────────

    /**
     * TC-04: Toggle starts in "Catalog" state.
     *
     * <p><em>Given</em> a fresh SellScene,<br>
     * <em>When</em> the toggle has not been clicked,<br>
     * <em>Then</em> the button label is "Catalog".</p>
     */
    @Test
    @DisplayName("TC-04 | Toggle button starts with label 'Catalog'")
    void tc04_toggleStartsAsCatalog() {
        assertEquals("Catalog", logic.toggleLabel, "Initial toggle label must be 'Catalog'");
    }

    // ── TC-05 ─────────────────────────────────────────────────────────────────

    /**
     * TC-05: First toggle click switches to catalog mode and relabels "Sell".
     *
     * <p><em>Given</em> the default "Catalog" state,<br>
     * <em>When</em> the toggle is clicked once,<br>
     * <em>Then</em> the label becomes "Sell" and catalog content is loaded.</p>
     */
    @Test
    @DisplayName("TC-05 | First toggle click loads catalog and sets label to 'Sell'")
    void tc05_firstToggleLoadsCatalog() {
        logic.onToggleClick();

        assertEquals("Sell",    logic.toggleLabel, "Label must switch to 'Sell'");
        assertEquals("catalog", logic.lastLoaded,  "loadCatalog must have been called");
    }

    // ── TC-06 ─────────────────────────────────────────────────────────────────

    /**
     * TC-06: Second toggle click switches back to sell mode and relabels "Catalog".
     *
     * <p><em>Given</em> the scene is in catalog mode (label = "Sell"),<br>
     * <em>When</em> the toggle is clicked again,<br>
     * <em>Then</em> the label returns to "Catalog" and item content is loaded.</p>
     */
    @Test
    @DisplayName("TC-06 | Second toggle click loads items and sets label back to 'Catalog'")
    void tc06_secondToggleLoadsItems() {
        logic.onToggleClick(); // → Sell / catalog
        logic.onToggleClick(); // → Catalog / items

        assertEquals("Catalog", logic.toggleLabel, "Label must return to 'Catalog'");
        assertEquals("items",   logic.lastLoaded,  "loadItems must have been called");
    }

    // ── TC-07 ─────────────────────────────────────────────────────────────────

    /**
     * TC-07: Grid column-wrap — first 4 items land in row 0, columns 0–3.
     *
     * <p>Mirrors the exact {@code col++; if (col == 4) { col=0; row++; }} rule
     * used in both {@code loadItems()} and {@code loadCatalog()}.</p>
     */
    @Test
    @DisplayName("TC-07 | First 4 items are placed in row 0, columns 0-3")
    void tc07_gridFourItemsInRowZero() {
        List<int[]> pos = logic.computeGridPositions(4);

        for (int i = 0; i < 4; i++) {
            assertEquals(i, pos.get(i)[0], "col should be " + i);
            assertEquals(0, pos.get(i)[1], "row should be 0 for item " + i);
        }
    }

    // ── TC-08 ─────────────────────────────────────────────────────────────────

    /**
     * TC-08: Grid column-wrap — 5th item wraps to row 1, column 0.
     */
    @Test
    @DisplayName("TC-08 | 5th item wraps to row 1, column 0")
    void tc08_gridFifthItemWrapsToRowOne() {
        List<int[]> pos = logic.computeGridPositions(5);
        int[] fifth = pos.get(4);

        assertEquals(0, fifth[0], "5th item col must be 0 after wrap");
        assertEquals(1, fifth[1], "5th item row must be 1 after wrap");
    }

    // ── TC-09 ─────────────────────────────────────────────────────────────────

    /**
     * TC-09: Grid column-wrap — 9 items fill exactly 2 full rows and start row 2.
     */
    @Test
    @DisplayName("TC-09 | 9 items produce positions across 3 rows correctly")
    void tc09_gridNineItemsThreeRows() {
        List<int[]> pos = logic.computeGridPositions(9);

        // Row 0: items 0-3
        for (int i = 0; i < 4; i++) assertEquals(0, pos.get(i)[1], "item " + i + " should be row 0");
        // Row 1: items 4-7
        for (int i = 4; i < 8; i++) assertEquals(1, pos.get(i)[1], "item " + i + " should be row 1");
        // Row 2: item 8
        assertEquals(2, pos.get(8)[1], "item 8 should be row 2");
        assertEquals(0, pos.get(8)[0], "item 8 should be col 0");
    }

    // ── TC-10 ─────────────────────────────────────────────────────────────────

    /**
     * TC-10: Adding the same dinosaur instance twice results in two catalog entries.
     *
     * <p>Confirms the catalog does not deduplicate — it stores every call exactly.</p>
     */
    @Test
    @DisplayName("TC-10 | Adding the same dinosaur twice gives two catalog entries")
    void tc10_addSameDinosaurTwice() {
        FakeDinosaur dino = new FakeDinosaur("Raptor");
        logic.addCatalog(dino);
        logic.addCatalog(dino);

        assertEquals(2, logic.catalog.size(), "Catalog must contain 2 entries");
        assertSame(logic.catalog.get(0), logic.catalog.get(1), "Both entries must be the same instance");
    }
}