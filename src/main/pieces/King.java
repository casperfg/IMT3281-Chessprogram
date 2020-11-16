package main.pieces;

import main.Chessboard;

import javafx.scene.image.Image;
public class King extends Piece{
    // movevector same as queen, but not reating vector.
    public boolean castled = false;
    public Piece towerPiece;
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
        if(canCastle(board)){
            if(shortCastle(board)){
                addPoss(board, 2, 0);
            }
            if(longCastle(board)){
                addPoss(board, -2, 0);
            }
        }
        System.out.println("King");
    }
    public boolean canCastle(Chessboard board){
        return (!board.whiteCastle && color) || (!board.blackCastle && !color);
    }
    public boolean shortCastle(Chessboard board){
        if(!board.blankSq(position[0]+3, position[1])) {
            towerPiece = board.board[position[1]][position[0] + 3].chessPiece;
            return board.blankSq(position[0] + 1, position[1])
                    && board.blankSq(position[0] + 2, position[1])
                    && !towerPiece.rookMoved;
        }else{
            return false;
        }
    }
    public boolean longCastle(Chessboard board){
        if(!board.blankSq(position[0]-4, position[1])) {
            towerPiece = board.board[position[1]][position[0] - 4].chessPiece;
            if(towerPiece.lastPosition == null);
            return board.blankSq(position[0] - 1, position[1]) // blank x+1
                    && board.blankSq(position[0] - 2, position[1]) // blank x+2
                    && board.blankSq(position[0] - 3, position[1])
                    && !towerPiece.rookMoved; // blank x+3
        }else{
            return false;
        }


    }
}
