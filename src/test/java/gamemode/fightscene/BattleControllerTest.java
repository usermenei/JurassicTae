package gamemode.fightscene;

import gamemode.forest.entity.Dinosaur;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests only the pure battle logic from {@link BattleController}.
 *
 * <p>Because {@code BattleController} is tightly coupled to JavaFX ({@link javafx.scene.control.Button}
 * handlers, {@link javafx.animation.PauseTransition}), its constructor cannot be called
 * in unit tests. Instead, the three logical units that contain pure, testable decisions
 * are extracted into a plain Java helper class ({@link BattleLogic}) that mirrors
 * the exact conditions from the real class.</p>
 *
 * <h2>Logical units tested</h2>
 * <ul>
 *   <li><b>Fight</b> — damage application, enemy-dead detection, EXP award.</li>
 *   <li><b>Catch</b> — DinoBall requirement, 10 % HP threshold, inventory mutation,
 *       rarity message selection.</li>
 *   <li><b>Enemy turn</b> — damage to player, player-dead detection, HP reset on defeat.</li>
 *   <li><b>Guard flags</b> — {@code battleEnded} and {@code playerTurn} block actions.</li>
 * </ul>
 *
 * <p>No JavaFX, no Mockito, no UI. Run with: {@code ./gradlew test}</p>
 */
class BattleControllerTest {

    // ── Stubs ─────────────────────────────────────────────────────────────────

    /**
     * Minimal enemy stub. Tracks HP, maxHp, strength, expDrop, name, and rarity.
     * Mirrors only the fields accessed by {@link BattleController}.
     */
    static class FakeEnemy {
        String name;
        int    hp, maxHp, strength, expDrop;
        Dinosaur.Rarity rarity;

        FakeEnemy(String name, int hp, int strength, int expDrop, Dinosaur.Rarity rarity) {
            this.name = name; this.hp = this.maxHp = hp;
            this.strength = strength; this.expDrop = expDrop; this.rarity = rarity;
        }

        void  takeDamage(int d) { hp = Math.max(0, hp - d); }
        int   getHp()           { return hp; }
        int   getMaxHp()        { return maxHp; }
        int   getStrength()     { return strength; }
        int   getExpDrop()      { return expDrop; }
        String getName()        { return name; }
        Dinosaur.Rarity getRarity() { return rarity; }
    }

    /**
     * Minimal player stub. Tracks HP, maxHp, strength, exp, and a simple inventory.
     */
    static class FakePlayer {
        int hp, maxHp, strength, exp;
        List<Object> inventory = new ArrayList<>();

        FakePlayer(int hp, int strength) { this.hp = this.maxHp = hp; this.strength = strength; }

        void   addItem(Object item)    { inventory.add(item); }
        void   removeItem(Object item) { inventory.remove(item); }
        void   addExp(int e)           { exp += e; }
        void   setHp(int v)            { hp = v; }
        int    getHp()                 { return hp; }
        int    getMaxHp()              { return maxHp; }
        int    getStrength()           { return strength; }
        boolean hasItemOfType(Class<?> c) {
            return inventory.stream().anyMatch(c::isInstance);
        }
        Object firstOfType(Class<?> c) {
            return inventory.stream().filter(c::isInstance).findFirst().orElse(null);
        }
    }

    /** Stub DinoBall — just a type marker. */
    static class FakeDinoBall {}

    /** Stub TamedDinosaur — wraps a FakeEnemy. */
    static class FakeTamed { final FakeEnemy dino; FakeTamed(FakeEnemy d) { dino = d; } }

    /**
     * Extracted battle logic from {@link BattleController} as a plain Java class.
     * Mirrors all three combat methods ({@code fight}, {@code catchDinosaur},
     * {@code enemyTurn}) and the two guard flags.
     *
     * <p>Each method body is a direct line-for-line copy of the logic inside
     * {@code BattleController}, adapted only to use the local stubs instead of
     * real JavaFX / game objects.</p>
     */
    static class BattleLogic {

        FakePlayer player;
        FakeEnemy  enemy;
        boolean    playerTurn  = true;
        boolean    battleEnded = false;

        /** Records the last status message set (mirrors {@code commandBox.setMessage}). */
        String lastMessage = null;

        /** Records the last event fired to the view. */
        String lastViewEvent = null;

