package spawnscreen.Location;

import spawnscreen.Interfaces.Interactable;
import javafx.scene.image.Image;

public abstract class Location implements Interactable {
    private String uml,name;
    private double xPos ,yPos ;
    private int height,width;

    public Location(String uml,double xPos,double yPos,int height,int width,String name){
        this.name = name;
        this.uml = uml;
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public double getxPos(){
        return xPos;
    }

    public double getyPos(){
        return yPos;
    }

    public String getUml(){
        return uml;
    }

    public Image getImage(){
        return new Image(getClass().getResource(getUml()).toExternalForm());
    }

    public String getName(){
        return name;
    }
}
