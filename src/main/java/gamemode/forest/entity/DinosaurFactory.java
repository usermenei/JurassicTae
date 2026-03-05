package gamemode.forest.entity;

/**
 * Factory class for creating pre-configured {@link Dinosaur} instances.
 * <p>
 * Provides static factory methods for each dinosaur category
 * ({@link HerbivoreDinosaur}, {@link CarnivoreDinosaur}, {@link MegaDinosaur}),
 * with each named type having fixed stats, rarity, and sprite paths.
 * Callers only need to supply the spawn coordinates.
 * </p>
 */
public class DinosaurFactory {

    /**
     * Creates a herbivore dinosaur of the given type at the specified position.
     * <p>
     * Supported types and their stats:
     * </p>
     * <ul>
     *   <li><b>LongNeck</b> – 150 HP, 10 strength, 40 EXP, level 1, {@link Dinosaur.Rarity#COMMON}, sell price 50</li>
     *   <li><b>Triceratops</b> – 250 HP, 20 strength, 80 EXP, level 3, {@link Dinosaur.Rarity#UNCOMMON}, sell price 45</li>
     * </ul>
     *
     * @param type the dinosaur type identifier (e.g. {@code "LongNeck"}, {@code "Triceratops"})
     * @param x    the initial X position in the game world
     * @param y    the initial Y position in the game world
     * @return a configured {@link HerbivoreDinosaur}, or {@code null} if the type is unrecognised
     */
    public static Dinosaur createHerbivore(String type, double x, double y) {
        switch (type) {
            case "LongNeck":
                return new HerbivoreDinosaur(
                        "Long Neck",
                        150, 10, 40, 1,
                        x, y,
                        Dinosaur.Rarity.COMMON,
                        "/images/dinosaur/longneck.png", 400, 400, 50
                );

            case "Triceratops":
                return new HerbivoreDinosaur(
                        "Triceratops",
                        250, 20, 80, 3,
                        x, y,
                        Dinosaur.Rarity.UNCOMMON,
                        "/images/dinosaur/triceratops.png", 200, 200, 45
                );
        }

        return null;
    }

    /**
     * Creates a carnivore dinosaur of the given type at the specified position.
     * <p>
     * Supported types and their stats:
     * </p>
     * <ul>
     *   <li><b>Raptor</b> – 150 HP, 135 strength, 100 EXP, level 2, {@link Dinosaur.Rarity#COMMON}, speed 3.5, sell price 100</li>
     *   <li><b>TRex</b> – 400 HP, 80 strength, 300 EXP, level 5, {@link Dinosaur.Rarity#RARE}, speed 2.2, sell price 150</li>
     * </ul>
     *
     * @param type the dinosaur type identifier (e.g. {@code "Raptor"}, {@code "TRex"})
     * @param x    the initial X position in the game world
     * @param y    the initial Y position in the game world
     * @return a configured {@link CarnivoreDinosaur}, or {@code null} if the type is unrecognised
     */
    public static Dinosaur createCarnivore(String type, double x, double y) {
        switch (type) {
            case "Raptor":
                return new CarnivoreDinosaur(
                        "Raptor",
                        150, 135, 100, 2,
                        x, y,
                        Dinosaur.Rarity.COMMON,
                        "/images/dinosaur/raptor.gif", 300, 300, 2, 100
                );

            case "TRex":
                return new CarnivoreDinosaur(
                        "T-Rex",
                        400, 80, 300, 5,
                        x, y,
                        Dinosaur.Rarity.RARE,
                        "/images/dinosaur/trex.gif", 300, 300, 2, 150
                );
        }

        return null;
    }

    /**
     * Creates the unique {@link MegaDinosaur} preset at the specified position.
     * <p>
     * The Ancient Colossus has 1500 HP, 150 strength, 1000 EXP drop,
     * requires level 10, is {@link Dinosaur.Rarity#RARE}, and has a sell price of 300.
     * </p>
     *
     * @param x the initial X position in the game world
     * @param y the initial Y position in the game world
     * @return a configured {@link MegaDinosaur}
     */
    public static Dinosaur createMega(double x, double y) {
        return new MegaDinosaur(
                "Ancient Colossus",
                1500, 150, 1000, 10,
                x, y,
                Dinosaur.Rarity.RARE,
                "/images/dinosaur/mega.gif", 500, 500, 300
        );
    }
}