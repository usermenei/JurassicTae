package gamemode.lobby.Item.Base;

import javafx.scene.image.Image;

/**
 * Abstract base class representing a generic item in the game.
 *
 * <p><b>Overview:</b></p>
 * <ul>
 *     <li>A name</li>
 *     <li>An image resource path</li>
 *     <li>A loaded {@link Image} object for rendering</li>
 * </ul>
 *
 * <p>
 * Subclasses should extend this class to create specific
 * item types such as weapons, potions, or equipment.
 * </p>
 */
public abstract class Item {

    /** Name of the item */
    private final String name;

    /** Resource path to the item's image */
    private final String imgUrl;

    /** Loaded JavaFX image instance */
    private final Image image;

    /**
     * Constructs an Item with a name and image resource path.
     *
     * @param name   the display name of the item
     * @param imgUrl the resource path of the item's image
     *               (e.g., "/images/sword.png")
     *
     * @throws NullPointerException if the image resource cannot be found
     */
    public Item(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.image = new Image(getClass().getResource(imgUrl).toExternalForm());
    }

    /**
     * Returns the name of the item.
     *
     * @return item name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the image resource path of the item.
     *
     * @return image resource path
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * Returns the loaded JavaFX image of the item.
     *
     * @return Image object used for rendering
     */
    public Image getImg() {
        return image;
    }
}