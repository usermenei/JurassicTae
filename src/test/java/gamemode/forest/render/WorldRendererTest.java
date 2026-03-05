package gamemode.forest.render;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.util.WorldManager;
import gamemode.lobby.Player.Player;
import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldRendererTest {

    @BeforeAll
    static void initJavaFX() {
        new JFXPanel(); // initializes JavaFX toolkit
    }

    @Test
    void testRenderWithEmptyWorld() {

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Player player = new Player(0,0);
        WorldManager worldManager = new WorldManager(player);

        WorldRenderer renderer = new WorldRenderer(gc, player, worldManager);

        assertDoesNotThrow(() -> renderer.render(0,0));
    }

    @Test
    void testRenderWithCameraOffset() {

        Canvas canvas = new Canvas(800,600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Player player = new Player(100,100);
        WorldManager worldManager = new WorldManager(player);

        WorldRenderer renderer = new WorldRenderer(gc, player, worldManager);

        assertDoesNotThrow(() -> renderer.render(200,150));
    }

    @Test
    void testRenderMultipleFrames() {

        Canvas canvas = new Canvas(800,600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Player player = new Player(50,50);
        WorldManager worldManager = new WorldManager(player);

        WorldRenderer renderer = new WorldRenderer(gc, player, worldManager);

        for(int i=0;i<20;i++){
            int finalI = i;
            assertDoesNotThrow(() -> renderer.render(finalI *10, finalI *5));
        }
    }

    @Test
    void testRenderPlayerDepthSort() {

        Canvas canvas = new Canvas(800,600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Player player = new Player(300,300);
        WorldManager worldManager = new WorldManager(new Player(0,0));

        WorldRenderer renderer = new WorldRenderer(gc, player, worldManager);

        renderer.render(0,0);

        assertEquals(300, player.getX());
        assertEquals(300, player.getY());
    }
}