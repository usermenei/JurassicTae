package gamemode.lobby.Player;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.Weapon;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit test suite for the {@link Player} class.
 *
 * <p>
 * This test class validates:
 * <ul>
 *     <li>Initialization defaults</li>
 *     <li>Movement and boundary constraints</li>
 *     <li>Collision detection logic</li>
 *     <li>Inventory management</li>
 *     <li>Buying and selling mechanics</li>
 *     <li>Potion effects</li>
 *     <li>Experience and leveling system</li>
 * </ul>
 *
 * <p>
 * JavaFX toolkit is initialized once for all tests because
 * the Player class depends on JavaFX components.
 * </p>
 */
class PlayerTest {

    private Player player;

    /**
     * Initializes JavaFX Toolkit before any tests run.
     * Required because Player internally loads JavaFX resources.
     */
    @BeforeAll
    static void initToolkit() {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized – safe to ignore
        }
    }

    /**
     * Creates a fresh Player instance before each test.
     */
    @BeforeEach
    void setUp() {
        player = new Player(100, 100);
    }

    /**
     * Concrete test weapon implementation used for
     * buy/sell/inventory tests.
     */
    static class TestWeapon extends Weapon implements Buyable, Sellable {

        private int sellPrice;

        public TestWeapon(String name, int buyPrice, int sellPrice) {
            super(name, "", buyPrice, 10);
            this.sellPrice = sellPrice;
        }

        @Override
        public int getSellPrice() {
            return sellPrice;
        }

        @Override
        public int getBuyPrice() {
            return super.getBuyPrice();
        }
    }

    /**
     * Concrete test potion implementation
     * used to simulate potion effects.
     */
    static class TestPotion extends Potion {

        public TestPotion(String name) {
            super(name, "", 0, 10);
        }
    }

    // ==========================================================
    // Initialization Tests
    // ==========================================================

    /**
     * Verifies default Player values after construction.
     */
    @Test
    @DisplayName("Player initializes with correct default values")
    void testInitializationDefaults() {
        assertEquals("Tae", player.getName());
        assertEquals(1000, player.getMoney());
        assertEquals(200, player.getHp());
        assertEquals(200, player.getMaxHp());
        assertEquals(1, player.getLevel());
        assertEquals(0, player.getExp());
    }

    // ==========================================================
    // Movement Tests
    // ==========================================================

    /**
     * Ensures moving right increases or maintains X position.
     */
    @Test
    void testMoveRight() {
        double initialX = player.getX();
        player.move(1, 0);
        assertTrue(player.getX() >= initialX);
    }

    /**
     * Ensures player cannot move beyond left boundary.
     */
    @Test
    void testBoundaryLeft() {
        player.setX(-500);
        player.move(0, 0);
        assertTrue(player.getX() >= 0);
    }

    /**
     * Ensures player cannot move beyond upper boundary.
     */
    @Test
    void testBoundaryTop() {
        player.setY(-500);
        player.move(0, 0);
        assertTrue(player.getY() >= 90);
    }

    // ==========================================================
    // Collision Tests
    // ==========================================================

    /**
     * Verifies collision detection returns true when
     * overlapping with itself.
     */
    @Test
    void testCollisionTrue() {
        assertTrue(player.intersects(
                player.getX(),
                player.getY(),
                player.getHeight(),
                player.getWidth()
        ));
    }

    /**
     * Verifies collision detection returns false
     * for distant objects.
     */
    @Test
    void testCollisionFalse() {
        assertFalse(player.intersects(9999, 9999, 10, 10));
    }

    // ==========================================================
    // Inventory Tests
    // ==========================================================

    /**
     * Tests adding and removing items from inventory.
     */
    @Test
    void testAddRemoveItem() {
        Item weapon = new TestWeapon("Sword", 100, 50);

        player.addItem(weapon);
        assertEquals(1, player.getInventory().size());

        player.removeItem(weapon);
        assertEquals(0, player.getInventory().size());
    }

    /**
     * Ensures inventory does not exceed its maximum capacity.
     */
    @Test
    void testInventoryLimit() {
        for (int i = 0; i < player.getInventorylimit(); i++) {
            player.addItem(new TestWeapon("W" + i, 10, 5));
        }

        player.buyItem(new TestWeapon("Extra", 10, 5));

        assertEquals(player.getInventorylimit(), player.getInventory().size());
    }

    // ==========================================================
    // Buy / Sell Tests
    // ==========================================================

    /**
     * Verifies successful item purchase reduces money
     * and adds item to inventory.
     */
    @Test
    void testBuyItemSuccess() {
        TestWeapon weapon = new TestWeapon("Sword", 100, 50);
        int initialMoney = player.getMoney();

        player.buyItem(weapon);

        assertTrue(player.getMoney() < initialMoney);
        assertTrue(player.getInventory().contains(weapon));
    }

    /**
     * Ensures purchase fails when player has insufficient money.
     */
    @Test
    void testBuyItemFailNotEnoughMoney() {
        player.setMoney(0);
        TestWeapon weapon = new TestWeapon("Sword", 100, 50);

        player.buyItem(weapon);

        assertFalse(player.getInventory().contains(weapon));
    }

    /**
     * Ensures duplicate weapons cannot be purchased.
     */
    @Test
    void testBuyDuplicateWeaponFails() {
        TestWeapon weapon = new TestWeapon("Sword", 100, 50);

        player.buyItem(weapon);
        player.buyItem(weapon);

        assertEquals(1, player.getInventory().size());
    }

    /**
     * Verifies selling an item increases money
     * and removes item from inventory.
     */
    @Test
    void testSellItemSuccess() {
        TestWeapon weapon = new TestWeapon("Sword", 100, 50);
        player.addItem(weapon);

        int initialMoney = player.getMoney();
        player.sellItem(weapon);

        assertTrue(player.getMoney() > initialMoney);
        assertFalse(player.getInventory().contains(weapon));
    }

    // ==========================================================
    // Potion Tests
    // ==========================================================

    /**
     * Ensures Heal Potion restores HP.
     */
    @Test
    void testHealPotion() {
        player.setHp(100);
        TestPotion heal = new TestPotion("Heal Potion");

        player.addItem(heal);
        player.usePotion(heal);

        assertTrue(player.getHp() > 100);
    }

    /**
     * Ensures EXP Potion activates EXP boost logic.
     */
    @Test
    void testExpPotion() {
        TestPotion expPotion = new TestPotion("Exp Potion");

        player.addItem(expPotion);
        player.usePotion(expPotion);

        player.addExp(50);

        // If EXP boost doubles gain to 100 and level-up threshold is 100,
        // remaining EXP should be 0 after leveling up.
        assertEquals(0, player.getExp());
    }

    /**
     * Ensures Speed Potion increases movement speed.
     */
    @Test
    void testSpeedPotion() {
        double originalSpeed = player.getSpeed();

        TestPotion speedPotion = new TestPotion("Speed Potion");
        player.addItem(speedPotion);
        player.usePotion(speedPotion);

        assertTrue(player.getSpeed() > originalSpeed);
    }

    // ==========================================================
    // Leveling Tests
    // ==========================================================

    /**
     * Verifies leveling up increases level and restores HP.
     */
    @Test
    void testLevelUp() {
        int initialLevel = player.getLevel();

        player.addExp(200);

        assertTrue(player.getLevel() > initialLevel);
        assertEquals(player.getMaxHp(), player.getHp());
    }
}