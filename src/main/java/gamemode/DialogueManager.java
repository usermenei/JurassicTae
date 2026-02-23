package gamemode;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DialogueManager {

    private static DialogueManager instance;

    private String fullText = "";
    private String visibleText = "";
    private String speaker = "System";

    private Image portrait;

    private boolean active = false;

    private long lastCharTime = 0;
    private int charIndex = 0;

    private final long TYPE_SPEED = 25;
    private final long DISPLAY_DURATION = 0; // 0 = no auto close

    private Font pixelFont;

    private DialogueManager() {
        pixelFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),
                20
        );
    }

    public static DialogueManager getInstance() {
        if (instance == null) {
            instance = new DialogueManager();
        }
        return instance;
    }

    // 🔥 Show dialogue with portrait
    public void showDialogue(String speakerName, String text, String portraitPath) {

        this.speaker = speakerName;
        this.fullText = text;
        this.visibleText = "";
        this.charIndex = 0;
        this.lastCharTime = System.currentTimeMillis();
        this.active = true;

        if (portraitPath != null) {
            portrait = new Image(getClass().getResource(portraitPath).toExternalForm());
        }
    }

    public void update() {

        if (!active) return;

        long now = System.currentTimeMillis();

        if (charIndex < fullText.length()) {
            if (now - lastCharTime > TYPE_SPEED) {
                visibleText += fullText.charAt(charIndex);
                charIndex++;
                lastCharTime = now;
            }
        }
    }

    public void onClick() {

        if (!active) return;

        if (charIndex < fullText.length()) {
            visibleText = fullText;
            charIndex = fullText.length();
        } else {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {

        if (!active) return;

        double boxHeight = 180;
        double boxY = canvasHeight - boxHeight - 20;

        // Dark background
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRoundRect(220, boxY, canvasWidth - 240, boxHeight, 20, 20);

        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(220, boxY, canvasWidth - 240, boxHeight, 20, 20);

        // Portrait
        if (portrait != null) {
            gc.drawImage(portrait, 0, boxY - 20, 200, 200);
        }

        // Speaker Name
        gc.setFill(Color.CYAN);
        gc.setFont(pixelFont);
        gc.fillText(speaker, 240, boxY + 35);

        // Dialogue Text
        gc.setFill(Color.WHITE);
        gc.setFont(pixelFont);

        drawWrappedText(gc, visibleText, 240, boxY + 70, canvasWidth - 260);
    }

    private void drawWrappedText(GraphicsContext gc, String text,
                                 double x, double y, double maxWidth) {

        String[] words = text.split(" ");
        String line = "";
        double lineHeight = 26;

        for (String word : words) {

            String testLine = line + word + " ";

            Text helper = new Text(testLine);
            helper.setFont(pixelFont);
            double width = helper.getLayoutBounds().getWidth();

            if (width > maxWidth) {
                gc.fillText(line, x, y);
                line = word + " ";
                y += lineHeight;
            } else {
                line = testLine;
            }
        }

        gc.fillText(line, x, y);
    }
}