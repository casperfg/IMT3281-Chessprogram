package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Queen extends Piece {
    public int[][] moveVector = new int[][]{{0,1},{0, -1},{1,0},{-1, 0}, {1,-1},{1, 1},{-1,1},{-1, -1}};
    public Queen(boolean color){
        super(color, "queen", 'q');
    }

    public Queen(){

    }
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_queen.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_queen.png"));
        }
    }
    public void possible(Chessboard board) {
        repeat(moveVector, board);
    }
}
