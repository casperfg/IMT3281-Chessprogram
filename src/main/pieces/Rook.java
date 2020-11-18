package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Rook extends Piece {
    public int[][] moveVector = new int[][]{{0,1},{0, -1},{1,0},{-1, 0}};
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_rook.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_rook.png"));
        }
    }
    public void possible(Chessboard board) {
        repeat(moveVector, board);
    }
}
