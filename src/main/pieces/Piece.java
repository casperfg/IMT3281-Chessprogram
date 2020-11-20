package main.pieces;

import javafx.scene.image.Image;
import main.Tile;
import main.Chessboard;

import java.util.ArrayList;

public class Piece{
    public int[] position; // internal position of tile (x,y) 0-7
    public int[] lastPosition = new int[]{-1, -1}; // x, y
    public boolean color; // color of piece, true=white
    public String name;
    public char type = '-'; // knbr osv..
    public boolean rookMoved = false; // set to true if tower and has moved (Castle not allowed)
    public Image icon; // image icon.
    public ArrayList<int[]> possibleMoves = new ArrayList<>(); // moves possible, coordinates in internal form.

    public void removePossible(){
        possibleMoves.removeAll(possibleMoves);
    }
    public void removePossible(int i) { // remove i element of possible.
        if (possibleMoves.size() > i){
            possibleMoves.remove(i);
        }
    }

    Piece(boolean color, String name, char type){
        this.color = color;
        this.name = name;
        this.type = type;
    }

    Piece(){

    }

    public void possible(Chessboard board){ // calls children (polumorphism)
    }

    // add position with movevector to possibleMoves, if legal.
    // used by Pawn and Piece.nonRepeat()
    public boolean avoidCheck(Chessboard board, int xt, int yt){ // 60-100ms
        Chessboard tmpBoard = new Chessboard(board, board.toFen(), board.cnt); // resets board position. to original position
        tmpBoard.checkForChecks = false;
        tmpBoard.move(position[0], position[1], xt, yt);

        if(!tmpBoard.kingAttack(!color)){ // avoided the check given
            return true;
        }
        return false;
    }
    public void addWithAvoid(Chessboard board, int xt, int yt){ // add to list if it avoids check
        if(board.checkForChecks && !board.check){ // if in check, the possible moves are given.
            if(avoidCheck(board, xt, yt)){
                possibleMoves.add(new int[]{xt, yt});
            }
        }else{
            possibleMoves.add(new int[]{xt, yt});
        }
    }
    public void addPoss(Chessboard board, int dx, int dy){ // add this vector to the list
        int xt = position[0]+dx;
        int yt = position[1]+dy;
        if(board.legalMove(position[0], position[1], xt, yt) ) {
            addWithAvoid(board, xt, yt);
        }
    }
    // continues to add movevector to position until illegal move
    public void repeat(int[][] moveVector, Chessboard board){
        int[] vector;
        int x, y;
        for(int i = 0; i < moveVector.length; i++){
            vector = moveVector[i];
            x = position[0]+vector[0];
            y = position[1]+vector[1];

            while(board.legalMove(position[0], position[1], x, y)){
                addWithAvoid(board, x, y);
                if(!board.blankSq(x,y)){
                    break;
                }
                x += vector[0];
                y += vector[1];
            }
        }
    }
    // checks each vector if legal, if so add it to list
    public void nonRepeat(int[][] moveVector, Chessboard board){
        for (int[] ints : moveVector) {
            addPoss(board, ints[0], ints[1]);
        }
    }
    public void setIcon() {  // Calls child classes
    }
    public boolean getColor(){
        return color;
    }
}
