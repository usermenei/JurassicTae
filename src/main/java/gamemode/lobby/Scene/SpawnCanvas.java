package gamemode.lobby.Scene;

import gamemode.DialogueManager;
import gamemode.lobby.Item.DinoBall;
import gamemode.lobby.Player.Player;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import gamemode.lobby.logic.*;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.KeyboardController;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SpawnCanvas extends Canvas {
    private GraphicsContext gc ;
    private Player player = GameLogic.getInstance().getPlayer();
    private Image shopImg,zooImg,ufoImg,gymImg;
    private boolean fWasPressed = false;
    private boolean showEnterShop = false,showEnterSell = false,showEnterGym = false,showEnterUfo = false;
    public SpawnCanvas(){

    public SpawnCanvas() {

        super(1422, 800);

        // Give player starter DinoBalls
        for (int i = 0; i < 3; i++) player.addItem(new DinoBall());

        gc = this.getGraphicsContext2D();

        shop = new Shop();
        zoo  = new Zoo();
        gym  = new Gym();
        ufo  = new Ufo();

        shopImg = shop.getImage();
        zooImg  = zoo.getImage();
        gymImg  = gym.getImage();
        ufoImg  = ufo.getImage();

        startGameLoop();

        // ===== INTRO DIALOGUE =====
        javafx.animation.PauseTransition intro =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.5));

        intro.setOnFinished(e -> {
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "Hm... As the Prime Minister of Thailand, I never expected to wake up surrounded by dinosaurs.",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "I have 3 DinoBalls in my bag. I should use them wisely to catch dinosaurs!",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "See that Shop in the top-left? I can buy more DinoBalls and potions there. Press F to enter.",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "The Zoo is in the top-right. I can sell caught dinosaurs there for money. Press F to enter.",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "There is also a Gym. If I pay 500$, I can train and boost my strength for battle!",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "The UFO will take me to the dinosaur world. Weaken a dinosaur below 10% HP to catch it.",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "Press E to pick up items I find on the ground while exploring.",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "And if things get too dangerous... press ESC to retreat back to this lobby!",
                    "/character/ptae.png"
            );
            DialogueManager.getInstance().queueDialogue(
                    "P'Tae",
                    "Alright. As Prime Minister, I cannot afford to fail. Let's go catch some dinosaurs!",
                    "/character/ptae.png"
            );
        });

        intro.play();

        // Click to advance dialogue
        setOnMouseClicked(e -> DialogueManager.getInstance().onClick());
    }

    // ===================================================

    private void startGameLoop() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, getWidth(), getHeight());

                update();
                render();

                // Reset alignment BEFORE dialogue renders so text is never shifted
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BASELINE);

                DialogueManager.getInstance().update();
                DialogueManager.getInstance().render(gc, getWidth(), getHeight());
            }
        };

        timer.start();
    }

    // ===================================================

    private void update() {

        if (GameController.getInstance().isGameEnded()) return;

        player.updateBuffs();

        KeyboardController keyboard = GameController.getInstance().getKeyboard();

        int dx = 0;
        int dy = 0;

        if (keyboard.isLeftPressed())  dx = -1;
        if (keyboard.isRightPressed()) dx = 1;
        if (keyboard.isUpPressed())    dy = -1;
        if (keyboard.isDownPressed())  dy = 1;

        player.move(dx, dy);

        showEnterShop = player.intersects(80,75,250,500);
        showEnterGym = player.intersects(80,450,250,500);
        showEnterSell = player.intersects(800,75,250,500);
        showEnterUfo = player.intersects(800,450,250,500);

        // ===== F KEY — dialogue skip (fires once per press) =====
        if (keyboard.isFPressed() && !fDialogueWasPressed) {
            fDialogueWasPressed = true;

            if (DialogueManager.getInstance().isActive()) {
                DialogueManager.getInstance().onClick();
                return; // block location entry while dialogue open
            }
        }

        // ===== F KEY — location entry =====
        if (keyboard.isFPressed() && !fWasPressed) {
            fWasPressed = true;

            if (!DialogueManager.getInstance().isActive()) {
                if (showEnterGym) {
                    GameController.getInstance().startGymMiniGame();
                } else if (showEnterShop) {
                    GameController.getInstance().getRoot().showShopScene();
                } else if (showEnterSell) {
                    GameController.getInstance().getRoot().showSellScene();
                } else if (showEnterUfo) {
                    GameController.getInstance().startCretaceousExploration();
                }
            }
        }

        // Reset both flags when F is released
        if (!keyboard.isFPressed()) {
            fWasPressed = false;
            fDialogueWasPressed = false;
        }
    }

    // ===================================================

    private void render() {
        player.render(gc);

        if (showEnterShop) {
            drawPressMessage("SHOP");
        }else if(showEnterSell){
            drawPressMessage("ZOO");
        }else if(showEnterUfo){
            drawPressMessage("UFO");
        }else if(showEnterGym){
            drawPressMessage("GYM");
        }
    }

        // ✅ Reset after drawPressMessage so dialogue text is never shifted
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
    }

    // ===================================================

    public Player getPlayer(){
        return player;
    }

    private void drawPressMessage(String location) {

        String text;

        if (location.equals("GYM")) {
            text = "Press F to enter the GYM\nFee: 500";
        } else {
            text = "Press F to enter the " + location;
        }

        String[] lines = text.split("\n");

        Font font = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"), 20);
        gc.setFont(font);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        double lineHeight = font.getSize() + 10;

        double boxWidth = 300;
        double boxHeight = (lineHeight * lines.length) + padding ;

        int xPos,width=500,yPos,height=250;
        switch (location){
            case "SHOP":
                xPos = 80;
                yPos = 75;
                break;
            case "GYM":
                xPos = 80;
                yPos = 450;
                break;
            case "ZOO":
                xPos = 800;
                yPos = 75;
                break;
            case "UFO":
                xPos = 800;
                yPos = 450;
                break;
            default:
                xPos = 80;
                yPos = 75;
        }

        double boxX = xPos + (width / 2) - (boxWidth / 2);
        double boxY = yPos + (height / 2) - (boxHeight / 2);

        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        gc.setFill(Color.WHITE);
        double centerY = boxY + boxHeight / 2;

        for (int i = 0; i < lines.length; i++) {
            double yOffset = (i - (lines.length - 1) / 2.0) * lineHeight;
            gc.fillText(lines[i], boxX + boxWidth / 2, centerY + yOffset);
        }
    }
}
