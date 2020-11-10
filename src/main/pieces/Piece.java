package main.pieces;

import javafx.scene.image.Image;
import main.Tile;
import main.Chessboard;

import java.util.ArrayList;

public class Piece{
    public int[] position; // internal position of tile (x,y) 0-7
    public int[] lastPosition = new int[2]; // x, y
    public boolean color; // color of piece, true=white
    public char type; // knbr osv..
    public Image icon; // image icon.
    public ArrayList<int[]> possibleMoves = new ArrayList<int[]>();

    public void possible(Chessboard board){
        System.out.println("piece");
    }
    public void addPoss(Chessboard board, int dx, int dy){
        int xt = position[0]+dx;
        int yt = position[1]+dy; // double check legal
        if(board.legalMove(position[0], position[1], xt, yt)){
            possibleMoves.add(new int[]{xt, yt});
        }
    }
    public void setIcon() {
    }
}
