package main;
import javafx.scene.Node;
import main.pieces.Piece;
public class Chessboard{
    public Tile[][] board = new Tile[8][8];
    public String line = "rnbkqbnr"; // n=knight
    public boolean whiteTurn = true;

    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public void makeStart(){
        int size = 8;
        int[] startLine = new int[]{7,0};
        for(int y = 0; y < size; y++){
            for(int x = 0; x<size; x++){
                board[y][x] = new Tile();
                if(y == 7 || y == 0){
                    board[y][x].updatePiece(line.charAt(x));
                }else if(y == 6 || y == 1){
                    board[y][x].updatePiece('p');
                }
                board[y][x].setProp(new int[]{x, y}, (y == 7 || y == 6));
            }
        }
    }
    public String toFen(){
        Tile myTile;
        Piece myPiece;
        StringBuilder fen = new StringBuilder();
        int empty;
        for(int y = 0; y<8; y++){
            empty = 0; // set number of empty squares to 0
            for(int x = 0; x<8; x++){
                myTile = board[y][x];
                if(!myTile.hasPiece){
                    empty += 1;
                }else{
                    myPiece = myTile.chessPiece; // get the chess piece
                    if(empty != 0){ // flush empty squares to fen
                        fen.append(empty);
                        empty = 0;
                    }
                    // add piece to fen
                    fen.append((myPiece.color) ? Character.toUpperCase(myTile.pieceChar) : myTile.pieceChar);
                }
            }
            if(empty != 0){fen.append(empty);}
            if(y != 7) {fen.append('/'); }
        }
        return fen.toString();
    }
    public Chessboard(){
        makeStart();
        System.out.println(toFen());
    }
}
