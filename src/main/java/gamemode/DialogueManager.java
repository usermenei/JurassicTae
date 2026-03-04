package gamemode;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.Queue;

/**
 * DialogueManager is a singleton class responsible for handling
 * dialogue rendering, typewriter animation, and dialogue queuing
 * inside the game.
 *
 * <p>
 * Features:
 * <ul>
 *     <li>Typewriter text animation</li>
 *     <li>Portrait rendering</li>
 *     <li>Dialogue queue system</li>
 *     <li>Click-to-skip or advance</li>
 * </ul>
 * </p>
 *
 * Usage:
 * Call {@link #update()} every frame before {@link #render(GraphicsContext, double, double)}.
 * Trigger {@link #onClick()} when user clicks or presses skip key.
 *
 * This class follows Singleton pattern.
 */
public class DialogueManager {

    private static DialogueManager instance;

    /** Full dialogue text */
    private String fullText = "";

    /** Currently visible (typed) text */
    private String visibleText = "";

    /** Speaker name */
    private String speaker = "System";

    /** Portrait image */
    private Image portrait;

    /** Whether dialogue is currently active */
    private boolean active = false;

    /** Timestamp of last character typed */
    private long lastCharTime = 0;

    /** Current character index for typewriter effect */
    private int charIndex = 0;

    /** Milliseconds between each character appearance */
    private final long TYPE_SPEED = 25;

    /** Font used for dialogue text */
    private Font pixelFont;

    /** Queue for chained dialogues */
    private final Queue<DialogueEntry> queue = new LinkedList<>();

    /**
     * Internal record storing dialogue entry information.
     *
     * @param speaker Speaker name
     * @param text Dialogue text
     * @param portraitPath Path to portrait image resource
     */
    private record DialogueEntry(String speaker, String text, String portraitPath) {}

    /**
     * Private constructor for Singleton pattern.
     * Loads pixel font resource.
     */
    private DialogueManager() {
        pixelFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/pixel.ttf"),
                20
        );
    }

    /**
     * Returns the singleton instance of DialogueManager.
     *
     * @return DialogueManager instance
     */
    public static DialogueManager getInstance() {
        if (instance == null) {
            instance = new DialogueManager();
        }
        return instance;
    }

    /**
     * Shows a single dialogue immediately.
     * Clears any previously queued dialogues.
     *
     * @param speakerName Speaker name
     * @param text Dialogue text
     * @param portraitPath Portrait image path (nullable)
     */
    public void showDialogue(String speakerName, String text, String portraitPath) {
        queue.clear();
        loadEntry(new DialogueEntry(speakerName, text, portraitPath));
    }

    /**
     * Adds dialogue to queue.
     * If no dialogue is currently active, it starts immediately.
     *
     * @param speakerName Speaker name
     * @param text Dialogue text
     * @param portraitPath Portrait image path (nullable)
     */
    public void queueDialogue(String speakerName, String text, String portraitPath) {
        queue.add(new DialogueEntry(speakerName, text, portraitPath));
        if (!active) {
            playNext();
        }
    }

    /**
     * Plays next dialogue in queue.
     */
    private void playNext() {
        if (queue.isEmpty()) {
            active = false;
            return;
        }
        loadEntry(queue.poll());
    }

    /**
     * Loads a dialogue entry and resets typing state.
     *
     * @param entry Dialogue entry to load
     */
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

    /**
     * Updates typewriter animation.
     * Should be called every frame.
     */
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

    /**
     * Handles click or skip input.
     * If text not fully shown → instantly reveal.
     * If fully shown → advance to next dialogue.
     */
    public void onClick() {
        if (!active) return;

        if (charIndex < fullText.length()) {
            visibleText = fullText;
            charIndex = fullText.length();
        } else {
            playNext();
        }
    }

    /**
     * Returns whether dialogue is currently active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Closes dialogue and clears queue.
     */
    public void close() {
        active = false;
        queue.clear();
    }

    /**
     * Renders dialogue UI.
     *
     * @param gc GraphicsContext
     * @param canvasWidth canvas width
     * @param canvasHeight canvas height
     */
    public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        if (!active) return;

        double boxHeight = 180;
        double boxY = canvasHeight - boxHeight - 20;

        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRoundRect(210, boxY, canvasWidth - 230, boxHeight, 20, 20);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(210, boxY, canvasWidth - 230, boxHeight, 20, 20);

        if (portrait != null) {
            gc.drawImage(portrait, 5, boxY - 10, 190, 190);
        }

        gc.setFill(Color.CYAN);
        gc.setFont(pixelFont);
        gc.fillText(speaker, 270, boxY + 35);

        gc.setFill(Color.WHITE);
        gc.setFont(pixelFont);
        drawWrappedText(gc, visibleText, 270, boxY + 70, canvasWidth - 290);
    }

    /**
     * Draws wrapped text inside dialogue box.
     */
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