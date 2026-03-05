package gamemode.lobby.Player;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.Base.Weapon;
import gamemode.lobby.logic.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;

/**
 * The {@code Player} class represents the controllable character in the lobby game mode.
 *
 * <p>This class manages all player-related attributes including:
 * <ul>
 *     <li>Core statistics (HP, strength, level, experience)</li>
 *     <li>Inventory management</li>
 *     <li>Movement and position handling</li>
 *     <li>Animation rendering</li>
 *     <li>Item purchasing and selling</li>
 *     <li>Potion usage and temporary buffs</li>
 *     <li>Level progression system</li>
 * </ul>
 *
 * <p>The player supports temporary buffs such as:
 * <ul>
 *     <li>Experience Boost</li>
 *     <li>Speed Boost</li>
 * </ul>
 *
 * <p>Rendering is handled via JavaFX {@link javafx.scene.canvas.GraphicsContext}.
 *
 * <p>This class follows a state-based structure where movement,
 * animation, and buffs are updated dynamically.
 *
 * @author
 * @version 1.0
 */
public class Player {

    // --- Fields ---

    /** The name of the player. */
    private final String name;
    /** Current currency held by the player. */
    private int money;
    /** Current health points of the player. */
    private int hp;
    /** Maximum health points the player can have. */
    private int maxHp;
    /** Current attack strength of the player. */
    private int strength;
    /** Initial base strength before modifiers. */
    private final int baseStrength = 30;
    /** Current experience points. */
    private int exp;
    /** Current level of the player. */
    private int level;
    /** List containing items currently held by the player. */
    private ArrayList<Item> inventory;
    /** Current movement speed. */
    private double speed;
    /** Initial base speed before modifiers. */
    private final double baseSpeed = 2;
    /** Maximum number of items the inventory can hold. */
    private final int inventorylimit = 12;

    /** X-coordinate in the game world. */
    private double x;
    /** Y-coordinate in the game world. */
    private double y;
    /** Visual width of the player sprite. */
    private final double WIDTH = 60;
    /** Visual height of the player sprite. */
    private final double HEIGHT = 60;

    // Buff management
    private boolean expBoostActive = false;
    private long expBoostEndTime = 0;
    private boolean speedBoostActive = false;
    private long speedBoostEndTime = 0;

    // Animation and State
    private AnimationPlayer idleAnimation;
    private AnimationPlayer runAnimation;
    private boolean isMoving = false;
    private boolean facingRight = true;
    /** Amount of experience required to reach the next level. */
    private int expToNextLevel = 100;

    /**
     * Constructs a new Player at a specific starting position.
     * Initializes default stats, inventory, and loads animation frames.
     *
     * @param x Initial X-coordinate.
     * @param y Initial Y-coordinate.
     */
    public Player(double x, double y) {
        this.speed = baseSpeed;
        this.strength = baseStrength;
        this.name = "Tae";
        setMoney(1000);
        setExp(0);
        setLevel(1);
        setMaxHp(200);
        setHp(200);
        this.inventory = new ArrayList<>();
        this.x = x;
        this.y = y;

        // Load idle frames
        Image[] idleFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            idleFrames[i] = new Image(getClass().getResource("/character/ptaeidle" + (i + 1) + ".png").toExternalForm());
        }

        // Load run frames
        Image[] runFrames = new Image[7];
        for (int i = 0; i < 7; i++) {
            runFrames[i] = new Image(getClass().getResource("/character/ptaerun" + (i + 1) + ".png").toExternalForm());
        }

