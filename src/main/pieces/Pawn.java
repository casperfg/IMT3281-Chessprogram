package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Pawn extends Piece {

    int direction; // ychange of piece. -1 white 1 black

    public Pawn() {}

    public void setIcon() {
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_pawn.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_pawn.png"));
        }
    }
    public void passant(Chessboard chBoard, int dx, int dy){
        int xt = position[0]+dx;
        int yt = position[1]+dy;
        if(chBoard.inside(xt,yt) && chBoard.checkForChecks){
            String pasTile = chBoard.board[yt][xt].tileName; // tilename that would be passant
            if(pasTile.equals(chBoard.enPassantSquare)){
                addPoss(chBoard, dx, dy);
            }
        }

    }

    public void possible(Chessboard chBoard) {
        int xt, yt;
        direction = color? -1: 1; // y change direction
        if(chBoard.blankSq(position[0], position[1]+direction)){ // 1 square forward is blank
            addPoss(chBoard, 0, direction); // move 1 square forward
            if((position[1] == 6 && color) || (position[1] == 1 && !color)){ // is on enpassant position
                if(chBoard.blankSq(position[0], position[1]+2*direction)){ // 2 squares forward is blank
                    addPoss(chBoard, 0,2*direction);
                }
            }
        }
        xt = position[0]-1; // pawn taking -x
        yt = position[1]+direction;
        passant(chBoard, -1, direction);
        passant(chBoard, 1, direction);

        if(!chBoard.blankSq(position[0]-1, position[1]+direction)){ // pawn taking -x
            ;
            addPoss(chBoard, -1, direction);
        }
        if(!chBoard.blankSq(position[0]+1, position[1]+direction)) { // pawn taking +x
            addPoss(chBoard, 1, direction);
        }
    }
}
