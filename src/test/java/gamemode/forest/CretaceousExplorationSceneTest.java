package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.util.WorldManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class CretaceousExplorationSceneTest {

    @Test
    void testSceneCreation() {

        AtomicBoolean battleCalled = new AtomicBoolean(false);
        AtomicBoolean exitCalled = new AtomicBoolean(false);

        CretaceousExplorationScene scene = new CretaceousExplorationScene(
                dino -> battleCalled.set(true),
                () -> exitCalled.set(true)
        );

        assertNotNull(scene);
        assertNotNull(scene.getScene());
    }

    @Test
    void testWorldManagerExists() {

        CretaceousExplorationScene scene = new CretaceousExplorationScene(
                d -> {},
                () -> {}
        );

        WorldManager wm = scene.getWorldManager();

        assertNotNull(wm);
    }

    @Test
    void testResumeWorldDoesNotCrash() {

        CretaceousExplorationScene scene = new CretaceousExplorationScene(
                d -> {},
                () -> {}
        );

        assertDoesNotThrow(scene::resumeWorld);
    }

    @Test
    void testClearInputDoesNotCrash() {

        CretaceousExplorationScene scene = new CretaceousExplorationScene(
                d -> {},
                () -> {}
        );

        assertDoesNotThrow(scene::clearInput);
    }
}