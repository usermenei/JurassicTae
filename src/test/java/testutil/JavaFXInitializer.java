package testutil;

import javafx.embed.swing.JFXPanel;

/**
 * Utility class to safely initialize JavaFX Toolkit once for tests.
 *
 * Uses JFXPanel trick to start JavaFX without
 * causing "Toolkit already initialized" errors.
 */
public final class JavaFXInitializer {

    private static boolean initialized = false;

    private JavaFXInitializer() {
        // Prevent instantiation
    }

    public static synchronized void init() {
        if (!initialized) {
            new JFXPanel(); // Initializes JavaFX toolkit safely
            initialized = true;
        }
    }
}