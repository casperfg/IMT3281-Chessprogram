package main;

import main.pieces.Bishop;
import main.pieces.King;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Piece;
import main.pieces.Queen;
import main.pieces.Rook;

public class Tile {
    public int[] position; // internal position of tile (x,y) 0-7
    boolean tileColorWhite; // tile is white
    String tileName; // name of tile: e4, e6 etc..
    String column = "abcdefgh"; // column names for tilename

    Boolean hasPiece = false; // has a piece on it
    Piece chessPiece; // piece pointer

    public void updatePiece(char type){ // set a piece
        hasPiece = true;
        switch (type) {
            case 'n' -> chessPiece = new Knight();
            case 'b' -> chessPiece = new Bishop();
            case 'r' -> chessPiece = new Rook();
            case 'k' -> chessPiece = new King();
            case 'q' -> chessPiece = new Queen();
            case 'p' -> chessPiece = new Pawn();
        }
        chessPiece.type = type;
    }
    // pos = (x,y)
    public void updateTileName(int[] pos){ // make e4, e5, g7 etc..
        tileName = String.valueOf(column.charAt(pos[0]));
        tileName += String.valueOf((7-pos[1])+1);
    }
    public void updateTileColor(int[] pos){
        if(pos[1]%2 == 0){ // If even y
            tileColorWhite = (pos[0]%2 == 0); // then even x is white
        }else{ // if odd
            tileColorWhite = (pos[0]%2 == 1); // then odd x is white
        }
    }
    public void setProp(int[] pos, boolean color){ // set properties
        position = pos; // (x,y)
        if(hasPiece){
            chessPiece.color = color; // is true if white
        }
        updateTileName(pos);
        updateTileColor(pos);
    }

    public void removePiece(){ // remove piece
        chessPiece = null;
        hasPiece = false;
    }
}
