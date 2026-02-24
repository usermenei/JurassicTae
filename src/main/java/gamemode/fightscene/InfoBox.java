package gamemode.fightscene;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class InfoBox extends VBox {

    private final ProgressBar hpBar;
    private int maxHp;

    public InfoBox(String name, int maxHp) {

        this.maxHp = maxHp;

        Label title = new Label(name);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        hpBar = new ProgressBar(1.0);
        hpBar.setPrefWidth(160);
        hpBar.setStyle("""
            -fx-accent: #4caf50;
        """);

        setPadding(new Insets(10));
        setSpacing(6);
        setStyle("""
            -fx-background-color: #1e1e1e;
            -fx-background-radius: 12;
        """);

        getChildren().addAll(title, hpBar);
    }

    public void setHp(int hp) {
        double progress = Math.max(0, (double) hp / maxHp);
        hpBar.setProgress(progress);

        if (progress > 0.5) {
            hpBar.setStyle("-fx-accent: #4caf50;"); // green
        } else if (progress > 0.25) {
            hpBar.setStyle("-fx-accent: #ffc107;"); // yellow
        } else {
            hpBar.setStyle("-fx-accent: #f44336;"); // red
        }
    }
}