        BattleLogic(FakePlayer player, FakeEnemy enemy) {
            this.player = player;
            this.enemy  = enemy;
        }

        // ── Fight ─────────────────────────────────────────────────────────

        /**
         * Mirrors the Fight button handler from {@link BattleController}.
         *
         * @param damage damage to deal (weapon damage or player strength)
         * @param weaponName display name of the weapon (or "Bare Hand")
         */
        void fight(int damage, String weaponName) {
            if (battleEnded || !playerTurn) return;

            playerTurn = false;
            enemy.takeDamage(damage);
            lastMessage = "P'Tae attacked with " + weaponName + " (-" + damage + " HP)";

            if (enemy.getHp() <= 0) {
                battleEnded = true;
                int expGained = enemy.getExpDrop();
                player.addExp(expGained);
                lastMessage   = enemy.getName() + " defeated! +" + expGained + " EXP";
                lastViewEvent = "onEnemyDefeated";
                return;
            }

            // Would normally schedule enemyTurn via PauseTransition — call directly in tests
            enemyTurn();
        }

        // ── Catch ─────────────────────────────────────────────────────────

        /**
         * Mirrors {@code BattleController.catchDinosaur()}.
         * Uses {@link FakeDinoBall} and {@link FakeTamed} as inventory items.
         */
        void catchDinosaur() {
            if (battleEnded || !playerTurn) return;

            playerTurn = false;

            FakeDinoBall ball = (FakeDinoBall) player.firstOfType(FakeDinoBall.class);
            if (ball == null) {
                lastMessage = "No DinoBall available!";
                playerTurn  = true;
                return;
            }

            double hpPercent = (double) enemy.getHp() / enemy.getMaxHp();
            if (hpPercent > 0.10) {
                lastMessage = "HP must be below 10% to catch!";
                playerTurn  = true;
                return;
            }

            player.removeItem(ball);
            player.addItem(new FakeTamed(enemy));

            lastMessage = switch (enemy.getRarity()) {
                case COMMON   -> enemy.getName() + " was caught!";
                case UNCOMMON -> "Nice! You captured an uncommon " + enemy.getName() + "!";
                case RARE     -> "INCREDIBLE! A rare " + enemy.getName() + " has been captured!";
            };

            battleEnded   = true;
            lastViewEvent = "onEnemyCaught";
        }

        // ── Enemy turn ────────────────────────────────────────────────────

        /**
         * Mirrors {@code BattleController.enemyTurn()}.
         */
        void enemyTurn() {
            if (battleEnded) return;

            int damage = enemy.getStrength();
            player.setHp(player.getHp() - damage);
            lastMessage = enemy.getName() + " attacked P'Tae (-" + damage + " HP)";

            if (player.getHp() <= 0) {
                battleEnded = true;
                player.setHp(player.getMaxHp());
                lastMessage   = "You were defeated... Returning to lobby.";
                lastViewEvent = "onPlayerDefeated";
                return;
            }

            playerTurn = true;
        }

        // ── Escape ────────────────────────────────────────────────────────

        /**
         * Mirrors the Escape button handler.
         */
        void escape() {
            if (battleEnded) return;
            battleEnded   = true;
            lastViewEvent = "onEscape";
        }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    BattleLogic logic;
    FakePlayer  player;
    FakeEnemy   enemy;

