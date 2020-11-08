package main;
import javafx.scene.Node;
import main.pieces.Piece;
public class Chessboard{
    public Tile[][] board = new Tile[8][8];
    public boolean whiteTurn = true;
    public boolean whiteCastle = false;
    public boolean blackCastle = false;
    public String passantSquare = "-"; // '-' if no passantsquare
    public int moveCount = 0; // increments after black move
    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public void makeStart(){ // setup start position
        int size = 8;
        String line = "rnbkqbnr"; // n=knight
        for(int y = 0; y < size; y++){
            for(int x = 0; x<size; x++){
                board[y][x] = new Tile();

                if(y == 7 || y == 0){ // place officer
                    board[y][x].updatePiece(line.charAt(x));
                }else if(y == 6 || y == 1){ // place pawn
                    board[y][x].updatePiece('p');
                }

                // set location and color of piece.
                // y=6 || y=6 when color is white
                board[y][x].setProp(new int[]{x, y}, (y == 7 || y == 6));
            }
        }
    }
    // validates before moving.
    public void move(int x, int y, int xt, int yt){ // from x,y to xt, yt
        Tile chTile; // tile
        Piece fPiece = board[y][x].chessPiece;
        Piece tPiece = board[yt][xt].chessPiece; // from piece and topiece
        boolean isBlank = !board[yt][xt].hasPiece;

        boolean isOpposite = false;
        if (!isBlank){
            isOpposite = tPiece.color != fPiece.color;
        }

        if(board[y][x].hasPiece && (isBlank || isOpposite)){
            board[yt][xt].chessPiece = fPiece; // move piece to new tile
            board[yt][xt].chessPiece.lastPosition = new int[]{x,y};
            board[yt][xt].hasPiece = true;
            board[y][x].removePiece();

            whiteTurn = !whiteTurn;
            if(whiteTurn) {moveCount++;}
        }else{
            System.out.println("wrong move");
            System.exit(1);
        }
    }
    public String toFen(){
        Tile myTile;
        Piece myPiece;
        String turn = ((whiteTurn)? "w" : "b");
        String wCastle = ((!whiteCastle)? "KQ" : "");
        String bCastle = ((!blackCastle)? "kq" : "");
        String result;

        StringBuilder fen = new StringBuilder();
        int empty;

        // state information, castle, enpassant movecount
        if(wCastle == "" && bCastle == ""){
            wCastle = "-";
        }
        result = " "+turn+" "+wCastle+bCastle;
        result += " "+passantSquare + " 0 "+String.valueOf(moveCount);

        // board position
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
                    fen.append((myPiece.color) ? Character.toUpperCase(myPiece.type) : myPiece.type);
                }
            }
            if(empty != 0){fen.append(empty);}
            if(y != 7) {fen.append('/'); } // add
        }
        return fen.toString()+result;
    }
    public Chessboard(){
        makeStart();
        System.out.println(toFen());
        move(3,6,3,5);
        System.out.println(toFen());
    }
}
