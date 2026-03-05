package gamemode.forest.entity;

import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.DinoBall;
import gamemode.lobby.Item.Potion.HealPotion;
import gamemode.lobby.Item.Potion.SpeedPotion;
import gamemode.lobby.Item.Potion.StrengthPotion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldItemTest {

    @Test
    void testPositionStoredCorrectly() {
        WorldItem item = new WorldItem(100, 200);

        assertEquals(100, item.getX());
        assertEquals(200, item.getY());
    }

    @Test
    void testNegativePosition() {
        WorldItem item = new WorldItem(-100, -50);

        assertEquals(-100, item.getX());
        assertEquals(-50, item.getY());
    }

    @Test
    void testItemGeneratedNotNull() {
        WorldItem item = new WorldItem(0, 0);

        assertNotNull(item.getItem());
    }

    @Test
    void testImageGeneratedNotNull() {
        WorldItem item = new WorldItem(0, 0);

        assertNotNull(item.getImage());
    }

    @Test
    void testGeneratedItemIsValidType() {
        WorldItem item = new WorldItem(0, 0);
        Item generated = item.getItem();

        boolean valid =
                generated instanceof HealPotion ||
                        generated instanceof SpeedPotion ||
                        generated instanceof StrengthPotion ||
                        generated instanceof DinoBall;

        assertTrue(valid);
    }

    @Test
    void testRandomItemGenerationMultipleTimes() {
        for (int i = 0; i < 50; i++) {
            WorldItem item = new WorldItem(i, i);
            assertNotNull(item.getItem());
            assertNotNull(item.getImage());
        }
    }
}