package main;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import main.pieces.Piece;

import java.util.ArrayList;

public class Chessboard {
    public Tile[][] board = new Tile[8][8];

    public boolean whiteTurn = true;
    public boolean whiteCastle = false;
    public boolean blackCastle = false;
    public String enPassantSquare = "-"; // '-' if no passantsquare
    public int moveCount = 0; // increments after black move

    public int repetition = 0;
    public String moveString;
    public ArrayList<String> moves = new ArrayList<>();
    public char promotionTo = '-';
    public Controller cnt = null; // controller pointer.

    public int[] humanPiece = new int[2]; // piece responcible for highlights.

    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public Chessboard(Controller cnt) {
        makeStart();
        this.cnt = cnt;
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
    public void newPiece(int x, int y, char type, boolean color){
        board[y][x].updatePiece(type);
        board[y][x].setProp(new int[]{x,y}, color);
    }
    public void resetHighlight(){
        for(int y = 0; y<8; y++){
            for(int x = 0; x<8; x++){
                board[y][x].highLight = false;
            }
        }
    }
    // ------------MOVE-----------------
    // converts move to save friendly format
    public void moveStringSet(Piece fPiece, int x, int y, int xt, int yt){
        moveString = "";
        if(fPiece.type != 'p'){
            moveString = Character.toString(fPiece.type).toUpperCase();
        }else{ // is pawn
            if(!blankSq(xt,yt)){ // is taking piece, must reference square (feks: exg3)
                moveString = Character.toString(board[y][x].tileName.charAt(0));
            }
        }
        if(!blankSq(xt,yt)){ moveString += "x"; }
        moveString += board[yt][xt].tileName; // the tilename it hits
        if(promotionTo != '-'){ // pawn promotion
            moveString += Character.toString(promotionTo).toUpperCase();
        }

    }
    public void repetition(int xt, int yt, Piece fPiece){
        if(fPiece.lastPosition[0] == xt && fPiece.lastPosition[1] == yt){ // 4x repetition ==> stalemate
            repetition += 1;
        }else{
            repetition = 0;
        }
    }
    public void specialMoves(int x, int y, int xt, int yt, Piece fPiece) {
        enPassantSquare = "-";
        if (fPiece.type == 'k') {
            if((x-xt) != 1 && (yt-y) == 0 ){ // castleing. assumes is legal (threats from black)
                if(!whiteCastle || !blackCastle){
                    moveString = (((xt-x) == 2)? "0-0" : "0-0-0"); // true if short castle.
                    board[y][((xt-x) == 2)? 7 : 0].removePiece(); // remove tower
                    newPiece((xt+x)/2, y, 'r', fPiece.color); // place new tower
                }
            }
            if (fPiece.color) { // king moved, no castling
                whiteCastle = true; // has castled or moved king
            } else {
                blackCastle = true;
            }
        }
        if(fPiece.type == 'p'){
            if((fPiece.color && yt == 0) || (!fPiece.color && yt == 7)){ // PROMOTION
                newPiece(xt, yt, (promotionTo == '-')? 'q' : promotionTo, fPiece.color);
                promotionTo = '-';
            }else if(yt-y != 1){ // 2 squares up
                enPassantSquare = board[(fPiece.color)? 5 : 2][xt].tileName;
            }
        }
    }
    // computer move
    public void move(String move) { // e2e4, promotion: e6e7q
        String column = "abcdefgh";
        if(move.length() >= 4) {
            int x = column.indexOf(move.charAt(0));
            int y = Character.getNumericValue(move.charAt(1));
            int xt = column.indexOf(move.charAt(2));
            int yt = Character.getNumericValue(move.charAt(3));

            if (move.length() > 4) { // is promotion, set char
                promotionTo = move.charAt(4); // used in SPECIALMOVE
            }
            move(x, 8 - y, xt, 8 - yt); // internalY = 8-External
        }else{
            System.out.println("Engine error");
            System.exit(1);
        }
    }

    // validates before moving.
    public void move(int x, int y, int xt, int yt, char promotionTo){
        move(x, y, xt, yt); // move with setting promotionTO.
        this.promotionTo = promotionTo;
    }
    // TODO: update king functionality (castle/check/mate)
    public void move(int x, int y, int xt, int yt) { // from x,y to xt, yt
        Piece fPiece;
        resetHighlight();
        if(legalMove(x, y, xt, yt)) { // checks if legal
            fPiece = board[y][x].chessPiece;
            repetition(xt, yt, fPiece);

            fPiece.position = new int[]{xt, yt};
            fPiece.lastPosition = new int[]{x, y};
            fPiece.possibleMoves.removeAll(fPiece.possibleMoves); // empty possible moves.
            moveStringSet(fPiece, x, y, xt, yt);

            board[yt][xt].chessPiece = fPiece; // move piece to new tile
            board[yt][xt].hasPiece = true;
            board[y][x].removePiece();

            whiteTurn = !whiteTurn;
            if (whiteTurn) {
                moveCount++;
            }
            specialMoves(x, y, xt, yt, fPiece);

            moves.add(moveString);
        }else{
            System.out.println("wrong move");
            System.out.println(toFen()+" "+moveString);
            //System.exit(1);
        }
    }
    // -----------------LEGALMOVE ---------------------------
    // is blank square
    public Boolean blankSq(int xt, int yt){
        if(inside(xt, yt)) {
            return !board[yt][xt].hasPiece;
        }else{
            return false;
        }
    }
    // inside valuerange
    public Boolean inside(int xt, int yt){
        return (xt >= 0) && (xt < 8) && (yt >= 0) && (yt < 8);
    }
    // from (x,y) ==> to (xt,yt)
    public Boolean legalMove(int x, int y, int xt, int yt){ // if given move is lega
        if (inside(x, y) && inside(xt, yt)) {
            Piece fPiece = board[y][x].chessPiece;
            Piece tPiece = board[yt][xt].chessPiece; // from piece and topiece
            boolean isBlank = blankSq(xt, yt);
            boolean isOpposite = false;
            if (!isBlank) { // landing on piece
                isOpposite = tPiece.color != fPiece.color;
            }
            return board[y][x].hasPiece && (isBlank || isOpposite);
        }else{
            return false;
        }
    }
    // ------------BOARD CONVERSION ----------
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
        result += " "+ enPassantSquare + " 0 "+ moveCount;

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

    public GridPane createBoard() {
        final int size = 8;
        final int squareSize = 50;
        GridPane gridPane = new GridPane();

        for (int row = 1; row <= size; row++) {
            for (int col = 1; col <= size; col ++) {
                createSquare(gridPane, row, col, squareSize);
            }
        }
        return gridPane;
    }

    public void createSquare(GridPane gridPane, int col, int row, int size) {
        StackPane square = new StackPane();     //creates new square obj
        Tile squareButton = board[col-1][row-1];
        Piece piece;
        String color;

        square.setPrefSize(size,size);          //sets a preferred size
        squareButton.setPrefSize(50, 50);

        if (squareButton.highLight) {
            color = "yellow";
        }else if (squareButton.tileColorWhite){
            color = "white";
        }else{
            color = "gray";
        }
        if(squareButton.hasPiece) { // if has piece
            piece = squareButton.chessPiece;
            ImageView pieceIcon = new ImageView(piece.icon);
            pieceIcon.setFitHeight(size);
            pieceIcon.setFitWidth(size);
            square.getChildren().add(pieceIcon);
        }
        squareButton.setOnAction(e->cnt.click(row-1,col-1));
        squareButton.setOpacity(0);
        square.getChildren().add(squareButton);

        square.setStyle("-fx-background-color: "+color+";");
        gridPane.add(square, row, col);
    }

    public boolean humanClick(int x, int y) { // maybe possible of board should be known beforehand
        System.out.println("click");
        if (board[y][x].hasPiece && board[y][x].chessPiece.color == whiteTurn) {
            resetHighlight();
            board[y][x].possible(this); // calculate possible moves by this piece
            humanPiece = new int[]{x, y}; // set human piece.
            return false;
        }else if (board[y][x].highLight) { // highlights is cleared when moved.
            move(humanPiece[0], humanPiece[1], x, y);
            return true;
        }else{ // clicks away the highlight
            resetHighlight();
            return false;
        }
    }


}
/*
MATT:
1. kongen er under trussel (aka sjakk)
2. kongen har ingen felt å gå til
3. det er ikke noe trekk kongens farge kan gjøre for å forhindre trusselen til kongen
 */