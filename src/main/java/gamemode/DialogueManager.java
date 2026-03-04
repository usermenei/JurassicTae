package gamemode;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class DialogueManager {

    private static DialogueManager instance;

    // ===== Single dialogue state =====
    private String fullText = "";
    private String visibleText = "";
    private String speaker = "System";
    private Image portrait;
    private boolean active = false;
    private long lastCharTime = 0;
    private int charIndex = 0;

    private final long TYPE_SPEED = 25;

    private Font pixelFont;

    // ===== Queue for chained dialogues =====
    private final Queue<DialogueEntry> queue = new LinkedList<>();

    private record DialogueEntry(String speaker, String text, String portraitPath) {}

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

    // ===== Show single dialogue =====
    public void showDialogue(String speakerName, String text, String portraitPath) {
        queue.clear(); // clear any pending queue
        loadEntry(new DialogueEntry(speakerName, text, portraitPath));
    }

    // ===== Queue multiple dialogues =====
    public void queueDialogue(String speakerName, String text, String portraitPath) {
        queue.add(new DialogueEntry(speakerName, text, portraitPath));
        if (!active) {
            playNext();
        }
    }

    private void playNext() {
        if (queue.isEmpty()) {
            active = false;
            return;
        }
        loadEntry(queue.poll());
    }

    private void loadEntry(DialogueEntry entry) {
        this.speaker = entry.speaker();
        this.fullText = entry.text();
        this.visibleText = "";
        this.charIndex = 0;
        this.lastCharTime = System.currentTimeMillis();
        this.active = true;

        if (entry.portraitPath() != null) {
            portrait = new Image(
                    getClass().getResource(entry.portraitPath()).toExternalForm()
            );
        } else {
            portrait = null;
        }
    }

    // ===== Update (call every frame) =====
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

    // ===== Click to skip / advance =====
    public void onClick() {
        if (!active) return;

        if (charIndex < fullText.length()) {
            // Skip typewriter — show full text immediately
            visibleText = fullText;
            charIndex = fullText.length();
        } else {
            // Advance to next queued dialogue or close
            playNext();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void close() {
        active = false;
        queue.clear();
    }

    // ===== Render (call every frame after update) =====
    public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        if (!active) return;

        double boxHeight = 180;
        double boxY = canvasHeight - boxHeight - 20;

        // Dark background — shifted right to clear portrait
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRoundRect(210, boxY, canvasWidth - 230, boxHeight, 20, 20);

        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(210, boxY, canvasWidth - 230, boxHeight, 20, 20);

        // Portrait — sits to the left of the box
        if (portrait != null) {
            gc.drawImage(portrait, 5, boxY - 10, 190, 190);
        }

        // Speaker name
        gc.setFill(Color.CYAN);
        gc.setFont(pixelFont);
        gc.fillText(speaker, 270, boxY + 35);

        // Dialogue text
        gc.setFill(Color.WHITE);
        gc.setFont(pixelFont);
        drawWrappedText(gc, visibleText, 270, boxY + 70, canvasWidth - 290);

        // "Click or F to skip" hint — only when text is fully shown
        if (charIndex >= fullText.length()) {
            gc.setFill(Color.rgb(255, 255, 255, 0.5));
            gc.setFont(Font.font("Monospaced", 13));
            gc.fillText("▶ Click or F to skip", canvasWidth - 230, boxY + boxHeight - 15);
        }
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