        idleAnimation = new AnimationPlayer(idleFrames, 150);
        runAnimation = new AnimationPlayer(runFrames, 100);
    }

    // --- Getters & Setters ---

    /** @return Maximum health points. */
    public int getMaxHp() { return maxHp; }
    /** @param maxHp New maximum health points. */
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    /** @return The player's name. */
    public String getName() { return name; }
    /** @return Current money. */
    public int getMoney() { return money; }
    /** @param money Set money (clamped at minimum 0). */
    public void setMoney(int money) { this.money = Math.max(0, money); }
    /** @return Current health. */
    public int getHp() { return hp; }
    /** @param hp Set health (clamped at minimum 0). */
    public void setHp(int hp) { this.hp = Math.max(0, hp); }
    /** @return Current strength. */
    public int getStrength() { return strength; }
    /** @param strength Set strength (clamped at minimum 0). */
    public void setStrength(int strength) { this.strength = Math.max(0, strength); }
    /** @return Current experience. */
    public int getExp() { return exp; }
    /** @param exp Set experience (clamped at minimum 0). */
    public void setExp(int exp) { this.exp = Math.max(0, exp); }
    /** @return Current level. */
    public int getLevel() { return level; }
    /** @param level Set level (clamped at minimum 1). */
    public void setLevel(int level) { this.level = Math.max(1, level); }
    /** @return The inventory list. */
    public ArrayList<Item> getInventory() { return inventory; }
    /** @param inventory Set the inventory list. */
    public void setInventory(ArrayList<Item> inventory) { this.inventory = inventory; }
    /** @return Current X-coordinate. */
    public double getX() { return x; }
    /** @param x Set X-coordinate. */
    public void setX(double x) { this.x = x; }
    /** @return Current Y-coordinate. */
    public double getY() { return y; }
    /** @param y Set Y-coordinate. */
    public void setY(double y) { this.y = y; }
    /** @return Player width. */
    public double getWidth() { return WIDTH; }
    /** @return Player height. */
    public double getHeight() { return HEIGHT; }
    /** @return Current movement speed. */
    public double getSpeed() { return speed; }
    /** @return Maximum capacity of inventory. */
    public int getInventorylimit() { return inventorylimit; }
    /** @return EXP needed for next level. */
    public int getExpToNextLevel() { return expToNextLevel; }

    // --- Core Methods ---

    /**
     * Adds an item to the inventory without checking constraints.
     * @param item The item to add.
     */
    public void addItem(Item item) {
        inventory.add(item);
    }

    /**
     * Updates the player's position based on input directions and handles boundaries.
     * * @param dirLR Direction Left/Right (-1, 0, 1).
     * @param dirUD Direction Up/Down (-1, 0, 1).
     */
    public void move(int dirLR, int dirUD) {
        if (GameController.getInstance().isGameEnded()) return;

        isMoving = (dirLR != 0 || dirUD != 0);

        if (dirLR < 0) {
            facingRight = false;
        } else if (dirLR > 0) {
            facingRight = true;
        }

        this.x += dirLR * speed;
        this.y += dirUD * speed;

        // Screen boundaries
        if (x < 0) x = 0;
        if (x > 1422 - WIDTH) x = 1422 - WIDTH;
        if (y < 90) y = 90;
        if (y > 800 - HEIGHT) y = 800 - HEIGHT;

        if (!isMoving) {
            runAnimation.reset();
        }
    }

    /**
     * Renders the player's current animation frame to the canvas.
     * @param gc The GraphicsContext used for drawing.
     */
    public void render(GraphicsContext gc) {
        if (isMoving) {
            runAnimation.update();
            runAnimation.render(gc, x, y, WIDTH, HEIGHT, facingRight);
        } else {
            idleAnimation.update();
            idleAnimation.render(gc, x, y, WIDTH, HEIGHT, facingRight);
        }
    }

    /** @param facingRight Set true if facing right, false for left. */
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    /**
     * Checks if the player's bounding box intersects with another rectangle.
     * @return True if colliding.
     */
    public boolean intersects(double otherX, double otherY, double otherH, double otherW) {
        return x < otherX + otherW &&
                x + WIDTH > otherX &&
                y < otherY + otherH &&
                y + HEIGHT > otherY;
    }

    /**
     * Sorts the inventory by item type: Dinosaurs first, then Weapons, then Potions.
     */
    public void sortInventory() {
        ArrayList<Item> items = getInventory();
        ArrayList<Item> newInventory = new ArrayList<>();

        for (Item item : items) if (item instanceof TamedDinosaur) newInventory.add(item);
        for (Item item : items) if (item instanceof Weapon) newInventory.add(item);
        for (Item item : items) if (item instanceof Potion) newInventory.add(item);

        setInventory(newInventory);
    }

    /**
     * Sells an item if it is Sellable, adding money and removing it from inventory.
     * @param item The item to sell.
     */
    public void sellItem(Item item) {
        if (item instanceof Sellable) {
            money += ((Sellable) item).getSellPrice();
        }
        inventory.remove(item);
    }

    /**
     * Buys an item if the player has enough money and space.
     * Weapons cannot be duplicated.
     * @param item The item to purchase.
     */
    public void buyItem(Item item) {
        if (inventory.size() >= inventorylimit) return;
        if (money < ((Buyable) item).getBuyPrice()) return;
        if (item instanceof Weapon && inventory.stream().anyMatch(i -> i.getName().equals(item.getName()))) return;

        money -= ((Buyable) item).getBuyPrice();
        inventory.add(item);
    }

    /**
     * Applies potion effects to the player and removes the potion from inventory.
     * @param potion The potion to consume.
     */
    public void usePotion(Potion potion) {
        switch (potion.getName()) {
            case "Exp Potion" -> {
                expBoostActive = true;
                expBoostEndTime = System.currentTimeMillis() + (10 * 60 * 1000);
                System.out.println("EXP Boost Activated!");
            }
            case "Heal Potion" -> {
                int healAmount = (maxHp-hp);
                setHp(hp + healAmount);
                System.out.println("Healed +" + healAmount);
            }
            case "Speed Potion" -> {
                speedBoostActive = true;
                speed = baseSpeed * 1.5;
                speedBoostEndTime = System.currentTimeMillis() + (10 * 60 * 1000);
                System.out.println("Speed Boost Activated!");
            }
            case "Strength Potion" -> {
                strength = strength * 2;
                System.out.println("Strength Boost Activated!");
            }
        }
        inventory.remove(potion);
    }

    /**
     * Checks and handles the expiration of timed buffs (EXP and Speed).
     */
    public void updateBuffs() {
        long now = System.currentTimeMillis();
        if (expBoostActive && now > expBoostEndTime) {
            expBoostActive = false;
            System.out.println("EXP Boost Ended");
        }
        if (speedBoostActive && now > speedBoostEndTime) {
            speedBoostActive = false;
            speed = baseSpeed;
            System.out.println("Speed Boost Ended");
        }
    }

    /** @param moving Set the movement state for animation. */
    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    /** @param item Removes specific item from inventory. */
    public void removeItem(Item item) {
        if (item == null) return;
        inventory.remove(item);
    }

    /**
     * Adds experience points, doubling them if a boost is active.
     * Triggers leveling up if threshold is met.
     * @param amount The base amount of EXP to add.
     */
    public void addExp(int amount) {
        if (expBoostActive) amount *= 2;
        setExp(exp + amount);

        while (exp >= expToNextLevel) {
            levelUp();
        }
        if (GameController.getInstance().getRoot() != null) {
            GameController.getInstance().getRoot().updateExpBar();
        }
    }

    /**
     * Increases level, enhances stats, and resets HP.
     * Increases requirement for the next level.
     */
    private void levelUp() {
        exp -= expToNextLevel;
        level++;
        expToNextLevel += 50;

        maxHp += 5;
        strength += 2;
        setHp(maxHp);

        if (GameController.getInstance() != null &&
                GameController.getInstance().getRoot() != null) {
            GameController.getInstance().getRoot().updateLevel();
        }
        System.out.println("LEVEL UP! Now level " + level);
    }

}