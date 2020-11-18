package main;

import javafx.scene.control.Button;
import main.pieces.*;

public class Tile extends Button {
    public Piece chessPiece; // piece pointer
    boolean tileColorWhite; // tile is white
    boolean highLight = false;
    String tileName; // name of tile: e4, e6 etc..
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
            int x, y;
            for (int i = 0; i < chessPiece.possibleMoves.size(); i++) {
                x = chessPiece.possibleMoves.get(i)[0];
                y = chessPiece.possibleMoves.get(i)[1];
                chBoard.board[y][x].highLight = true;
            }
        }
    }

    public boolean kingAttack(Chessboard chBoard) {
        int x, y;
        possible(chBoard, false); // get possible moves from here.
        for (int i = 0; i < chessPiece.possibleMoves.size(); i++) {
            x = chessPiece.possibleMoves.get(i)[0];
            y = chessPiece.possibleMoves.get(i)[1];
            if(chBoard.board[y][x].hasPiece){
                if(chBoard.board[y][x].chessPiece.type == 'k'){
                    chessPiece.removePossible();
                    return true;
                }
            }
        }
        chessPiece.removePossible();
        return false;
    }
}
