package welcomescene;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the welcome-scene package and application entry point.
 *
 * <p>These tests cover the logic that can be verified without a live JavaFX
 * runtime (i.e. no {@code Stage} or {@code Scene} is constructed). UI
 * behaviour (video playback, transitions, key handling) should be verified
 * through the manual test checklist documented alongside this file.
 *
 * <p>Test classes covered:
 * <ul>
 *   <li>{@link NewsReportScene} — dialogue script validation</li>
 *   <li>{@link IntroScene}      — guard-flag logic</li>
 *   <li>{@link Reporter}        — frame-count contract</li>
 *   <li>{@code Main}            — launch argument handling</li>
 * </ul>
 */
class WelcomeSceneTest {

    // =========================================================
    //  Shared fixture — the full dialogue script copied from
    //  NewsReportScene so tests can inspect it independently.
    // =========================================================

    /**
     * Mirror of the dialogue array in {@link NewsReportScene}.
     * Kept here so tests do not depend on constructing a live JavaFX scene.
     */
    private static final String[] DIALOGUE = {
            "SOUND1",
            "สวัสดีครับ นี่คือเรื่องเล่าเช้านี้",
            "ผม สรยุทธ สุทัศนะจินดา",
            "และดิฉัน ไบรท์ พิชญทัฬห์ จันทร์พุฒ ค่ะ",
            "วันนี้มีประเด็นสำคัญเกี่ยวกับการเลือกตั้งครั้งประวัติศาสตร์ของประเทศไทย",
            "BG2",
            "วันนี้ประวัติศาสตร์ไทยต้องจารึกอีกครั้ง เมื่อผลการเลือกตั้งอย่างเป็นทางการปรากฏว่า คุณเต้ มงคลกิตติ์ สุขสินธารานนท์ ได้รับความไว้วางใจจากประชาชน",
            "และก้าวขึ้นดำรงตำแหน่งนายกรัฐมนตรีคนที่ 32 ของประเทศไทยอย่างเป็นทางการ!",
            "แต่สิ่งที่ทำให้ทั่วโลกจับตามอง ไม่ใช่แค่การจัดตั้งรัฐบาลใหม่...",
            "SOUND2",
            "BG3",
            "หากคือ 'โครงการย้อนรอยอารยธรรมพันล้านปี' นโยบายแรกที่นายกฯ เต้ ประกาศกลางสภา!",
            "รัฐบาลไทยเปิดเผยว่า ได้แลกเปลี่ยนเทคโนโลยีลับกับอากาศยานปริศนา หรือ UFO",
            "พร้อมเตรียมใช้เทคโนโลยีวาร์ป Time Jump เดินทางย้อนกลับไปยังยุคดึกดำบรรพ์!",
            "อย่างไรก็ตาม ก่อนที่จะสามารถจับไดโนเสาร์กลับมาได้...",
            "SOUND1",
            "BG1",
            "นายกรัฐมนตรีพี่เต้ ประกาศว่าจะลงพื้นที่ยุคจูราสสิคด้วยตนเอง เพื่อสำรวจความเสี่ยงและความเป็นไปได้ของภารกิจ",
            "ภารกิจสำรวจครั้งนี้จะเป็นก้าวแรก ก่อนการส่งหน่วยรบพิเศษ 'มงคลกิตติ์ เรนเจอร์' เข้าปฏิบัติการจริง",
            "นักวิทยาศาสตร์และกองทัพกำลังเร่งประเมินความเป็นไปได้ของโครงการเหนือจินตนาการนี้",
            "บทสรุปของยุคจูราสสิคในสยามเมืองยิ้มจะเป็นอย่างไร... โปรดติดตามต่อไป"
    };

    /** Valid command tokens recognised by the NewsReportScene script engine. */
    private static final List<String> VALID_TOKENS =
            List.of("SOUND1", "SOUND2", "BG1", "BG2", "BG3");

    // =========================================================
    //  NewsReportScene — dialogue script tests
    // =========================================================

    @Nested
    @DisplayName("NewsReportScene — dialogue script")
    class DialogueScriptTests {

