package gamemode.lobby.Scene;

import gamemode.forest.entity.Dinosaur;
import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.TamedDinosaur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.GameLogic;

/**
 * A custom JavaFX {@link Button} that represents a sell action for a given {@link Item}.
 *
 * <p>This button displays the item's image, name, and sell price in a styled card layout.
 * When clicked, it sells the item from the player's inventory, updates the sell scene,
 * refreshes the money display, and — if the item is a {@link TamedDinosaur} — returns
 * the dinosaur to the shop catalog.</p>
 *
 * <h2>Visual Behaviour</h2>
 * <ul>
 *   <li>Default state: dark-grey background with a subtle grey border.</li>
 *   <li>Hover state: slightly lighter background with a gold border.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * Item myItem = ...; // any Item that also implements Sellable
 * ButtonSell sellBtn = new ButtonSell(myItem);
 * somePane.getChildren().add(sellBtn);
 * }</pre>
 *
 * <p><strong>Precondition:</strong> {@code item} must implement {@link Sellable}; otherwise
 * a {@link ClassCastException} is thrown during construction when the sell price is read.</p>
 *
 * @see Item
 * @see Sellable
 * @see TamedDinosaur
 * @see GameLogic
 * @see GameController
 */
public class ButtonSell extends Button {

    // -----------------------------------------------------------------------
    // CSS style constants
    // -----------------------------------------------------------------------

    /** Base (idle) CSS style applied to this button. */
    private static final String STYLE_DEFAULT = """
            -fx-background-color: #4a4a4a;
            -fx-background-radius: 12;
            -fx-font-family: 'Minecraft';
            -fx-border-color: #777;
            -fx-border-radius: 12;
            """;

    /** Hover CSS style applied when the mouse enters the button area. */
    private static final String STYLE_HOVER = """
            -fx-background-color: #5a5a5a;
            -fx-background-radius: 12;
            -fx-font-family: 'Minecraft';
            -fx-border-color: gold;
            -fx-border-radius: 12;
            """;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Constructs a {@code ButtonSell} for the specified {@code item}.
     *
     * <p>The constructor performs the following steps:
     * <ol>
     *   <li>Sets the preferred button size to 140 × 130 pixels.</li>
     *   <li>Applies the default dark-grey card style.</li>
     *   <li>Loads and displays the item's image at 70 × 70 pixels.</li>
     *   <li>Renders the item name on a slightly lighter grey background label.</li>
     *   <li>Renders the sell price in gold-coloured text.</li>
     *   <li>Registers hover-enter / hover-exit handlers to swap CSS styles.</li>
     *   <li>Registers an {@code onAction} handler that:
     *     <ul>
     *       <li>Calls {@link GameLogic#getPlayer()}.sellItem(item) to remove the item
     *           from the player's inventory and credit the player with the sell price.</li>
     *       <li>If the item is a {@link TamedDinosaur}, re-adds the underlying
     *           {@link Dinosaur} to the shop's sell catalog.</li>
     *       <li>Reloads the sell scene and money display via {@link GameController}.</li>
     *       <li>Refreshes the shop's sell tab.</li>
     *     </ul>
     *   </li>
     * </ol>
     * </p>
     *
     * @param item the {@link Item} to sell; must also implement {@link Sellable}
     * @throws ClassCastException   if {@code item} does not implement {@link Sellable}
     * @throws NullPointerException if {@code item} is {@code null}, or if
     *                              {@link Item#getImgUrl()} returns a path that cannot
     *                              be resolved as a resource
     */
    public ButtonSell(Item item) {

        /* ── Size ── */
        this.setPrefSize(140, 130);

        /* ── Default style ── */
        this.setStyle(STYLE_DEFAULT);

        /* ── Content container ── */
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        /* ── Item image ── */
        Image img = new Image(
                getClass().getResource(item.getImgUrl()).toExternalForm()
        );
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);

        /* ── Item name label ── */
        Label nameLabel = new Label(item.getName());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPadding(new Insets(3, 8, 3, 8));
        nameLabel.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(80, 80, 80),
                        new CornerRadii(6),
                        Insets.EMPTY
                )
        ));

        /* ── Sell-price label ── */
        Label priceLabel = new Label("Price: " + ((Sellable) item).getSellPrice() + "$");
        priceLabel.setTextFill(Color.GOLD);
        priceLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        box.getChildren().addAll(imageView, nameLabel, priceLabel);
        this.setGraphic(box);

        /* ── Hover effects ── */
        this.setOnMouseEntered(e -> this.setStyle(STYLE_HOVER));
        this.setOnMouseExited(e -> this.setStyle(STYLE_DEFAULT));

        /* ── Sell action ── */
        this.setOnAction(e -> {
            // Remove item from inventory and credit the sell price to the player.
            GameLogic.getInstance().getPlayer().sellItem(item);

            // If the sold item was a tamed dinosaur, return it to the catalog.
            if (item instanceof TamedDinosaur) {
                GameController.getInstance()
                        .getRoot()
                        .getSellScene()
                        .addCatalog((Dinosaur) ((TamedDinosaur) item).getDinosaur());
            }

            // Refresh UI components.
            GameController.getInstance().reloadSellScene();
            GameController.getInstance().reloadMoney();
            GameController.getInstance().getRoot().getShopScene().loadSell();

            System.out.println("Sell " + item.getName());
        });
    }
}