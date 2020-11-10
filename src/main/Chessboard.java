package main;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import main.pieces.Piece;

public class Chessboard extends GridPane {
    public Tile[][] board = new Tile[8][8];
    public boolean whiteTurn = true;
    public boolean whiteCastle = false;
    public boolean blackCastle = false;
    public String passantSquare = "-"; // '-' if no passantsquare
    public int moveCount = 0; // increments after black move


    private String compMove;
    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public Chessboard() {
        makeStart();
    }

    public void makeStart() { // setup start position
        int size = 8;
        String line = "rnbqkbnr"; // n=knight
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[y][x] = new Tile();

                if (y == 7 || y == 0) { // place officer
                    board[y][x].updatePiece(line.charAt(x));
                } else if (y == 6 || y == 1) { // place pawn
                    board[y][x].updatePiece('p');
                }

                // set location and color of piece.
                // y=6 || y=6 when color is white
                board[y][x].setProp(new int[]{x, y}, (y == 7 || y == 6));
            }
        }
    }

    public void specialMoves(int x, int y, int xt, int yt, Piece fPiece) {
        if (fPiece.type == 'k') {
            if (fPiece.color) { // king moved, no castling
                whiteCastle = true;
            } else {
                blackCastle = true;
            }
            //if((x-xt) != 1){ // castleing

            //}
        }
    }

    public void move(String move) { // e2e4
        String column = "abcdefgh";
        System.out.println(move);
        int x = column.indexOf(move.charAt(0));
        int y = Character.getNumericValue(move.charAt(1));
        int xt = column.indexOf(move.charAt(2));
        int yt = Character.getNumericValue(move.charAt(3));

        compMove = move;
        move(x, 8 - y, xt, 8 - yt); // internalY = 8-External
    }
    public Boolean blankSq(int xt, int yt){
        return !board[yt][xt].hasPiece;
    }
    // from (x,y) ==> to (xt,yt)
    public Boolean legalMove(int x, int y, int xt, int yt){ // if given move is lega
        Piece fPiece = board[y][x].chessPiece;
        Piece tPiece = board[yt][xt].chessPiece; // from piece and topiece
        boolean isBlank = blankSq(xt, yt);

        boolean isOpposite = false;
        if (!isBlank) {
            isOpposite = tPiece.color != fPiece.color;
        }
        return board[y][x].hasPiece && (isBlank || isOpposite);
    }

    // validates before moving.
    public void move(int x, int y, int xt, int yt) { // from x,y to xt, yt
        Piece fPiece = board[y][x].chessPiece;
        if(legalMove(x, y, xt, yt)) { // checks if legal
            fPiece.position = new int[]{xt, yt};
            fPiece.lastPosition = new int[]{x, y};
            board[yt][xt].chessPiece = fPiece; // move piece to new tile
            board[yt][xt].hasPiece = true;
            board[y][x].removePiece();

            whiteTurn = !whiteTurn;
            if (whiteTurn) {
                moveCount++;
            }
            specialMoves(x, y, xt, yt, fPiece);

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
        if(wCastle.equals("") && bCastle.equals("")){
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
            if (empty != 0) {
                fen.append(empty);
            }
            if (y != 7) {
                fen.append('/');
            } // add
        }
        return fen.toString() + result;
    }

    public GridPane boardView() {
        final int size = 10;
        GridPane gridPane = new GridPane();
        for (int row = 1; row < size-1; row++) {
            for (int col = 1; col < size-1; col ++) {
                StackPane tileSquare = new StackPane();
                Piece cp;
                String color;
                if (board[col-1][row-1].tileColorWhite) {
                    color = "white";
                } else {
                    color = "gray";
                }
                if(board[col-1][row-1].hasPiece){ // if has piece
                    cp = board[col-1][row-1].chessPiece;
                    ImageView vImg = new ImageView(cp.icon);
                    vImg.setFitHeight(50);
                    vImg.setFitWidth(50);
                    tileSquare.getChildren().add(vImg);
                }
                tileSquare.setStyle("-fx-background-color: "+color+";");
                gridPane.add(tileSquare,row, col);
            }
        }
        return gridPane;
    }

    public void humanClick(int x, int y) {
        if (board[y][x].hasPiece) {
            if (board[y][x].chessPiece.color == whiteTurn) { // is correct turn
                board[y][x].possible(this); // call possible WIP
            }
        }
    }
}
