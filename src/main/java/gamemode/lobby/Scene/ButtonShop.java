package gamemode.lobby.Scene;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.GameLogic;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A custom JavaFX {@link Button} representing a purchasable item in the shop.
 *
 * <p>Displays the item's image, name, a stat label, and buy price in a styled
 * card layout. When clicked, it calls {@code player.buyItem(item)} and refreshes
 * the money display.</p>
 *
 * <h2>Visual Behaviour</h2>
 * <ul>
 *   <li>Default state: dark-grey background with a subtle grey border.</li>
 *   <li>Hover state: slightly lighter background with a gold border.</li>
 * </ul>
 *
 * <p><strong>Precondition:</strong> {@code item} must implement {@link Buyable};
 * otherwise a {@link ClassCastException} is thrown when the buy price is read.</p>
 *
 * @see Item
 * @see Buyable
 * @see GameLogic
 * @see GameController
 */
public class ButtonShop extends Button {

    /**
     * CSS style applied to the button in its idle (non-hovered) state.
     * Renders a dark-grey card with a subtle grey border.
     */
    private static final String STYLE_DEFAULT = """
            -fx-background-color: #4a4a4a;
            -fx-background-radius: 12;
            -fx-border-color: #777;
            -fx-font-family: 'Minecraft';
            -fx-border-radius: 12;
            """;

    /**
     * CSS style applied to the button when the mouse hovers over it.
     * Renders a slightly lighter card with a gold border to indicate interactivity.
     */
    private static final String STYLE_HOVER = """
            -fx-background-color: #5a5a5a;
            -fx-background-radius: 12;
            -fx-font-family: 'Minecraft';
            -fx-border-color: gold;
            -fx-border-radius: 12;
            """;

    /**
     * Constructs a {@code ButtonShop} for the specified {@code item}.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Sets preferred size to 120 × 150 pixels.</li>
     *   <li>Applies the default dark-grey card style.</li>
     *   <li>Loads the item image via {@link Item#getImg()} and displays it at 70 × 70 px.</li>
     *   <li>Renders the item name on a grey background label.</li>
     *   <li>Renders the item description from {@link Buyable#getDescription()}.</li>
     *   <li>Renders the buy price in gold-coloured text.</li>
     *   <li>Registers hover-enter / hover-exit handlers to swap CSS styles.</li>
     *   <li>Registers an {@code onAction} handler that:
     *     <ul>
     *       <li>Retrieves the current {@link Player} from {@link GameLogic}.</li>
     *       <li>Calls {@code player.buyItem(item)} to add the item to inventory
     *           and deduct the buy price from the player's gold.</li>
     *       <li>Calls {@link GameController#reloadMoney()} to refresh the HUD.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param item the {@link Item} to purchase; must also implement {@link Buyable}
     * @throws ClassCastException   if {@code item} does not implement {@link Buyable}
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public ButtonShop(Item item) {

        this.setPrefSize(120, 150);
        this.setStyle(STYLE_DEFAULT);

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // Item image
        Image img = item.getImg();
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);

        // Item name label
        Label nameLabel = new Label(item.getName());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPadding(new Insets(3, 8, 3, 8));
        nameLabel.setBackground(new Background(
                new BackgroundFill(Color.rgb(80, 80, 80), new CornerRadii(6), Insets.EMPTY)
        ));

        // Stat label
        Label statLabel = new Label(((Buyable) item).getDescription());
        statLabel.setTextFill(Color.LIGHTGRAY);
        statLabel.setStyle("-fx-font-size: 11px;");

        // Buy price label
        Label priceLabel = new Label("Price: " + ((Buyable) item).getBuyPrice() + "$");
        priceLabel.setTextFill(Color.GOLD);
        priceLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        box.getChildren().addAll(imageView, nameLabel, statLabel, priceLabel);
        this.setGraphic(box);

        // Hover effects
        this.setOnMouseEntered(e -> this.setStyle(STYLE_HOVER));
        this.setOnMouseExited(e -> this.setStyle(STYLE_DEFAULT));

        // Buy action
        this.setOnAction(e -> {
            Player player = GameLogic.getInstance().getPlayer();
            player.buyItem(item);
            GameController.getInstance().reloadMoney();
            System.out.println("Buy " + item.getName());
        });
    }
}