package gym_minigame.utility;

import javafx.scene.text.Text;

public class ScoreManager {

    private int score = 0;
    private final Text scoreText;

    public ScoreManager(Text scoreText) {
        this.scoreText = scoreText;
        updateText();
    }

    public void addScore(int baseAmount, double multiplier) {
        int finalAmount = (int) (baseAmount * multiplier);
        score += finalAmount;
        updateText();
    }

    public void subtractScore(int amount) {
        score -= amount;
        if (score < 0) score = 0;
        updateText();
    }

    public int getScore() {
        return score;
    }

    private void updateText() {
        scoreText.setText("Score : " + score);
    }
}