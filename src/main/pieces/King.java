package main.pieces;

import main.Chessboard;

import javafx.scene.image.Image;
public class King extends Piece{
    // movevector same as queen, but not reating vector.
    public boolean castled = false;
    public Piece towerPiece;
    public int[][] moveVector = new int[][]{{0,1},{0, -1},{1,0},{-1, 0}, {1,-1},{1, 1},{-1,1},{-1, -1}};
    public King(boolean color){
        super(color, "king");
    }

    public King(){

    }
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
    public boolean shortCastle(Chessboard board){ // if shortCastle is possible.
        if(!board.blankSq(position[0]+3, position[1])) {
            towerPiece = board.board[position[1]][position[0] + 3].chessPiece; // tower piece
            if(towerPiece.type == 'r'){
                towerPiece = board.board[position[1]][position[0] + 3].chessPiece;
                return board.blankSq(position[0] + 1, position[1]) // blank +1x
                        && board.blankSq(position[0] + 2, position[1]) // blank +2x
                        && !towerPiece.rookMoved; // rook has not moved
            }
            return false;

        }
        return false;
    }
    public boolean longCastle(Chessboard board){
        if(!board.blankSq(position[0]-4, position[1])) {
            towerPiece = board.board[position[1]][position[0] - 4].chessPiece; // tower piece
            if(towerPiece.type == 'r'){
                return board.blankSq(position[0] - 1, position[1]) // blank x+1
                        && board.blankSq(position[0] - 2, position[1]) // blank x+2
                        && board.blankSq(position[0] - 3, position[1]) // blank x+3
                        && !towerPiece.rookMoved; // rook as not moved
            }
            return false;
        }
        return false;
    }
}
