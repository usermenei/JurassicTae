package gamemode.forest.util;

import javafx.scene.image.Image;

/**
 * Utility class responsible for loading image assets from the application's resources.
 *
 * <p>This class provides a static helper method for retrieving image files
 * located in the classpath (typically inside the resources folder).
 * It ensures that the requested resource exists before creating the {@link Image}.
 *
 * <p>If the resource cannot be found, the method throws a {@link RuntimeException}
 * to prevent the application from silently failing due to missing assets.
 *
 * <p>Example usage:
 * <pre>
 * Image dinosaurImage = AssetLoader.load("/images/dinosaur.png");
 * </pre>
 *
 * @author
 */
public class AssetLoader {
    /**
     * Loads an image from the application's resource directory.
     *
     * <p>The path must be a valid classpath resource path starting with {@code /}.
     * For example: {@code "/images/player.png"}.
     *
     * @param path the classpath location of the image resource
     * @return the loaded {@link Image} object
     * @throws RuntimeException if the resource cannot be found
     */
    public static Image load(String path) {

        var url = AssetLoader.class.getResource(path);

        if (url == null)
            throw new RuntimeException("Missing resource: " + path);

        return new Image(url.toExternalForm());
    }
}