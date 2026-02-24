package welcomescene;

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

public class NewsReportScene {

    private Scene scene;
    private int index = 0;
    private boolean reporter1Turn = true;

    private MediaPlayer bgmPlayer;

    private final String[] dialogue = {

            "SOUND1",
            "สวัสดีครับ นี่คือเรื่องเล่าเช้านี้",
            "ผม สรยุทธ์ สุทัศนะจินดา",
            "และดิฉัน ไบรท์ พิชญทัฬห์ จันทร์พุฒ ค่ะ",
            "วันนี้มีประเด็นสำคัญเกี่ยวกับการเลือกตั้งครั้งประวัติศาสตร์ของประเทศไทย",

            "BG2",
            "วันนี้ประวัติศาสตร์ไทยต้องจารึกอีกครั้ง เมื่อผลการเลือกตั้งอย่างเป็นทางการปรากฏว่า คุณเต้ มงคลกิตติ์ สุขสินธารานนท์ ได้รับความไว้วางใจจากประชาชน",
            "และก้าวขึ้นดำรงตำแหน่งนายกรัฐมนตรีคนที่ 31 ของประเทศไทยอย่างเป็นทางการ!",
            "แต่สิ่งที่ทำให้ทั่วโลกจับตามอง ไม่ใช่แค่การจัดตั้งรัฐบาลใหม่...",

            "SOUND2",
            "BG3",
            "หากคือ 'โครงการย้อนรอยอารยธรรมพันล้านปี' นโยบายแรกที่นายกฯ เต้ ประกาศกลางสภา!",
            "รัฐบาลไทยเปิดเผยว่า ได้แลกเปลี่ยนเทคโนโลยีลับกับอากาศยานปริศนา หรือ UFO",
            "พร้อมเตรียมใช้เทคโนโลยีวาร์ป Time Jump เดินทางย้อนกลับไปยังยุคดึกดำบรรพ์!",
            "อย่างไรก็ตาม ก่อนที่จะสามารถจับไดโนเสาร์กลับมาได้...",

            "SOUND1",
            "BG1",
            "นายกรัฐมนตรีเต้ ประกาศว่าจะลงพื้นที่ยุคจูราสสิคด้วยตนเอง เพื่อสำรวจความเสี่ยงและความเป็นไปได้ของภารกิจ",
            "ภารกิจสำรวจครั้งนี้จะเป็นก้าวแรก ก่อนการส่งหน่วยรบพิเศษ 'มงคลกิตติ์ เรนเจอร์' เข้าปฏิบัติการจริง",
            "นักวิทยาศาสตร์และกองทัพกำลังเร่งประเมินความเป็นไปได้ของโครงการเหนือจินตนาการนี้",
            "บทสรุปของยุคจูราสสิคในสยามเมืองยิ้มจะเป็นอย่างไร... โปรดติดตามต่อไป"
    };

    public NewsReportScene() {

        StackPane root = new StackPane();

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

        // ================= DIALOGUE BOX (Stable Version) =================

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

        Label reporterLabel = new Label("สรยุทธ์");
        reporterLabel.setStyle("""
            -fx-text-fill: #00ffff;
            -fx-font-size: 20px;
        """);

        StackPane messageBox = new StackPane();
        messageBox.setPrefHeight(90);
        messageBox.setMaxHeight(90);
        messageBox.setPadding(new Insets(10));

        Label messageText = new Label("");
        messageText.setWrapText(true);
        messageText.setMaxWidth(1000);
        messageText.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 18px;
        """);

        messageBox.getChildren().add(messageText);
        dialogueContainer.getChildren().addAll(reporterLabel, messageBox);

        StackPane.setAlignment(dialogueContainer, Pos.BOTTOM_CENTER);
        dialogueContainer.setTranslateY(-40);

        root.getChildren().add(dialogueContainer);

        scene = new Scene(root, 1422, 800);

        // ================= INPUT HANDLER =================
        Runnable nextDialogue = () -> {

            while (index < dialogue.length) {

                String line = dialogue[index++];

                if (line.equals("SOUND1")) {
                    playBGM("/news/bgm1.mp3");
                    continue;
                }

                if (line.equals("SOUND2")) {
                    playBGM("/news/bgm2.mp3");
                    continue;
                }

                if (line.equals("BG1")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg1.png")));

                    // SHOW REPORTERS
                    reporter1.getView().setVisible(true);
                    reporter2.getView().setVisible(true);
                    continue;
                }

                if (line.equals("BG2")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg2.png")));

                    // HIDE REPORTERS
                    reporter1.getView().setVisible(false);
                    reporter2.getView().setVisible(false);
                    continue;
                }

                if (line.equals("BG3")) {
                    background.setImage(new Image(getClass().getResourceAsStream("/news/bg3.png")));

                    // HIDE REPORTERS
                    reporter1.getView().setVisible(false);
                    reporter2.getView().setVisible(false);
                    continue;
                }

                messageText.setText(line);

                reporter1Turn = !reporter1Turn;
                reporterLabel.setText(reporter1Turn ? "สรยุทธ์" : "ไบรท์");

                if (reporter1Turn) {
                    reporter1.startTalking();
                    reporter2.stopTalking();
                } else {
                    reporter2.startTalking();
                    reporter1.stopTalking();
                }

                break;
            }

            if (index >= dialogue.length) {
                stopBGM();
                GameController.getInstance().returnToMain();
            }
        };

        scene.setOnMouseClicked(e -> nextDialogue.run());

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) nextDialogue.run();
            if (e.getCode() == KeyCode.F) {
                stopBGM();
                GameController.getInstance().returnToMain();
            }
        });

        nextDialogue.run();
    }

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

    private void stopBGM() {
        if (bgmPlayer != null) bgmPlayer.stop();
    }

    public Scene getScene() {
        return scene;
    }
}