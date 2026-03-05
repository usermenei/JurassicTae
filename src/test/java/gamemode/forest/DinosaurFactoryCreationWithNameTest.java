package gamemode.forest;

import gamemode.forest.entity.Dinosaur;
import gamemode.forest.entity.DinosaurFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DinosaurFactoryCreationWithNameTest {

    @Test
    void testCreateLongNeck() {
        Dinosaur dino = DinosaurFactory.createHerbivore("LongNeck", 100, 200);

        assertNotNull(dino);
        assertEquals("Long Neck", dino.getName());
        assertEquals(150, dino.getHp());
        assertEquals(10, dino.getStrength());
        assertEquals(1, dino.getRequiredLevel());
        assertEquals(Dinosaur.Rarity.COMMON, dino.getRarity());
        assertEquals(100, dino.getX());
        assertEquals(200, dino.getY());
    }

    @Test
    void testCreateTriceratops() {
        Dinosaur dino = DinosaurFactory.createHerbivore("Triceratops", 50, 60);

        assertNotNull(dino);
        assertEquals("Triceratops", dino.getName());
        assertEquals(250, dino.getHp());
        assertEquals(20, dino.getStrength());
        assertEquals(3, dino.getRequiredLevel());
        assertEquals(Dinosaur.Rarity.UNCOMMON, dino.getRarity());
    }

    @Test
    void testCreateRaptor() {
        Dinosaur dino = DinosaurFactory.createCarnivore("Raptor", 10, 20);

        assertNotNull(dino);
        assertEquals("Raptor", dino.getName());
        assertEquals(150, dino.getHp());
        assertEquals(135, dino.getStrength());
        assertEquals(2, dino.getRequiredLevel());
        assertEquals(Dinosaur.Rarity.COMMON, dino.getRarity());
    }

    @Test
    void testCreateTRex() {
        Dinosaur dino = DinosaurFactory.createCarnivore("TRex", 5, 5);

        assertNotNull(dino);
        assertEquals("T-Rex", dino.getName());
        assertEquals(400, dino.getHp());
        assertEquals(80, dino.getStrength());
        assertEquals(5, dino.getRequiredLevel());
        assertEquals(Dinosaur.Rarity.RARE, dino.getRarity());
    }

    @Test
    void testCreateMegaDinosaur() {
        Dinosaur dino = DinosaurFactory.createMega(300, 400);

        assertNotNull(dino);
        assertEquals("Ancient Colossus", dino.getName());
        assertEquals(1500, dino.getHp());
        assertEquals(150, dino.getStrength());
        assertEquals(10, dino.getRequiredLevel());
        assertEquals(Dinosaur.Rarity.RARE, dino.getRarity());
    }

    @Test
    void testInvalidHerbivoreType() {
        Dinosaur dino = DinosaurFactory.createHerbivore("Unknown", 0, 0);

        assertNull(dino);
    }

    @Test
    void testInvalidCarnivoreType() {
        Dinosaur dino = DinosaurFactory.createCarnivore("Unknown", 0, 0);

        assertNull(dino);
    }
}