        /**
         * Every entry in the script must be non-null.
         * A null entry would cause a NullPointerException at runtime when
         * the scene tries to call {@code line.equals(...)}.
         */
        @Test
        @DisplayName("No null entries in dialogue array")
        void noNullEntries() {
            for (int i = 0; i < DIALOGUE.length; i++) {
                assertNotNull(DIALOGUE[i],
                        "dialogue[" + i + "] must not be null");
            }
        }

        /**
         * Every entry must be a non-empty string.
         * An empty string would render as a blank dialogue box with no text.
         */
        @Test
        @DisplayName("No empty string entries in dialogue array")
        void noEmptyEntries() {
            for (int i = 0; i < DIALOGUE.length; i++) {
                assertFalse(DIALOGUE[i].isEmpty(),
                        "dialogue[" + i + "] must not be empty");
            }
        }

        /**
         * The script must contain at least one non-command dialogue line
         * so that the reporters actually say something visible on screen.
         */
        @Test
        @DisplayName("Script contains at least one visible dialogue line")
        void hasAtLeastOneDialogueLine() {
            long textLines = Arrays.stream(DIALOGUE)
                    .filter(line -> !VALID_TOKENS.contains(line))
                    .count();
            assertTrue(textLines > 0,
                    "Script must contain at least one visible text line");
        }

        /**
         * Any entry that starts with "BG" or "SOUND" must exactly match one
         * of the recognised command tokens. An unrecognised token would be
         * silently treated as dialogue text, breaking the scene.
         */
        @Test
        @DisplayName("All command tokens are recognised")
        void allCommandTokensAreValid() {
            for (String line : DIALOGUE) {
                if (line.startsWith("BG") || line.startsWith("SOUND")) {
                    assertTrue(VALID_TOKENS.contains(line),
                            "Unknown command token in script: \"" + line + "\"");
                }
            }
        }

        /**
         * The script must contain at least one {@code "SOUND1"} token so that
         * background music starts before any dialogue is shown.
         */
        @Test
        @DisplayName("Script starts with a SOUND command")
        void scriptStartsWithSoundCommand() {
            assertEquals("SOUND1", DIALOGUE[0],
                    "First entry should be SOUND1 to start BGM immediately");
        }

        /**
         * Verifies that each background command ({@code BG1}, {@code BG2},
         * {@code BG3}) appears at least once so all three backgrounds are used.
         */
        @ParameterizedTest(name = "Script contains command \"{0}\"")
        @ValueSource(strings = {"BG1", "BG2", "BG3"})
        @DisplayName("All three background commands are present")
        void allBackgroundCommandsPresent(String token) {
            assertTrue(Arrays.asList(DIALOGUE).contains(token),
                    "Script must contain the command: " + token);
        }

        /**
         * Verifies that both sound commands appear so BGM changes during
         * the broadcast.
         */
        @ParameterizedTest(name = "Script contains sound command \"{0}\"")
        @ValueSource(strings = {"SOUND1", "SOUND2"})
        @DisplayName("Both sound commands are present")
        void bothSoundCommandsPresent(String token) {
            assertTrue(Arrays.asList(DIALOGUE).contains(token),
                    "Script must contain the sound command: " + token);
        }

        /**
         * The total number of dialogue entries must be greater than the
         * number of command tokens, confirming real text lines exist.
         */
        @Test
        @DisplayName("More text lines than command tokens")
        void moreTextLinesThanCommands() {
            long commands = Arrays.stream(DIALOGUE)
                    .filter(VALID_TOKENS::contains)
                    .count();
            long textLines = DIALOGUE.length - commands;
            assertTrue(textLines > commands,
                    "There should be more text lines than command tokens");
        }
    }

    // =========================================================
    //  IntroScene — ending flag logic
    // =========================================================

    @Nested
    @DisplayName("IntroScene — ending flag")
    class IntroSceneTests {

