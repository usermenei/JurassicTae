package welcomescene;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import gamemode.lobby.logic.GameController;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * A scripted news-broadcast cut-scene that tells the backstory of
 * <b>Jurassic Tae</b> through two animated TV reporters.
 *
 * <p>The scene is driven by the {@link #dialogue} array, which is a sequential
 * script containing:
 * <ul>
 *   <li>Regular Thai text lines — displayed in the dialogue box and
 *       attributed to alternating reporters</li>
 *   <li>{@code "SOUND1"} / {@code "SOUND2"} — trigger background music swaps</li>
 *   <li>{@code "BG1"} / {@code "BG2"} / {@code "BG3"} — swap the background
 *       image and show/hide the reporter sprites</li>
 * </ul>
 *
 * <p>The player advances the script by clicking the mouse or pressing
 * {@link KeyCode#SPACE}. Pressing {@link KeyCode#F} skips the entire scene
 * and calls {@link GameController#returnToMain()} immediately.
 *
 * <p>Scene position in the intro flow:
 * <pre>
 *   IntroScene → ChongsamVideoScene → <b>NewsReportScene</b> → (lobby / main scene)
 * </pre>
 *
 * @see ChongsamVideoScene
 * @see Reporter
 * @see GameController#returnToMain()
 */
public class NewsReportScene {

    /** The JavaFX {@link Scene} containing all news-broadcast UI elements. */
    private Scene scene;

    /**
     * Index of the next entry to be read from {@link #dialogue}.
     * Incremented each time the player advances the script.
     */
    private int index = 0;

    /**
     * Tracks which reporter is currently speaking.
     * {@code true} = reporter 1 (สรยุทธ), {@code false} = reporter 2 (ไบรท์).
     */
    private boolean reporter1Turn = true;

    /**
     * {@link MediaPlayer} for the looping background music track.
     * Replaced when a {@code "SOUND1"} or {@code "SOUND2"} command is
     * encountered in the dialogue script.
     */
    private MediaPlayer bgmPlayer;

    /**
     * {@link MediaPlayer} reserved for short sound-effect clips.
     * Currently unused but kept for future SFX support.
     */
    private MediaPlayer sfxPlayer;

    /**
     * The full dialogue script for the news broadcast.
     *
     * <p>Special command tokens embedded in the array:
     * <ul>
     *   <li>{@code "SOUND1"} — plays {@code /news/bgm1.mp3} on loop</li>
     *   <li>{@code "SOUND2"} — plays {@code /news/bgm2.mp3} on loop</li>
     *   <li>{@code "BG1"}    — switches to background 1 and shows reporters</li>
     *   <li>{@code "BG2"}    — switches to background 2 and hides reporters</li>
     *   <li>{@code "BG3"}    — switches to background 3 and hides reporters</li>
     * </ul>
     * All other entries are Thai-language dialogue lines displayed in the
     * dialogue box and spoken by alternating reporters.
     */
    private final String[] dialogue = {

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

    /**
     * Constructs and fully initialises the news-report cut-scene.
     *
     * <p>Steps performed:
     * <ul>
     *   <li>Loads the Thai pixel font ({@code /fonts/thaipixel.ttf})</li>
     *   <li>Creates the background {@link ImageView} starting with
     *       {@code /news/bg1.png}</li>
     *   <li>Positions two {@link Reporter} sprite animations on the left
     *       and right sides of the screen</li>
     *   <li>Builds a dialogue box at the bottom with a speaker-name label
     *       and a wrapping message label</li>
     *   <li>Wires mouse-click and keyboard handlers to advance the script</li>
     *   <li>Immediately runs the first line of the script so the scene is
     *       not blank on first display</li>
     *   <li>Adds a blinking "Press F to Skip" hint label</li>
     * </ul>
     */
    public NewsReportScene() {

        StackPane root = new StackPane();

        // ================= LOAD THAI PIXEL FONT =================
        Font thaiPixelFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/thaipixel.ttf"),
                30
        );

        // ================= BACKGROUND =================
        ImageView background = new ImageView(
                new Image(getClass().getResourceAsStream("/news/bg1.png"))
        );
        background.setFitWidth(1422);
        background.setFitHeight(800);
        root.getChildren().add(background);

        // ================= REPORTERS =================
        Reporter reporter1 = new Reporter("/news/reporter1_", 0, 150);
        Reporter reporter2 = new Reporter("/news/reporter2_", 0, 150);

        reporter1.getView().setFitWidth(450);
        reporter2.getView().setFitWidth(450);
        reporter1.getView().setPreserveRatio(true);
        reporter2.getView().setPreserveRatio(true);

        StackPane.setAlignment(reporter1.getView(), Pos.CENTER_LEFT);
        StackPane.setAlignment(reporter2.getView(), Pos.CENTER_RIGHT);

        reporter1.getView().setTranslateX(120);
        reporter2.getView().setTranslateX(-120);
        reporter1.getView().setTranslateY(-30);
        reporter2.getView().setTranslateY(-30);

        root.getChildren().addAll(reporter1.getView(), reporter2.getView());

        // ================= DIALOGUE BOX =================
        VBox dialogueContainer = new VBox();
        dialogueContainer.setPrefWidth(1100);
        dialogueContainer.setMaxWidth(1100);
        dialogueContainer.setPrefHeight(150);
        dialogueContainer.setMaxHeight(150);
        dialogueContainer.setPadding(new Insets(15));
        dialogueContainer.setSpacing(8);
        dialogueContainer.setStyle("""
            -fx-background-color: black;
            -fx-border-color: white;
            -fx-border-width: 3;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
        """);

        // Speaker name label — updated as reporters alternate
        Label reporterLabel = new Label("สรยุทธ");
        reporterLabel.setFont(thaiPixelFont);
        reporterLabel.setStyle("-fx-text-fill: #00ffff;");

        StackPane messageBox = new StackPane();
        messageBox.setPrefHeight(90);
        messageBox.setMaxHeight(90);
        messageBox.setPadding(new Insets(10));

        // Main dialogue text label — updated each time the script advances
        Label messageText = new Label("");
        messageText.setWrapText(true);
        messageText.setMaxWidth(1000);
        messageText.setFont(Font.font(thaiPixelFont.getFamily(), 30));
        messageText.setStyle("-fx-text-fill: white;");

        messageBox.getChildren().add(messageText);
        dialogueContainer.getChildren().addAll(reporterLabel, messageBox);

        StackPane.setAlignment(dialogueContainer, Pos.BOTTOM_CENTER);
        dialogueContainer.setTranslateY(-40);
        root.getChildren().add(dialogueContainer);

        scene = new Scene(root, 1422, 800);

        // ================= INPUT HANDLER =================
        /**
         * Advances the script by one dialogue line (or processes the next
         * command token) each time it is invoked.
         *
         * Command tokens ({@code SOUND1}, {@code SOUND2}, {@code BG1–3}) are
         * consumed silently and the loop continues until a real text line is
         * found. When the script is exhausted, BGM is stopped and the game
         * transitions to the main lobby.
         */
        Runnable nextDialogue = () -> {

            while (index < dialogue.length) {

                String line = dialogue[index++];

                // Music swap commands
                if (line.equals("SOUND1")) { playBGM("/news/bgm1.mp3"); continue; }
                if (line.equals("SOUND2")) { playBGM("/news/bgm2.mp3"); continue; }

                // Background / reporter visibility commands
                if (line.equals("BG1")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg1.png")));
                    reporter1.getView().setVisible(true);
                    reporter2.getView().setVisible(true);
                    continue;
                }
                if (line.equals("BG2")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg2.png")));
                    reporter1.getView().setVisible(false);
                    reporter2.getView().setVisible(false);
                    continue;
                }
                if (line.equals("BG3")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg3.png")));
                    reporter1.getView().setVisible(false);
                    reporter2.getView().setVisible(false);
                    continue;
                }

                // Regular dialogue line — display text and animate the speaker
                messageText.setText(line);
                reporter1Turn = !reporter1Turn;
                reporterLabel.setText(reporter1Turn ? "สรยุทธ" : "ไบรท์");

                if (reporter1Turn) {
                    reporter1.startTalking();
                    reporter2.stopTalking();
                } else {
                    reporter2.startTalking();
                    reporter1.stopTalking();
                }

                break;
            }

            // End of script — return to lobby
            if (index >= dialogue.length) {
                stopBGM();
                GameController.getInstance().returnToMain();
            }
        };

        // Mouse click advances dialogue
        scene.setOnMouseClicked(e -> nextDialogue.run());

        // SPACE advances dialogue; F skips the entire scene
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) nextDialogue.run();
            if (e.getCode() == KeyCode.F) {
                stopBGM();
                GameController.getInstance().returnToMain();
            }
        });

        // Run first line immediately so scene is not blank on display
        nextDialogue.run();

        // ================= SKIP LABEL =================
        Label infoText = new Label("Press F to Skip");
        infoText.setFont(Font.font(thaiPixelFont.getFamily(), 18));
        infoText.setStyle("-fx-text-fill: white;");
        StackPane.setAlignment(infoText, Pos.BOTTOM_CENTER);
        infoText.setTranslateY(-10);
        root.getChildren().add(infoText);

        // Blink animation for the skip hint
        FadeTransition blink = new FadeTransition(Duration.seconds(0.8), infoText);
        blink.setFromValue(1.0);
        blink.setToValue(0.2);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
    }

    /**
     * Starts or replaces the background music track.
     *
     * <p>If a BGM track is already playing it is stopped before the new one
     * begins. The new track loops indefinitely via
     * {@link MediaPlayer#INDEFINITE}.
     *
     * @param path classpath-relative path to the MP3 resource
     *             (e.g. {@code "/news/bgm1.mp3"})
     */
    private void playBGM(String path) {
        try {
            if (bgmPlayer != null) bgmPlayer.stop();
            Media media = new Media(getClass().getResource(path).toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the background music player if one is currently active.
     *
     * <p>Called when the script ends naturally or when the player skips the
     * scene with {@link KeyCode#F}, ensuring no audio leaks into the lobby.
     */
    private void stopBGM() {
        if (bgmPlayer != null) bgmPlayer.stop();
    }

    /**
     * Returns the {@link Scene} containing the news-broadcast UI.
     *
     * <p>Pass this to {@link GameController#switchScene(javafx.scene.Scene)}
     * or {@link javafx.stage.Stage#setScene(javafx.scene.Scene)} to display
     * this scene.
     *
     * @return the fully initialised news-report {@link Scene}
     */
    public Scene getScene() {
        return scene;
    }
}