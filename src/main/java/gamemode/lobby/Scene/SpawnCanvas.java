package gamemode.lobby.Scene;

import gamemode.forest.entity.CarnivoreDinosaur;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.TamedDinosaur;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class SpawnCanvas extends Canvas {
    private GraphicsContext gc ;
    private Player player = GameLogic.getInstance().getPlayer();
    private Image shopImg,zooImg,ufoImg,gymImg;
    private boolean fWasPressed = false;
    private boolean showEnterShop = false,showEnterSell = false,showEnterGym = false,showEnterUfo = false;
    public SpawnCanvas(){

        super(1422,800);

        //********************************
        for(int i = 0;i<10;i++) player.addItem(new TamedDinosaur(new CarnivoreDinosaur()));
        //*******************************

        gc = this.getGraphicsContext2D();

        startGameLoop();   // ✅ ต้องเรียก
    }

    private void startGameLoop() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                gc.clearRect(0,0,getWidth(),getHeight());

                update();
                render();
            }
        };

        timer.start();
    }

    private void update() {
        if (GameController.getInstance().isGameEnded()) return;

        player.updateBuffs();

        KeyboardController keyboard =
                GameController.getInstance().getKeyboard();

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

        // 🔥 ENTER LOGIC (Fixed)
        if (keyboard.isFPressed() && !fWasPressed) {

            fWasPressed = true;   // prevent spam

            if (showEnterGym) {
                GameController.getInstance().startGymMiniGame();
            }
            else if (showEnterShop) {
                GameController.getInstance().getRoot().showShopScene();
            }
            else if (showEnterSell) {
                GameController.getInstance().getRoot().showSellScene();
            }
            else if (showEnterUfo){
                GameController.getInstance().startCretaceousExploration();
            }
        }

        // Reset when key released
        if (!keyboard.isFPressed()) {
            fWasPressed = false;
        }
    }

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
        gc.setTextBaseline(VPos.CENTER);   // 🔥 ตัวแก้ปัญหาจริง

        double padding = 25;
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

        // background
        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        // border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 25, 25);

        gc.setFill(Color.WHITE);

        // 🔥 คำนวณตำแหน่งกลางจริง
        double centerY = boxY + boxHeight / 2;

        for (int i = 0; i < lines.length; i++) {

            double yOffset = (i - (lines.length - 1) / 2.0) * lineHeight;

            gc.fillText(
                    lines[i],
                    boxX + boxWidth / 2,
                    centerY + yOffset
            );
        }
    }
}
