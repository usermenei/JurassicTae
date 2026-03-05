package gamemode.fightscene;

import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.Weapon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * The {@code CommandBox} represents the player command interface during a battle.
 *
 * <p>This component appears at the bottom of the battle screen and allows the
 * player to choose battle actions such as:</p>
 *
 * <ul>
 *     <li>Fight using a weapon</li>
 *     <li>Open the bag to use potions</li>
 *     <li>Attempt to catch the enemy</li>
 *     <li>Escape from battle</li>
 * </ul>
 *
 * <p>The UI consists of two major states:</p>
 *
 * <ol>
 *     <li>Main command menu (FIGHT / BAG / CATCH / ESCAPE)</li>
 *     <li>Dynamic item menus (Weapon selection / Potion selection)</li>
 * </ol>
 *
 * <p>Item menus support pagination when the number of items exceeds the maximum
 * number displayed per page.</p>
 *
 * <h2>Layout Structure</h2>
 *
 * <pre>
 * StackPane
 *  └── VBox (container)
 *        ├── Label messageLabel
 *        ├── GridPane mainButtons
 *        │     ├── FIGHT
 *        │     ├── BAG
 *        │     ├── CATCH
 *        │     └── ESCAPE
 *        └── VBox dynamicMenu (weapon/potion menus)
 * </pre>
 *
 * <p>The {@code dynamicMenu} replaces the main command grid whenever the player
 * enters the weapon or potion selection interface.</p>
 *
 * @author
 */
public class CommandBox extends StackPane {

    /** Maximum number of items displayed per page in item menus. */
    private static final int ITEMS_PER_PAGE = 2;

    /** Grid containing the main command buttons. */
    private final GridPane mainButtons;

    /** Container for dynamically generated weapon/potion menus. */
    private final VBox dynamicMenu;

    /** Label used to display battle messages to the player. */
    private final Label messageLabel;

    /** Button used to open the weapon selection menu. */
    private final Button fightBtn;

    /** Button used to open the potion inventory. */
    private final Button bagBtn;

    /** Button used to attempt catching the enemy. */
    private final Button catchBtn;

    /** Button used to escape from battle. */
    private final Button escapeBtn;

    /** Current page index for paginated menus. */
    private int currentPage = 0;

    /** List of potions currently displayed in the potion menu. */
    private List<Potion> currentPotions;

    /** List of weapons currently displayed in the weapon menu. */
    private List<Weapon> currentWeapons;

    /** Player name used in UI messages. */
    private final String playerName;

    /** Pixel-style font used by the UI. */
    private Font pixelFont;

    /**
     * Constructs a new {@code CommandBox}.
     *
     * <p>This initialises the battle command UI and creates the
     * four main action buttons.</p>
     *
     * @param playerName the name of the player displayed in messages
     */
    public CommandBox(String playerName) {

        this.playerName = playerName;

        setMinHeight(220);
        setPrefHeight(220);
        setMaxHeight(220);
        setPadding(new Insets(12));

        setStyle("""
            -fx-background-color: #2c3e50;
            -fx-border-color: #d4af37;
            -fx-border-width: 5;
        """);

        loadFont();

        messageLabel = new Label("What will " + playerName + " do?");
        messageLabel.setFont(pixelFont);
        messageLabel.setStyle("-fx-text-fill: white;");
        messageLabel.setAlignment(Pos.CENTER_LEFT);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        mainButtons = new GridPane();
        mainButtons.setHgap(16);
        mainButtons.setVgap(12);
        mainButtons.setAlignment(Pos.CENTER);

        fightBtn  = createButton("FIGHT");
        bagBtn    = createButton("BAG");
        catchBtn  = createButton("CATCH");
        escapeBtn = createButton("ESCAPE");

        mainButtons.add(fightBtn,0,0);
        mainButtons.add(bagBtn,1,0);
        mainButtons.add(catchBtn,0,1);
        mainButtons.add(escapeBtn,1,1);

        dynamicMenu = new VBox(10);
        dynamicMenu.setAlignment(Pos.CENTER);
        dynamicMenu.setVisible(false);
        dynamicMenu.setManaged(false);

        VBox container = new VBox(10);
        container.getChildren().addAll(messageLabel,mainButtons,dynamicMenu);

        getChildren().add(container);

        fightBtn.setOnAction(e -> setMessage("Choose a weapon."));
        bagBtn.setOnAction(e -> setMessage("Open your bag."));
        catchBtn.setOnAction(e -> setMessage("Try to catch it!"));
        escapeBtn.setOnAction(e -> setMessage("Attempting to escape..."));
    }

