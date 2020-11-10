package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Bishop extends Piece{
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{1,-1},{1, 1},{-1,1},{-1, -1}};
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_bishop.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_bishop.png"));
        }
    }
    public void possible(Chessboard board) {
        int[] vector;
        int x,y;

        for(int i = 0; i < moveVector.length; i++){
            x = position[0];
            y = position[1];
            vector = moveVector[i];
            while(board.legalMove(x, y, vector[0], vector[1])){
                addPoss(board, vector[0], vector[0]);
                x = position[0]+vector[0];
                y = position[1]+vector[1];
            }
        }
        System.out.println("Knight");
    }
}
