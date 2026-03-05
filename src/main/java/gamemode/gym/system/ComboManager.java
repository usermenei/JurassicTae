package gamemode.gym.system;

import javafx.scene.text.Text;

/**
 * Manages the combo counter and score multiplier for the gym rhythm mini-game.
 * <p>
 * The combo increments by one on each successful note hit and resets to zero
 * on a miss. The multiplier scales linearly with the combo count, increasing
 * by 1.0 for every 25 consecutive hits (e.g. 25 combo = x2.0, 50 = x3.0).
 * Both values are reflected live in the provided {@link Text} nodes.
 * </p>
 */
public class ComboManager {

    /** The current consecutive hit count. Resets to 0 on a miss. */
    private int combo = 0;

    /** The current score multiplier, derived from the combo count. */
    private double multiplier = 1.0;

    /** The UI text node displaying the current combo count. */
    private final Text comboText;

    /** The UI text node displaying the current score multiplier. */
    private final Text multiplierText;

    /**
     * Constructs a {@code ComboManager} and immediately updates both text nodes
     * to reflect the initial state (combo 0, multiplier x1.00).
     *
     * @param comboText      the {@link Text} node to display the combo count
     * @param multiplierText the {@link Text} node to display the score multiplier
     */
    public ComboManager(Text comboText, Text multiplierText) {
        this.comboText = comboText;
        this.multiplierText = multiplierText;
        updateText();
    }

    /**
     * Increments the combo by one, recalculates the multiplier, and updates
     * both UI text nodes. Should be called on each successful note hit.
     */
    public void addCombo() {
        combo++;
        updateMultiplier();
        updateText();
    }

    /**
     * Resets the combo to zero and the multiplier to x1.0, then updates
     * both UI text nodes. Should be called when a note is missed.
     */
    public void resetCombo() {
        combo = 0;
        multiplier = 1.0;
        updateText();
    }

    /**
     * Recalculates the score multiplier based on the current combo.
     * The multiplier increases by 1.0 for every 25 consecutive hits:
     * {@code multiplier = 1.0 + (combo / 25.0)}.
     */
    private void updateMultiplier() {
        multiplier = 1.0 + (combo / 25.0);
    }

    /**
     * Refreshes both UI text nodes to reflect the current combo and multiplier.
     */
    private void updateText() {
        comboText.setText("Combo : " + combo);
        multiplierText.setText("x" + String.format("%.2f", multiplier));
    }

    /**
     * Returns the current score multiplier.
     *
     * @return the multiplier (always &gt;= 1.0)
     */
    public double getMultiplier() {
        return multiplier;
    }
}