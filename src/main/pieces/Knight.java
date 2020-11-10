package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Knight extends Piece { // Hest
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}, {1,-2} };
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_knight.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_knight.png"));
        }
    }
    public void possible(Chessboard board) {
        int[] vector;
        for(int i = 0; i < moveVector.length; i++){
            vector = moveVector[i];
            if(board.legalMove(position[0], position[1], vector[0], vector[1])){
                addPoss(board, vector[0], vector[0]);
            }
        }
        System.out.println("Knight");
    }
}
