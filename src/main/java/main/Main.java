package main;

import javafx.application.Platform;
import welcomescene.IntroScene;
import gamemode.lobby.Scene.SpawnScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gamemode.lobby.logic.GameController;
import gamemode.lobby.logic.GameLogic;

/**
 * Main entry point for the <b>Jurassic Tae</b> JavaFX application.
 *
 * <p>This class bootstraps the entire game by:
 * <ul>
 *   <li>Initialising the {@link GameLogic} singleton</li>
 *   <li>Creating the main {@link SpawnScreen} lobby</li>
 *   <li>Wiring up the {@link GameController} with the primary {@link Stage}</li>
 *   <li>Displaying the {@link IntroScene} as the first screen</li>
 *   <li>Ensuring all background threads are terminated on window close</li>
 * </ul>
 *
 * @author Jurassic Tae Team
 * @version 1.0
 */
public class Main extends Application {

    /**
     * JavaFX application entry point called by the framework after {@link #main(String[])} invokes {@code launch()}.
     *
     * <p>Performs the following setup steps in order:
     * <ol>
     *   <li>Initialises {@link GameLogic} so the player singleton is ready</li>
     *   <li>Constructs the {@link SpawnScreen} (lobby root node)</li>
     *   <li>Creates the main {@link Scene} at 1422 × 800 px</li>
     *   <li>Initialises {@link GameController} with the stage and scene</li>
     *   <li>Sets the stage title and switches to the {@link IntroScene}</li>
     *   <li>Registers a close-request handler that forcefully exits the JVM
     *       to prevent lingering executor threads from keeping the process alive</li>
     * </ol>
     *
     * @param stage the primary {@link Stage} provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        // Initialise game logic / player singleton before anything else
        GameLogic.getInstance();

        // Build the lobby screen and wrap it in a scene
        SpawnScreen root = new SpawnScreen();
        Scene mainScene = new Scene(root, 1422, 800);

        // Wire the controller so all subsystems share the same stage & scene
        GameController.getInstance().init(stage, mainScene);
        GameController.getInstance().setRoot(root);

        stage.setTitle("Jurassic Tae");

        // Start with the animated intro/splash screen
        stage.setScene(new IntroScene().getScene());

        // Force-kill background threads (e.g. WorldManager ExecutorService)
        // when the user closes the window, preventing the JVM from hanging
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    /**
     * Standard Java entry point.
     *
     * <p>Delegates to {@link Application#launch(String...)} which initialises
     * the JavaFX runtime and then calls {@link #start(Stage)}.
     *
     * @param args command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        launch();
    }
}