    /**
     * Loads the pixel-style font used by the command interface.
     *
     * <p>If the font resource cannot be found, a fallback monospaced font
     * is used instead.</p>
     */
    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/pixel.ttf");
            pixelFont = (is != null) ? Font.loadFont(is,18) : null;
        } catch(Exception ignored){}

        if(pixelFont == null){
            pixelFont = Font.font("Monospaced",18);
        }
    }

    /**
     * Displays the weapon selection menu.
     *
     * @param weapons list of available weapons
     * @param onSelect callback executed when a weapon is chosen
     */
    public void showWeaponMenu(List<Weapon> weapons, Consumer<Weapon> onSelect){
        this.currentWeapons = weapons;
        currentPage = 0;

        mainButtons.setVisible(false);
        mainButtons.setManaged(false);      // ⭐ add

        dynamicMenu.setVisible(true);
        dynamicMenu.setManaged(true);       // ⭐ add

        renderWeaponPage(onSelect);
    }

    /**
     * Renders a single page of weapon selections.
     *
     * @param onSelect callback triggered when the player selects a weapon
     */
    private void renderWeaponPage(Consumer<Weapon> onSelect){
        dynamicMenu.getChildren().clear();

        int totalItems = (currentWeapons == null ? 0 : currentWeapons.size()) + 1;
        int totalPages = Math.max(1,(int)Math.ceil((double)totalItems/ITEMS_PER_PAGE));

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE,totalItems);

        for(int i=start;i<end;i++){
            Button btn;

            if(i==0){
                btn = createButton("Bare Hand");
                btn.setOnAction(e->{clearMenu(); onSelect.accept(null);});
            }
            else{
                Weapon w = currentWeapons.get(i-1);
                btn = createButton(w.getName());
                btn.setOnAction(e->{clearMenu(); onSelect.accept(w);});
            }

            dynamicMenu.getChildren().add(btn);
        }

        dynamicMenu.getChildren().add(createNav(totalPages,()->renderWeaponPage(onSelect)));
    }

    /**
     * Displays the potion selection menu.
     *
     * @param potions list of available potions
     * @param onSelect callback executed when a potion is selected
     */
    public void showPotionMenu(List<Potion> potions, Consumer<Potion> onSelect){
        this.currentPotions = potions;
        currentPage = 0;

        mainButtons.setVisible(false);
        mainButtons.setManaged(false);      // ⭐ add

        dynamicMenu.setVisible(true);
        dynamicMenu.setManaged(true);       // ⭐ add

        renderPotionPage(onSelect);
    }

    /**
     * Renders a page of potion selections.
     *
     * @param onSelect callback executed when a potion is selected
     */
    private void renderPotionPage(Consumer<Potion> onSelect){
        dynamicMenu.getChildren().clear();

        if(currentPotions == null || currentPotions.isEmpty()){
            setMessage("No potions available.");
            Button back = createButton("BACK");
            back.setOnAction(e->clearMenu());
            dynamicMenu.getChildren().add(back);
            return;
        }

        int totalPages = Math.max(1,(int)Math.ceil((double)currentPotions.size()/ITEMS_PER_PAGE));

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE,currentPotions.size());

        for(int i=start;i<end;i++){
            Potion p = currentPotions.get(i);
            Button btn = createButton(p.getName());
            btn.setOnAction(e->{clearMenu(); onSelect.accept(p);});
            dynamicMenu.getChildren().add(btn);
        }

        dynamicMenu.getChildren().add(createNav(totalPages,()->renderPotionPage(onSelect)));
    }

    /**
     * Creates the navigation bar for paginated menus.
     *
     * @param totalPages total number of pages
     * @param refresh function used to reload the current menu page
     * @return navigation HBox containing Prev / Next / Back buttons
     */
    private HBox createNav(int totalPages,Runnable refresh){
        HBox nav = new HBox(12);
        nav.setAlignment(Pos.CENTER);

        Button prev = createButton("PREV");
        Button next = createButton("NEXT");
        Button back = createButton("BACK");

        prev.setDisable(currentPage==0);
        next.setDisable(currentPage>=totalPages-1);

        prev.setOnAction(e->{currentPage--; refresh.run();});
        next.setOnAction(e->{currentPage++; refresh.run();});
        back.setOnAction(e->clearMenu());

        nav.getChildren().addAll(prev,next,back);

        return nav;
    }

    /**
     * Creates a styled button used within the command box.
     *
     * @param text button label
     * @return styled JavaFX button
     */
    private Button createButton(String text){
        Button b = new Button(text);
        b.setPrefWidth(160);
        b.setPrefHeight(42);
        b.setFont(pixelFont);
        return b;
    }

    /**
     * Clears the dynamic menu and returns the UI to the main command menu.
     */
    private void clearMenu(){
        dynamicMenu.setVisible(false);
        dynamicMenu.setManaged(false);      // ⭐ add

        mainButtons.setVisible(true);
        mainButtons.setManaged(true);       // ⭐ add

        setMessage("What will " + playerName + " do?");
    }

    /**
     * Updates the message label displayed to the player.
     *
     * @param msg message to display
     */
    public void setMessage(String msg){
        messageLabel.setText(msg);
    }

    /** @return the Fight button */
    public Button getFightButton(){ return fightBtn; }

    /** @return the Bag button */
    public Button getBagButton(){ return bagBtn; }

    /** @return the Catch button */
    public Button getCatchButton(){ return catchBtn; }

    /** @return the Escape button */
    public Button getEscapeButton(){ return escapeBtn; }
}