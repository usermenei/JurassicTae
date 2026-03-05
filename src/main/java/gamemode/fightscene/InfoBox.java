package gamemode.fightscene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.InputStream;

/**
 * A styled JavaFX {@link VBox} that displays a character's name and HP bar
 * inside the battle scene.
 *
 * <p>The box shows three rows:
 * <ol>
 *   <li>Character name in gold pixel font.</li>
 *   <li>An "HP" tag followed by a colour-coded {@link ProgressBar}.</li>
 *   <li>A numeric HP label ({@code currentHp / maxHp}) right-aligned.</li>
 * </ol>
 *
 * <h2>Bar colour thresholds</h2>
 * <pre>
 *   HP &gt; 50 %  →  green   (#4caf50)
 *   HP &gt; 25 %  →  yellow  (#ffc107)
 *   HP ≤ 25 %  →  red     (#f44336)
 * </pre>
 *
 * <h2>Font</h2>
 * <p>Attempts to load {@code /fonts/pixel.ttf} from the classpath at size 14.
 * Falls back to {@code Monospaced 14} if the file is missing.</p>
 *
 * @see BattleView
 */
public class InfoBox extends VBox {

    /** The progress bar that visually represents remaining HP. */
    private final ProgressBar hpBar;

    /** Numeric label showing "{@code currentHp / maxHp}". */
    private final Label hpLabel;

    /** Maximum HP value; updated when {@link #setHp(int, int)} is called. */
    private int maxHp;

    /** Current HP value, clamped to {@code [0, maxHp]}. */
    private int currentHp;

    /** Pixel font loaded from {@code /fonts/pixel.ttf}; falls back to Monospaced. */
    private Font pixelFont;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Constructs an {@code InfoBox} for the given character name and maximum HP.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Stores {@code maxHp} and sets {@code currentHp = maxHp} (full HP).</li>
     *   <li>Loads the pixel font via {@link #loadFont()}.</li>
     *   <li>Creates the name label, HP bar, HP tag, and numeric HP label.</li>
     *   <li>Arranges them in two {@link HBox} rows inside this {@link VBox}.</li>
     *   <li>Applies a dark semi-transparent background with a gold border.</li>
     * </ol>
     *
     * @param name  the character name shown at the top of the box
     * @param maxHp the maximum (and initial) HP value
     */
    public InfoBox(String name, int maxHp) {

        this.maxHp     = maxHp;
        this.currentHp = maxHp;

        loadFont();

        // Name label
        Label nameLabel = new Label(name);
        nameLabel.setFont(pixelFont);
        nameLabel.setStyle("-fx-text-fill: #d4af37;");

        // HP bar
        hpBar = new ProgressBar(1.0);
        hpBar.setPrefWidth(180);
        hpBar.setPrefHeight(14);
        hpBar.setStyle("-fx-accent: #4caf50;");

        // Numeric HP label
        hpLabel = new Label(maxHp + " / " + maxHp);
        hpLabel.setFont(pixelFont);
        hpLabel.setStyle("-fx-text-fill: white;");

        // "HP" tag
        Label hpTag = new Label("HP");
        hpTag.setFont(pixelFont);
        hpTag.setStyle("-fx-text-fill: #aaaaaa;");

        HBox barRow = new HBox(8, hpTag, hpBar);
        barRow.setAlignment(Pos.CENTER_LEFT);

        HBox numRow = new HBox();
        numRow.setAlignment(Pos.CENTER_RIGHT);
        numRow.setPrefWidth(180 + 8 + hpTag.getPrefWidth());
        numRow.getChildren().add(hpLabel);

        setPadding(new Insets(10, 14, 10, 14));
        setSpacing(4);
        setAlignment(Pos.CENTER_LEFT);
        setStyle("""
            -fx-background-color: rgba(20,20,20,0.85);
            -fx-background-radius: 10;
            -fx-border-color: #d4af37;
            -fx-border-width: 2;
            -fx-border-radius: 10;
        """);

        getChildren().addAll(nameLabel, barRow, numRow);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Attempts to load the pixel font from {@code /fonts/pixel.ttf} at size 14.
     * Falls back to {@code Monospaced 14} if the resource is missing or
     * cannot be read, ensuring the UI never crashes due to a missing font file.
     */
    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/pixel.ttf");
            pixelFont = (is != null) ? Font.loadFont(is, 14) : null;
        } catch (Exception ignored) {}
        if (pixelFont == null) {
            pixelFont = Font.font("Monospaced", 14);
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Updates the HP display using the current {@link #maxHp}.
     *
     * <p>The following updates are applied:
     * <ul>
     *   <li>{@link #currentHp} is clamped to {@code max(0, hp)}.</li>
     *   <li>The progress bar fraction is recalculated as {@code currentHp / maxHp}.</li>
     *   <li>The numeric label is updated to "{@code currentHp / maxHp}".</li>
     *   <li>CSS is forcibly re-applied so the bar colour change is immediate.</li>
     *   <li>Bar colour is updated according to the thresholds in the class doc.</li>
     * </ul>
     *
     * @param hp the new current HP value (negative values are treated as 0)
     */
    public void setHp(int hp) {
        this.currentHp = Math.max(0, hp);
        double progress = (double) currentHp / maxHp;

        hpBar.setProgress(progress);
        hpLabel.setText(currentHp + " / " + maxHp);

        hpBar.applyCss();
        hpBar.layout();

        if (progress > 0.5) {
            hpBar.setStyle("-fx-accent: #4caf50;"); // green
        } else if (progress > 0.25) {
            hpBar.setStyle("-fx-accent: #ffc107;"); // yellow
        } else {
            hpBar.setStyle("-fx-accent: #f44336;"); // red
        }
    }

    /**
     * Updates both the maximum HP and the current HP display.
     *
     * <p>Use this overload when the max HP may have changed (e.g. after a level-up
     * or when switching between characters). Internally updates {@link #maxHp}
     * then delegates to {@link #setHp(int)}.</p>
     *
     * @param hp  the new current HP value (negative values are treated as 0)
     * @param max the new maximum HP value
     */
    public void setHp(int hp, int max) {
        this.maxHp = max;
        setHp(hp);
    }
}