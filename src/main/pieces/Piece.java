package main.pieces;

import javafx.scene.image.Image;
import main.Tile;
import main.Chessboard;

import java.util.ArrayList;

public class Piece{
    public int[] position; // internal position of tile (x,y) 0-7
    public int[] lastPosition = new int[2]; // x, y
    public boolean color; // color of piece, true=white
    int direction;
    public char type; // knbr osv..
    public Image icon; // image icon.

    public ArrayList<int[]> possibleMoves;
    public void possible(Chessboard board){
        System.out.println("piece");
    }

    public void setIcon() {
    }
}
