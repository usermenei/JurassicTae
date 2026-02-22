package CretaceousExploration.util;

import javafx.scene.image.Image;

public class AssetLoader {

    public static Image load(String path) {

        var url = AssetLoader.class.getResource(path);

        if (url == null)
            throw new RuntimeException("Missing resource: " + path);

        return new Image(url.toExternalForm());
    }
}