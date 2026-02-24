package gamemode.lobby.Player;

import gamemode.lobby.Interfaces.Buyable;
import gamemode.lobby.Interfaces.Sellable;
import gamemode.lobby.Item.Base.Item;
import gamemode.lobby.Item.Base.Potion;
import gamemode.lobby.Item.Base.TamedDinosaur;
import gamemode.lobby.Item.Base.Weapon;
import gamemode.lobby.Location.*;
import gamemode.lobby.Scene.SpawnCanvas;
import gamemode.lobby.Scene.SpawnScreen;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

import gamemode.lobby.logic.GameController;

public class Player {
    //field
    private final String name;
    private int money;
    private int hp;
    private int strength;
    private int exp;
    private int level;
    private ArrayList<Item> inventory;
    private double speed = 2;

    public int getInventorylimit() {
        return inventorylimit;
    }

    private int inventorylimit = 12;
    private double x, y;
    private final double WIDTH = 60;
    private final double HEIGHT = 60;
    private final Image playerImage = new Image(getClass().getResource("/gamemode/lobby/person.png").toExternalForm());

    //constructor
    public Player(double x,double y) {
        this.name = "Tae";
        setMoney(0);
        setHp(100);
        setStrength(1);
        setExp(0);
        setLevel(1);
        inventory = new ArrayList<>();

        this.x = x;
        this.y = y;
    }

    //getter & setter
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

        this.x += dirLR * speed;
        this.y += dirUD * speed;

        // กันออกนอกจอ
        if (x < 0) x = 0;
        if (x > 1422 - WIDTH) x = 1422 - WIDTH;

        if (y < 0) y = 0;
        if (y > 800 - HEIGHT) y = 800 - HEIGHT;

        SpawnScreen spawnScreen = GameController.getInstance().getRoot();
        SpawnCanvas spawnCanvas = GameController.getInstance().getRoot().getSpawnCanvas();
        Shop shop = spawnCanvas.getShop();
        Zoo zoo = spawnCanvas.getZoo();
        Ufo ufo = spawnCanvas.getUfo();
        Gym gym = spawnCanvas.getGym();
    }

    public boolean isNear(Location location){
        return intersects(
                location.getxPos(),
                location.getyPos(),
                location.getHeight(),
                location.getWidth()
        );
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(playerImage, x, y, WIDTH, HEIGHT);
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
        if(money < ((Buyable) item).getBuyPrice())return;
        if(item instanceof Weapon
                && inventory.stream().anyMatch(i -> i.getName().equals(item.getName())))return;
        money -= ((Buyable) item).getBuyPrice();
        inventory.add(item);
    }
}