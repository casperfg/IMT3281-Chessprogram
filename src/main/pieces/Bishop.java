package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Bishop extends Piece{
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{1,-1},{1, 1},{-1,1},{-1, -1}};
    public Bishop(boolean color){
        super(color, "bishop", 'b');
    }

    public Bishop(){

    }
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_bishop.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_bishop.png"));
        }
    }
    public void possible(Chessboard board) {
        repeat(moveVector, board);
    }
}