    @BeforeEach
    void setUp() {
        player = new FakePlayer(100, 10);
        enemy  = new FakeEnemy("Raptor", 80, 15, 50, Dinosaur.Rarity.COMMON);
        logic  = new BattleLogic(player, enemy);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // FIGHT TESTS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * TC-01: Fighting with a weapon deals the weapon's damage to the enemy.
     *
     * <p><em>Given</em> an enemy with 80 HP,<br>
     * <em>When</em> the player fights with a 30-damage weapon,<br>
     * <em>Then</em> the enemy HP drops to 50.</p>
     */
    @Test
    @DisplayName("TC-01 | Fight with weapon deals correct damage to enemy")
    void tc01_fightDealsWeaponDamage() {
        logic.fight(30, "Sword");

        assertEquals(50, enemy.getHp(), "Enemy HP should be 80 - 30 = 50");
    }

    /**
     * TC-02: Fighting with bare hands uses player strength as damage.
     *
     * <p><em>Given</em> a player with strength 10,<br>
     * <em>When</em> the player fights bare-handed,<br>
     * <em>Then</em> enemy HP drops by 10.</p>
     */
    @Test
    @DisplayName("TC-02 | Fight bare-handed uses player strength as damage")
    void tc02_fightBareHandUsesStrength() {
        logic.fight(player.getStrength(), "Bare Hand");

        assertEquals(70, enemy.getHp(), "Enemy HP should be 80 - 10 = 70");
    }

    /**
     * TC-03: Killing the enemy awards EXP to the player.
     *
     * <p><em>Given</em> an enemy with 80 HP and 50 expDrop,<br>
     * <em>When</em> the player deals 80+ damage,<br>
     * <em>Then</em> the player gains 50 EXP and the battle ends.</p>
     */
    @Test
    @DisplayName("TC-03 | Killing enemy awards EXP and sets battleEnded")
    void tc03_killEnemyAwardsExp() {
        logic.fight(80, "Sword");

        assertAll(
                () -> assertEquals(50, player.exp,    "Player should gain 50 EXP"),
                () -> assertTrue(logic.battleEnded,    "battleEnded must be true"),
                () -> assertEquals("onEnemyDefeated", logic.lastViewEvent)
        );
    }

    /**
     * TC-04: After a fight where the enemy survives, it is the enemy's turn
     * and the player's turn flag is set back to {@code true} afterward.
     */
    @Test
    @DisplayName("TC-04 | After fight (enemy survives), enemy attacks and turn returns")
    void tc04_afterFightEnemyCounterAttacks() {
        logic.fight(10, "Sword"); // enemy survives at 70 HP

        // Enemy (strength 15) attacked player
        assertEquals(85, player.getHp(), "Player HP should be 100 - 15 = 85");
        assertTrue(logic.playerTurn, "playerTurn must be restored to true");
    }

    /**
     * TC-05: Fight is ignored when {@code battleEnded} is true.
     */
    @Test
    @DisplayName("TC-05 | Fight is ignored when battleEnded = true")
    void tc05_fightIgnoredWhenBattleEnded() {
        logic.battleEnded = true;
        logic.fight(80, "Sword");

        assertEquals(80, enemy.getHp(), "Enemy HP must not change after battle ended");
    }

    /**
     * TC-06: Fight is ignored when it is not the player's turn.
     */
    @Test
    @DisplayName("TC-06 | Fight is ignored when playerTurn = false")
    void tc06_fightIgnoredWhenNotPlayerTurn() {
        logic.playerTurn = false;
        logic.fight(80, "Sword");

        assertEquals(80, enemy.getHp(), "Enemy HP must not change on enemy's turn");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CATCH TESTS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * TC-07: Catch fails with a message when no DinoBall is in inventory.
     */
    @Test
    @DisplayName("TC-07 | Catch fails when player has no DinoBall")
    void tc07_catchFailsNoDinoBall() {
        // no DinoBall added
        logic.catchDinosaur();

        assertEquals("No DinoBall available!", logic.lastMessage);
        assertTrue(logic.playerTurn, "Turn must be returned to player");
        assertFalse(logic.battleEnded, "Battle must not end");
    }

    /**
     * TC-08: Catch fails with a message when enemy HP is above 10 %.
     */
    @Test
    @DisplayName("TC-08 | Catch fails when enemy HP > 10%")
    void tc08_catchFailsHpTooHigh() {
        player.addItem(new FakeDinoBall());
        enemy.hp = 50; // 50/80 = 62.5% — above threshold

        logic.catchDinosaur();

        assertEquals("HP must be below 10% to catch!", logic.lastMessage);
        assertTrue(logic.playerTurn, "Turn must be returned to player");
    }

    /**
     * TC-09: Successful catch consumes one DinoBall and adds a TamedDinosaur.
     */
    @Test
    @DisplayName("TC-09 | Successful catch consumes DinoBall and adds TamedDinosaur")
    void tc09_catchSuccessInventoryUpdated() {
        player.addItem(new FakeDinoBall());
        enemy.hp = 5; // 5/80 = 6.25% — below threshold

        logic.catchDinosaur();

        assertFalse(player.hasItemOfType(FakeDinoBall.class), "DinoBall must be consumed");
        assertTrue(player.hasItemOfType(FakeTamed.class),     "TamedDinosaur must be added");
        assertTrue(logic.battleEnded,                         "battleEnded must be true");
        assertEquals("onEnemyCaught", logic.lastViewEvent);
    }

    /**
     * TC-10: COMMON rarity catch shows the plain "was caught!" message.
     */
    @Test
    @DisplayName("TC-10 | COMMON enemy catch shows plain message")
    void tc10_catchCommonMessage() {
        player.addItem(new FakeDinoBall());
        enemy.hp = 5; enemy.rarity = Dinosaur.Rarity.COMMON;

        logic.catchDinosaur();

        assertTrue(logic.lastMessage.contains("was caught!"), "Message should say 'was caught!'");
    }

    /**
     * TC-11: UNCOMMON rarity catch shows the "Nice!" message.
     */
    @Test
    @DisplayName("TC-11 | UNCOMMON enemy catch shows 'Nice!' message")
    void tc11_catchUncommonMessage() {
        player.addItem(new FakeDinoBall());
        enemy.hp = 5; enemy.rarity = Dinosaur.Rarity.UNCOMMON;

        logic.catchDinosaur();

        assertTrue(logic.lastMessage.contains("Nice!"), "Message should contain 'Nice!'");
    }

    /**
     * TC-12: RARE rarity catch shows the "INCREDIBLE!" message.
     */
    @Test
    @DisplayName("TC-12 | RARE enemy catch shows 'INCREDIBLE!' message")
    void tc12_catchRareMessage() {
        player.addItem(new FakeDinoBall());
        enemy.hp = 5; enemy.rarity = Dinosaur.Rarity.RARE;

        logic.catchDinosaur();

        assertTrue(logic.lastMessage.contains("INCREDIBLE!"), "Message should contain 'INCREDIBLE!'");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ENEMY TURN TESTS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * TC-13: Enemy turn deals damage equal to enemy strength.
     */
    @Test
    @DisplayName("TC-13 | Enemy turn deals damage equal to enemy strength")
    void tc13_enemyTurnDealsCorrectDamage() {
        logic.enemyTurn();

        assertEquals(85, player.getHp(), "Player HP should be 100 - 15 = 85");
    }

    /**
     * TC-14: Enemy turn restores {@code playerTurn} to {@code true} when player survives.
     */
    @Test
    @DisplayName("TC-14 | Enemy turn restores playerTurn after player survives")
    void tc14_enemyTurnRestoresPlayerTurn() {
        logic.playerTurn = false;
        logic.enemyTurn();

        assertTrue(logic.playerTurn, "playerTurn must be true after enemy turn");
    }

    /**
     * TC-15: When the enemy kills the player, HP is reset to full and
     * {@code onPlayerDefeated} is fired.
     */
    @Test
    @DisplayName("TC-15 | Player death resets HP and fires onPlayerDefeated")
    void tc15_playerDeathResetsHp() {
        player.setHp(10); // enemy strength is 15 → lethal
        logic.enemyTurn();

        assertAll(
                () -> assertEquals(player.getMaxHp(), player.getHp(), "HP must reset to max on defeat"),
                () -> assertTrue(logic.battleEnded,                    "battleEnded must be true"),
                () -> assertEquals("onPlayerDefeated", logic.lastViewEvent)
        );
    }

    /**
     * TC-16: Enemy turn is ignored (no-op) when {@code battleEnded} is true.
     */
    @Test
    @DisplayName("TC-16 | Enemy turn is ignored when battleEnded = true")
    void tc16_enemyTurnIgnoredWhenBattleEnded() {
        logic.battleEnded = true;
        int hpBefore = player.getHp();
        logic.enemyTurn();

        assertEquals(hpBefore, player.getHp(), "Player HP must not change after battle ended");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ESCAPE TESTS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * TC-17: Escape sets {@code battleEnded} and fires {@code onEscape}.
     */
    @Test
    @DisplayName("TC-17 | Escape ends battle and fires onEscape")
    void tc17_escapeFires() {
        logic.escape();

        assertTrue(logic.battleEnded,            "battleEnded must be true");
        assertEquals("onEscape", logic.lastViewEvent);
    }

    /**
     * TC-18: Escape is ignored when the battle has already ended.
     */
    @Test
    @DisplayName("TC-18 | Escape is ignored when battleEnded = true")
    void tc18_escapeIgnoredWhenBattleEnded() {
        logic.battleEnded   = true;
        logic.lastViewEvent = null;
        logic.escape();

        assertNull(logic.lastViewEvent, "No view event should fire when battle already ended");
    }
}