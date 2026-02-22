package gym_minigame.utility;

import javafx.scene.text.Text;

public class ComboManager {

    private int combo = 0;
    private double multiplier = 1.0;

    private final Text comboText;
    private final Text multiplierText;

    public ComboManager(Text comboText, Text multiplierText) {
        this.comboText = comboText;
        this.multiplierText = multiplierText;
        updateText();
    }

    public void addCombo() {
        combo++;
        updateMultiplier();
        updateText();
    }

    public void resetCombo() {
        combo = 0;
        multiplier = 1.0;
        updateText();
    }

    private void updateMultiplier() {
        // Relative scaling
        // 25 combo = +1 multiplier
        multiplier = 1.0 + (combo / 25.0);
    }

    private void updateText() {
        comboText.setText("Combo : " + combo);
        multiplierText.setText("x" + String.format("%.2f", multiplier));
    }

    public double getMultiplier() {
        return multiplier;
    }
}