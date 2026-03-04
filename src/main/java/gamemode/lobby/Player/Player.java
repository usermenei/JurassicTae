package gamemode.lobby.Player;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.Base.Weapon;
import gamemode.lobby.Scene.SpawnCanvas;
import gamemode.lobby.Scene.SpawnScreen;
import gamemode.lobby.logic.GameLogic;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

import gamemode.lobby.logic.GameController;

public class Player {
    //field

    private final String name;
    private int money;
    private int hp,maxHp;
    private int strength,baseStrength = 30;
    private int exp;
    private int level;
    private ArrayList<Item> inventory;
    private double speed ,baseSpeed = 2;

    public int getInventorylimit() {
        return inventorylimit;
    }

    private int inventorylimit = 12;
    private double x, y;
    private final double WIDTH = 60;
    private final double HEIGHT = 60;
    private final Image playerImage = new Image(getClass().getResource("/gamemode/lobby/person.png").toExternalForm());

    private boolean expBoostActive = false;
    private long expBoostEndTime = 0;

    private boolean speedBoostActive = false;
    private long speedBoostEndTime = 0;

    private AnimationPlayer idleAnimation;
    private AnimationPlayer runAnimation;

    private boolean isMoving = false;
    private boolean facingRight = true;
    private int expToNextLevel = 100;

    //constructor
    public Player(double x,double y) {
        this.speed = baseSpeed;
        this.strength = baseStrength;
        this.name = "Tae";
        setMoney(0);
        setExp(0);
        setLevel(1);
        setMaxHp(200);
        setHp(200);
        inventory = new ArrayList<>();

        this.x = x;
        this.y = y;
        // Load idle frames
        Image[] idleFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            idleFrames[i] = new Image(
                    getClass().getResource("/character/ptaeidle" + (i + 1) + ".png").toExternalForm()
            );
        }

        // Load run frames
        Image[] runFrames = new Image[7];
        for (int i = 0; i < 7; i++) {
            runFrames[i] = new Image(
                    getClass().getResource("/character/ptaerun" + (i + 1) + ".png").toExternalForm()
            );
        }

        // Create animation players
        idleAnimation = new AnimationPlayer(idleFrames, 150);
        runAnimation = new AnimationPlayer(runFrames, 100);
    }

    //getter & setter
    public void setMaxHp(int maxHp){
        this.maxHp = maxHp;
    }

    public int getMaxHp(){return maxHp;}
    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = Math.max(0, money);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = Math.max(0, exp);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<Item> inventory) {
        this.inventory = inventory;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public double getWidth() { return WIDTH; }
    public double getHeight() { return HEIGHT; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    //method

    public void addItem(Item item){
        inventory.add(item);
    }

    public void move(int dirLR, int dirUD) {

        if (GameController.getInstance().isGameEnded()) return;

        isMoving = (dirLR != 0 || dirUD != 0);

        // Set facing direction
        if (dirLR < 0) {
            facingRight = false;  // moving left (A key)
        } else if (dirLR > 0) {
            facingRight = true;   // moving right (D key)
        }

        this.x += dirLR * speed;
        this.y += dirUD * speed;

        if (x < 0) x = 0;
        if (x > 1422 - WIDTH) x = 1422 - WIDTH;

        if (y < 0) y = 0;
        if (y > 800 - HEIGHT) y = 800 - HEIGHT;

        if (!isMoving) {
            runAnimation.reset();
        }
    }

    public void render(GraphicsContext gc) {

        if (isMoving) {
            runAnimation.update();
            runAnimation.render(gc, x, y, WIDTH, HEIGHT, facingRight);
        } else {
            idleAnimation.update();
            idleAnimation.render(gc, x, y, WIDTH, HEIGHT, facingRight);
        }
    }
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean intersects(double otherX, double otherY,
                              double otherH, double otherW) {

        return x < otherX + otherW &&
                x + WIDTH > otherX &&
                y < otherY + otherH &&
                y + HEIGHT > otherY;
    }


    public void sortInventory(){
        ArrayList<Item> items = getInventory();
        ArrayList<Item> newInventory = new ArrayList<>();

        for(Item item : items){
            if(item instanceof TamedDinosaur){
                newInventory.add(item);
            }
        }
        for(Item item : items){
            if(item instanceof Weapon){
                newInventory.add(item);
            }
        }
        for(Item item : items){
            if(item instanceof Potion){
                newInventory.add(item);
            }
        }
        setInventory(newInventory);
    }

    public void sellItem(Item item){
        //(if!(item instanceof Sellable))return;
        if (item instanceof Sellable)money += ((Sellable) item).getSellPrice();
        inventory.remove(item);
    }

    public void buyItem(Item item){
        if(inventory.size() >= 12)return;
        if(money < ((Buyable) item).getBuyPrice())return;
        if(item instanceof Weapon
                && inventory.stream().anyMatch(i -> i.getName().equals(item.getName())))return;
        money -= ((Buyable) item).getBuyPrice();
        inventory.add(item);
    }

    public void usePotion(Potion potion) {

        switch (potion.getName()) {

            case "Exp Potion" -> {
                expBoostActive = true;
                expBoostEndTime = System.currentTimeMillis() + (10 * 60 * 1000); // 10 นาที
                System.out.println("EXP Boost Activated!");
            }

            case "Heal Potion" -> {
                int healAmount = (int)(hp * 0.10);
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
    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }
    public void removeItem(Item item) {
        if (item == null) return;
        inventory.remove(item);
    }

    public void addExp(int amount) {

        if (expBoostActive) {
            amount *= 2;
        }

        setExp(exp + amount);

        while (exp >= expToNextLevel) {
            levelUp();
        }
        GameController.getInstance().getRoot().updateExpBar();
    }

    private void levelUp() {
        exp -= expToNextLevel;
        level++;

        expToNextLevel += 50;

        ///upgrade stat
        maxHp += 5;
        strength += 2;

        setHp(maxHp); // heal เต็มตอนเลเวลอัป

        GameController.getInstance().getRoot().updateLevel();
        System.out.println("LEVEL UP! Now level " + level);
    }

    public int getExpToNextLevel(){return expToNextLevel;}

}