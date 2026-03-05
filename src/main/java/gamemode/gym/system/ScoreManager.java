package gamemode.gym.system;

import javafx.scene.text.Text;

/**
 * Manages the player's score during a gym rhythm mini-game session.
 * <p>
 * Score is increased on successful note hits (scaled by the current combo
 * multiplier) and decreased by a flat penalty on misses. The score is
 * clamped to a minimum of 0 and reflected live in the provided {@link Text} node.
 * </p>
 */
public class ScoreManager {

    /** The current score. Always &gt;= 0. */
    private int score = 0;

    /** The UI text node displaying the current score. */
    private final Text scoreText;

    /**
     * Constructs a {@code ScoreManager} and immediately updates the text node
     * to reflect the initial score of 0.
     *
     * @param scoreText the {@link Text} node to display the current score
     */
    public ScoreManager(Text scoreText) {
        this.scoreText = scoreText;
        updateText();
    }

    /**
     * Adds to the score by multiplying a base amount by the given multiplier,
     * then updates the UI text node.
     *
     * @param baseAmount the base score value before applying the multiplier
     * @param multiplier the combo multiplier to scale the base amount by
     */
    public void addScore(int baseAmount, double multiplier) {
        int finalAmount = (int) (baseAmount * multiplier);
        score += finalAmount;
        updateText();
    }

    /**
     * Subtracts a flat penalty from the score, clamped to a minimum of 0,
     * then updates the UI text node.
     *
     * @param amount the penalty amount to subtract (should be &gt;= 0)
     */
    public void subtractScore(int amount) {
        score -= amount;
        if (score < 0) score = 0;
        updateText();
    }

    /**
     * Returns the current score.
     *
     * @return the score (always &gt;= 0)
     */
    public int getScore() {
        return score;
    }

    /**
     * Refreshes the UI text node to reflect the current score.
     */
    private void updateText() {
        scoreText.setText("Score : " + score);
    }
}