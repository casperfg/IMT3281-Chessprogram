package main.pieces;

import javafx.scene.image.Image;

public class Piece{
    public int[] position; // internal position of tile (x,y) 0-7
    public int[] lastPosition = new int[2]; // x, y
    public boolean color;
    public char type;
    public Image icon;
    public void possible(){
        System.out.println("piece");
    }

    public void setIcon() {
    }
}
