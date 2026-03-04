package gamemode.fightscene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.InputStream;

public class InfoBox extends VBox {

    private final ProgressBar hpBar;
    private final Label hpLabel;
    private int maxHp;
    private int currentHp;
    private Font pixelFont;

    public InfoBox(String name, int maxHp) {

        this.maxHp = maxHp;
        this.currentHp = maxHp;

        loadFont();

        // ===== NAME LABEL =====
        Label nameLabel = new Label(name);
        nameLabel.setFont(pixelFont);
        nameLabel.setStyle("-fx-text-fill: #d4af37;");

        // ===== HP BAR + HP TEXT ROW =====
        hpBar = new ProgressBar(1.0);
        hpBar.setPrefWidth(180);
        hpBar.setPrefHeight(14);
        hpBar.setStyle("-fx-accent: #4caf50;");

        hpLabel = new Label(maxHp + " / " + maxHp);
        hpLabel.setFont(pixelFont);
        hpLabel.setStyle("-fx-text-fill: white;");

        // "HP:" tag
        Label hpTag = new Label("HP");
        hpTag.setFont(pixelFont);
        hpTag.setStyle("-fx-text-fill: #aaaaaa;");

        HBox barRow = new HBox(8, hpTag, hpBar);
        barRow.setAlignment(Pos.CENTER_LEFT);

        HBox numRow = new HBox();
        numRow.setAlignment(Pos.CENTER_RIGHT);
        numRow.setPrefWidth(180 + 8 + hpTag.getPrefWidth());
        numRow.getChildren().add(hpLabel);

        // ===== LAYOUT =====
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

    // ===================================================

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/pixel.ttf");
            pixelFont = (is != null) ? Font.loadFont(is, 14) : null;
        } catch (Exception ignored) {}
        if (pixelFont == null) {
            pixelFont = Font.font("Monospaced", 14);
        }
    }

    // ===================================================

    public void setHp(int hp) {
        this.currentHp = Math.max(0, hp);
        double progress = (double) currentHp / maxHp;

        hpBar.setProgress(progress);
        hpLabel.setText(currentHp + " / " + maxHp);

        // Force JavaFX to actually re-render the bar
        hpBar.applyCss();
        hpBar.layout();

        if (progress > 0.5) {
            hpBar.setStyle("-fx-accent: #4caf50;");
        } else if (progress > 0.25) {
            hpBar.setStyle("-fx-accent: #ffc107;");
        } else {
            hpBar.setStyle("-fx-accent: #f44336;");
        }
    }

    public void setHp(int hp, int max) {
        this.maxHp = max;
        setHp(hp);
    }
}