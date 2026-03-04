package gamemode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DialogueManager} class.
 *
 * <p>
 * This test suite verifies:
 * <ul>
 *     <li>Singleton behavior</li>
 *     <li>Dialogue activation and deactivation</li>
 *     <li>Queue processing</li>
 *     <li>Click/skip interaction behavior</li>
 * </ul>
 *
 * The DialogueManager is expected to manage dialogue state,
 * including active status, queued dialogues, and click interactions.
 */
class DialogueManagerTest {

    private DialogueManager manager;

    /**
     * Initializes the DialogueManager instance before each test.
     * Ensures a clean state by closing any active dialogue.
     */
    @BeforeEach
    void setUp() {
        manager = DialogueManager.getInstance();
        manager.close(); // reset state
    }

    /**
     * Tests that DialogueManager follows the Singleton pattern.
     * Both instances retrieved must reference the same object.
     */
    @Test
    void testSingletonInstance() {
        DialogueManager m1 = DialogueManager.getInstance();
        DialogueManager m2 = DialogueManager.getInstance();
        assertSame(m1, m2);
    }

    /**
     * Tests that calling showDialogue() activates the manager.
     * After showing dialogue, the manager should be active.
     */
    @Test
    void testShowDialogueActivatesManager() {
        manager.showDialogue("NPC", "Hello World", null);
        assertTrue(manager.isActive());
    }

    /**
     * Tests that calling close() deactivates the manager.
     */
    @Test
    void testCloseDeactivatesManager() {
        manager.showDialogue("NPC", "Hello", null);
        manager.close();
        assertFalse(manager.isActive());
    }

    /**
     * Tests that queueDialogue() starts the dialogue immediately
     * if the manager is currently inactive.
     */
    @Test
    void testQueueDialogueStartsWhenInactive() {
        manager.queueDialogue("NPC", "First", null);
        assertTrue(manager.isActive());
    }

    /**
     * Tests skip behavior:
     * First click reveals full text,
     * Second click closes the dialogue if no further dialogues exist.
     */
    @Test
    void testSkipRevealsFullText() {
        manager.showDialogue("NPC", "Hello", null);

        // simulate click before fully typed
        manager.onClick();

        // after skip, next click should close (since no queue)
        manager.onClick();
        assertFalse(manager.isActive());
    }

    /**
     * Tests queue progression:
     * After finishing the first dialogue,
     * the manager should automatically advance to the next queued dialogue.
     */
    @Test
    void testQueueAdvancesToNextDialogue() {
        manager.queueDialogue("NPC", "First", null);
        manager.queueDialogue("NPC", "Second", null);

        // skip first
        manager.onClick(); // reveal full
        manager.onClick(); // advance to second

        assertTrue(manager.isActive());
    }
}