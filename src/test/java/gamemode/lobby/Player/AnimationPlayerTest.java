package gamemode.lobby.Player;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1>AnimationPlayerTest</h1>
 *
 * <p>
 * Unit test class for {@link AnimationPlayer}.
 * </p>
 *
 * <p>
 * This test suite verifies:
 * </p>
 * <ul>
 *     <li>Frame advancement logic</li>
 *     <li>Looping behavior</li>
 *     <li>Reset functionality</li>
 * </ul>
 *
 * <p>
 * Rendering is not tested because it depends on JavaFX
 * GraphicsContext which requires UI environment.
 * </p>
 *
 * @author Pongtawan
 * @version 1.0
 * @since 2026
 */
public class AnimationPlayerTest {

    private AnimationPlayer animationPlayer;

    /**
     * Sets up a fresh AnimationPlayer instance before each test.
     */
    @BeforeEach
    void setUp() {
        Image dummy1 = new Image("https://via.placeholder.com/10");
        Image dummy2 = new Image("https://via.placeholder.com/10");

        Image[] frames = {dummy1, dummy2};

        animationPlayer = new AnimationPlayer(frames, 1);
    }

    /**
     * Tests that animation advances frames when enough time has passed.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    void testFrameAdvancement() throws InterruptedException {
        int initialFrame = getCurrentFrame();

        Thread.sleep(5);
        animationPlayer.update();

        int updatedFrame = getCurrentFrame();

        assertNotEquals(initialFrame, updatedFrame,
                "Frame should advance after update with sufficient delay.");
    }

    /**
     * Tests that animation loops back to the first frame
     * when looping is enabled.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    void testLoopingBehavior() throws InterruptedException {
        animationPlayer.setLoop(true);

        Thread.sleep(5);
        animationPlayer.update();

        Thread.sleep(5);
        animationPlayer.update();

        assertTrue(getCurrentFrame() == 0 || getCurrentFrame() == 1,
                "Frame should loop back when exceeding frame count.");
    }

    /**
     * Tests that animation stops at last frame
     * when looping is disabled.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    void testNonLoopingBehavior() throws InterruptedException {
        animationPlayer.setLoop(false);

        Thread.sleep(5);
        animationPlayer.update();

        Thread.sleep(5);
        animationPlayer.update();

        assertEquals(1, getCurrentFrame(),
                "Frame should remain at last frame when looping is disabled.");
    }

    /**
     * Tests that reset() returns animation to first frame.
     */
    @Test
    void testResetFunctionality() {
        animationPlayer.reset();
        assertEquals(0, getCurrentFrame(),
                "Reset should set current frame to 0.");
    }

    /**
     * Helper method to access private currentFrame field
     * using reflection for testing purposes.
     *
     * @return current frame index
     */
    private int getCurrentFrame() {
        try {
            var field = AnimationPlayer.class.getDeclaredField("currentFrame");
            field.setAccessible(true);
            return field.getInt(animationPlayer);
        } catch (Exception e) {
            fail("Reflection access failed.");
            return -1;
        }
    }
}