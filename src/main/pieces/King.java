package main.pieces;

import main.Chessboard;
import main.Tile;

import java.util.ArrayList;
import javafx.scene.image.Image;
public class King extends Piece{
    // movevector same as queen, but not reating vector.
    public int[][] moveVector = new int[][]{{0,1},{0, -1},{1,0},{-1, 0}, {1,-1},{1, 1},{-1,1},{-1, -1}};
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_king.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_king.png"));
        }
    }
    public void possible(Chessboard board) {
         nonRepeat(moveVector, board);
        System.out.println("Knight");
    }
}
