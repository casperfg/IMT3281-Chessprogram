package main;

public class Tile {
    Boolean hasPiece;
    Piece chessPiece;
    String tileName;

    public void updatePiece(char type){
        hasPiece = true;
        switch (type) {
            case 'n' -> chessPiece = new Knight();
            case 'b' -> chessPiece = new Bishop();
            case 'r' -> chessPiece = new Rook();
            case 'k' -> chessPiece = new King();
            case 'q' -> chessPiece = new Queen();
            case 'p' -> chessPiece = new Pawn();
        }
    }
    public void setPos(int[] pos){
        if(hasPiece){
            chessPiece.position = pos;
        }
    }
    public void removePiece(){
        chessPiece = null;
        hasPiece
    }
}
