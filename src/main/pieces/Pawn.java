package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;
import main.Tile;

public class Pawn extends Piece{
    public int[][] moveVector = new int[][]{{1,0},{2,0},};
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_pawn.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_pawn.png"));
        }
    }
    public Boolean addPoss(Chessboard board, int dx, int dy){
        int xt = position[0]+dx;
        int yt = position[1]+dy;
        if(board.legalMove(position[0], position[1], xt, yt)){
            possibleMoves.add(new int[]{xt, yt});
            return true;
        }
        return false;
    }
    public void possible(Chessboard board) {

            if(addPoss(board, 0, 1))
            if((position[1] == 6 && color) || (position[1] == 1 && !color)){
                addPoss(board, 0,-2*(color? 1: -1)); // ensure right direction
            }
        System.out.println("pawn");
    }
}
