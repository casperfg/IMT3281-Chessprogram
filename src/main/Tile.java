package main;

import javafx.scene.control.Button;
import main.pieces.*;

import java.util.ArrayList;

public class Tile extends Button {
    public Piece chessPiece; // piece pointer
    boolean tileColorWhite; // tile is white
    boolean highLight = false;
    String tileName = "yoyoyo"; // name of tile: e4, e6 etc..
    String column = "abcdefgh"; // column names for tilename
    Boolean hasPiece = false; // has a piece on it

    public void updatePiece(char type) { // set a piece (INITIAL)
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
    public void updateTileName(int[] pos) { // make e4, e5, g7 etc..
        tileName = String.valueOf(column.charAt(pos[0]));
        tileName += String.valueOf((7 - pos[1]) + 1);
    }

    public void updateTileColor(int[] pos) {
        if (pos[1] % 2 == 0) { // If even y
            tileColorWhite = (pos[0] % 2 == 0); // then even x is white
        } else { // if odd
            tileColorWhite = (pos[0] % 2 == 1); // then odd x is white
        }
    }

    public void setProp(int[] pos, boolean color) { // set properties
        if (hasPiece) {
            chessPiece.color = color; // is true if white
            chessPiece.position = pos;
            chessPiece.setIcon();
        }
        updateTileName(pos);
        updateTileColor(pos);
    }

    public void removePiece() { // remove piece

        chessPiece = null;
        hasPiece = false;
    }

    public void possible(Chessboard chBoard, boolean setHighlight) {
        chessPiece.removePossible();
        chessPiece.possible(chBoard);
        if(setHighlight) {
            setHighlight(chBoard);
        }
    }
    public ArrayList<int[]> retPossible(){
        return chessPiece.possibleMoves;
    }

    public boolean kingAttack(Chessboard chBoard, boolean color) {
        int x, y;
        Tile thisTile;
        if(hasPiece) { // this tile as a piece.
            possible(chBoard, false); // get possible moves
            for (int[] pos : chessPiece.possibleMoves) { // loop the possible moves this piece has
                thisTile = chBoard.board[pos[1]][pos[0]];
                if (thisTile.hasPiece) { // attacks a piece
                    if (thisTile.chessPiece.type == 'k' && thisTile.chessPiece.color == color) { // attacks the king
                        chessPiece.removePossible(); // remove possible
                        return true; // return true
                    }
                }
            }
            chessPiece.removePossible(); // remove possible
        }
        return false;
    }

    public void setHighlight(Chessboard chBoard) {
        int x, y;
        for (int i = 0; i < chessPiece.possibleMoves.size(); i++) {
            x = chessPiece.possibleMoves.get(i)[0];
            y = chessPiece.possibleMoves.get(i)[1];
            chBoard.board[y][x].highLight = true;
        }
    }
}
