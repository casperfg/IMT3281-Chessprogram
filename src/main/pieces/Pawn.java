package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;
import main.Tile;

public class Pawn extends Piece{
    int direction; // ychange of piece. -1 white 1 black

    public Pawn(boolean color){
        super(color, "pawn");
    }

    public Pawn(){

    }

    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_pawn.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_pawn.png"));
        }
    }
    public void possible(Chessboard board) {
        direction = color? -1: 1; // y change direction
        if(board.blankSq(position[0], position[1]+direction)){ // 1 square forward is blank
            addPoss(board, 0, direction); // move 1 square forward
            if((position[1] == 6 && color) || (position[1] == 1 && !color)){ // is on enpassant position
                if(board.blankSq(position[0], position[1]+2*direction)){ // 2 squares forward is blank
                    addPoss(board, 0,2*direction);
                }
            }
        }
        if(!board.blankSq(position[0]-1, position[1]+direction)){ // pawn taking -x
            addPoss(board, -1, direction);
        }
        if(!board.blankSq(position[0]+1, position[1]+direction)) { // pawn taking +x
            addPoss(board, 1, direction);
        }
        System.out.println("pawn");
    }
}
