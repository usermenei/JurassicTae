package gamemode.fightscene;

import javafx.application.Platform;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class CommandBoxTest {

    @BeforeAll
    static void initFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}
    }

    private CommandBox createBox() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        CommandBox[] box = new CommandBox[1];

        Platform.runLater(() -> {
            box[0] = new CommandBox("Tester");
            latch.countDown();
        });

        latch.await();
        return box[0];
    }

    @Test
    void tc01_constructorCreatesObject() throws Exception {
        CommandBox box = createBox();
        assertNotNull(box);
    }

    @Test
    void tc02_fightButtonExists() throws Exception {
        CommandBox box = createBox();
        assertNotNull(box.getFightButton());
    }

    @Test
    void tc03_bagButtonExists() throws Exception {
        CommandBox box = createBox();
        assertNotNull(box.getBagButton());
    }

    @Test
    void tc04_catchButtonExists() throws Exception {
        CommandBox box = createBox();
        assertNotNull(box.getCatchButton());
    }

    @Test
    void tc05_escapeButtonExists() throws Exception {
        CommandBox box = createBox();
        assertNotNull(box.getEscapeButton());
    }

    @Test
    void tc06_setMessageDoesNotCrash() throws Exception {
        CommandBox box = createBox();
        assertDoesNotThrow(() -> box.setMessage("Test Message"));
    }
}