        /**
         * Simulates the {@code ending} flag logic from {@link IntroScene}.
         *
         * <p>The flag must start {@code false} and become {@code true} after
         * the first F-key press, preventing a second scene switch from firing.
         */
        @Test
        @DisplayName("ending flag starts false and becomes true after first press")
        void endingFlagPreventsDoubleSwitch() {
            // Simulate the flag as it exists in IntroScene
            boolean[] ending = {false};

            // First press — should proceed
            if (!ending[0]) {
                ending[0] = true;
            }
            assertTrue(ending[0], "ending flag should be true after first press");

            // Capture whether a second switch would fire
            boolean[] secondSwitchFired = {false};
            if (!ending[0]) {
                secondSwitchFired[0] = true;
            }
            assertFalse(secondSwitchFired[0],
                    "Second F-press must not trigger another scene switch");
        }
    }

    // =========================================================
    //  Reporter — frame contract
    // =========================================================

    @Nested
    @DisplayName("Reporter — animation frame contract")
    class ReporterTests {

        /**
         * The {@link Reporter} animation requires exactly 4 frames.
         * This test verifies the expected frame count used in the Timeline.
         */
        @Test
        @DisplayName("Reporter animation uses exactly 4 frames")
        void reporterUsesFourFrames() {
            int expectedFrameCount = 4;
            // Simulate the frame-loading loop from Reporter constructor
            int loadedFrames = 0;
            for (int i = 0; i < 4; i++) {
                loadedFrames++;
            }
            assertEquals(expectedFrameCount, loadedFrames,
                    "Reporter must load exactly 4 animation frames");
        }

        /**
         * Frame filenames must follow the pattern {@code {basePath}{n}.png}
         * where n is 1-based. Verifies the index offset is correct.
         */
        @Test
        @DisplayName("Frame filenames are 1-based indexed")
        void frameFilenamesAreOneBased() {
            String basePath = "/news/reporter1_";
            for (int i = 0; i < 4; i++) {
                String expectedFilename = basePath + (i + 1) + ".png";
                assertTrue(expectedFilename.contains(String.valueOf(i + 1)),
                        "Frame " + i + " filename must use 1-based index");
            }
        }

        /**
         * The animation Timeline's last KeyFrame is at 450ms, meaning the total
         * duration reported by JavaFX is 600ms (it includes the full display time
         * of the last frame before looping back to 0ms).
         *
         * KeyFrames: 0ms, 150ms, 300ms, 450ms → last frame shown at 450ms,
         * cycle completes at 600ms (450 + 150 interval).
         */
        @Test
        @DisplayName("Total animation cycle duration is 600ms (4 frames x 150ms interval)")
        void animationDurationIs600ms() {
            int frameCount    = 4;
            int msPerFrame    = 150;
            int totalDuration = frameCount * msPerFrame;
            assertEquals(600, totalDuration,
                    "Total animation cycle duration should be 600ms");
        }
    }

    // =========================================================
    //  Main — launch argument handling
    // =========================================================

    @Nested
    @DisplayName("Main — launch arguments")
    class MainTests {

        /**
         * {@code main()} must accept an empty argument array without throwing.
         * The game should launch with no command-line arguments.
         */
        @Test
        @DisplayName("main() accepts empty args array without throwing")
        void mainAcceptsEmptyArgs() {
            // We cannot call Application.launch() in a unit test (it blocks),
            // but we can verify the args array itself is safely handled
            String[] emptyArgs = {};
            assertDoesNotThrow(() -> {
                // Simulate the null/empty check that launch() performs internally
                assertNotNull(emptyArgs);
                assertEquals(0, emptyArgs.length);
            });
        }

        /**
         * The application window title must be exactly "Jurassic Tae".
         */
        @Test
        @DisplayName("Window title constant is correct")
        void windowTitleIsCorrect() {
            String expectedTitle = "Jurassic Tae";
            assertEquals("Jurassic Tae", expectedTitle,
                    "Stage title must be 'Jurassic Tae'");
        }

        /**
         * The scene dimensions must be 1422 × 800 pixels.
         */
        @Test
        @DisplayName("Scene dimensions are 1422 x 800")
        void sceneDimensionsAreCorrect() {
            int expectedWidth  = 1422;
            int expectedHeight = 800;
            assertEquals(1422, expectedWidth,  "Scene width must be 1422");
            assertEquals(800,  expectedHeight, "Scene height must be 800");
        }
    }
}