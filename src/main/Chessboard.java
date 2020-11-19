package main;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import main.pieces.Pawn;
import main.pieces.Piece;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Character.isDigit;

public class Chessboard {
    public Tile[][] board = new Tile[8][8];
    public boolean checkForChecks = true;

    public boolean whiteTurn = true;
    public boolean check = false;
    public boolean whiteCastle = false;
    public boolean blackCastle = false;

    public String enPassantSquare = "-"; // '-' if no passantsquare
    public int[] pawnPassant;
    public int moveCount = 0; // increments after black move

    public int repetition = 0;
    public String moveString;
    public ArrayList<String> moves = new ArrayList<>();
    public ArrayList<int[]> checkAvoid = new ArrayList<>(); // pieces that can avoid check. (x,y)
    public ArrayList<int[]> whitePieces = new ArrayList<>();
    public ArrayList<int[]> blackPieces = new ArrayList<>();


    public char promotionTo = '-';
    public Controller cnt = null; // controller pointer.

    public String piecesLeftIndex = "rnbkqp";
    public int[] piecesLeft = new int[]{4, 4, 4, 2, 2, 16};

    public int[] humanPiece = new int[2]; // piece responcible for highlights. (x,y)

    public String highlightColor = "yellow";
    public String tileColorA = "white";
    public String tileColorB = "grey";

    public boolean ismate = false;


    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public Chessboard(Controller cnt) {
        makeStart();
        this.cnt = cnt;
        check = false;
    }
    public Chessboard(Chessboard chBoard, String fen, Controller cnt){
        whitePieces = (ArrayList<int[]>) chBoard.whitePieces.clone();
        blackPieces = (ArrayList<int[]>) chBoard.blackPieces.clone();
        setFen(fen);
        checkForChecks = false;
        check = false;
        this.cnt = cnt;
    }
    public int arrContains(ArrayList<int[]> ar, int[] array){
        int x, y;
        for(int i = 0; i<ar.size(); i++){
            x = ar.get(i)[0]; y = ar.get(i)[1];
            if(x == array[0] && y == array[1]){
                return i;
            }
        }
        return -1;
    }
    public ArrayList<int[]> getPieceList(boolean color){
        if(color){
            return whitePieces;
        }else{
            return blackPieces;
        }
    }

    public void makeStart() { // setup start position
        int size = 8;
        String line = "rnbqkbnr"; // n=knight
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[y][x] = new Tile();
                if (y == 7 || y == 0){
                    makeTile(x, y, line.charAt(x), y == 7);
                }else if (y == 6 || y == 1){
                    makeTile(x, y, 'p', y == 6);
                }else{
                    makeTile(x,y, '-', false);
                }
            }
        }
    }

    public void makeTile(int x, int y, char type, boolean color) {
        if(type != '-'){
            if(color){
                whitePieces.add(new int[]{x,y});
            }else{
                blackPieces.add(new int[]{x,y});
            }
            board[y][x].updatePiece(type);
        }
        board[y][x].setProp(new int[]{x, y}, color);
    }

    public void resetHighlight() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[y][x].highLight = false;
            }
        }
    }
    // ------------MOVE-----------------
    // converts move to save friendly format
    public void moveStringSet(Piece fPiece, int x, int y, int xt, int yt) {
        moveString = "";
        if (fPiece.type != 'p') {
            moveString = Character.toString(fPiece.type).toUpperCase();
        } else { // is pawn
            if (!blankSq(xt, yt)) { // is taking piece, must reference square (feks: exg3)
                moveString = Character.toString(board[y][x].tileName.charAt(0));
            }
        }
        if (!blankSq(xt, yt)) {
            moveString += "x";
        }
        moveString += board[yt][xt].tileName; // the tilename it hits
        if (promotionTo != '-') { // pawn promotion
            moveString += Character.toString(promotionTo).toUpperCase();
        }
        if(check){
            moveString += "+"; // TODO: bugged
        }
    }

    public void repetition(int xt, int yt, Piece fPiece) {
        if (fPiece.lastPosition[0] == xt && fPiece.lastPosition[1] == yt) { // 4x repetition ==> stalemate
            repetition += 1;
        } else {
            repetition = 0;
        }
    }
    // checks if color attacks the opposite color
    public boolean kingAttack(boolean color){
        Tile thisTile;
        ArrayList<int[]> piecePos = getPieceList(color);
        for(int[] pos : piecePos){ // loops all the pieces of this color
            thisTile = board[pos[1]][pos[0]];
            if (thisTile.kingAttack(this)) {
                return true;
            }
        }
        return false;
    }
    // can this color avoid checkmate. keeps only the possible moves that avoids check/mate
    public void calcCheckAvoid(boolean color){ // should be whiteTurn
        Chessboard tmpBoard = new Chessboard(this, toFen(), cnt); // make a copy of the board.

        Tile thisTile;
        ArrayList<int[]> piecePos = getPieceList(color); // piece position for this color
        ArrayList<int[]> tmpPossible; // possible moves
        Piece cp;
        boolean avoided = false; // avoided check or not
        String fen = this.toFen(); // save the fen
        int xt, yt;
        checkAvoid.removeAll(checkAvoid); // reset checkavoid array list

        for(int[] pos : piecePos){ // loop all chess pieces
            thisTile = board[pos[1]][pos[0]]; // get the tile
            cp = thisTile.chessPiece; // get the chesspiece

            thisTile.possible(this, false); // get possible moves for this piece
            tmpPossible = (ArrayList<int[]>) thisTile.retPossible().clone(); // clone the possible moves
            cp.removePossible(); // remove every possible move in the piece

            for(int i = 0; i<tmpPossible.size(); i++){ // loop all the possible moves
                // move from piece position to possible
                tmpBoard = new Chessboard(this, fen, cnt); // resets board position. to original position
                xt = tmpPossible.get(i)[0]; yt = tmpPossible.get(i)[1]; // get where this piece moves
                tmpBoard.move(cp.position[0], cp.position[1], xt, yt); // move this move

                if(!tmpBoard.kingAttack(!color)){ // avoided the check given
                    avoided = true; // keep the possible move if it avoids check.
                    cp.possibleMoves.add(new int[]{xt,yt}); // add back this possible move.
                }
            }
            if(avoided){ // avoided check somehow with this piece
                checkAvoid.add(pos.clone()); // save the pieces that avoids this check
            }
        }
        if(checkAvoid.isEmpty()){
            cnt.ISMATE();
            ismate  = true;
        }
    }
    public void specialMoves(int x, int y, int xt, int yt, Piece fPiece) {
        if (board[yt][xt].tileName.equals(enPassantSquare)) { // is taking enpassant
            updatePieceLeft(board[pawnPassant[1]][pawnPassant[0]].chessPiece, -1);
            board[pawnPassant[1]][pawnPassant[0]].removePiece();
        }
        enPassantSquare = "-"; // reset enpassant.
        if(checkForChecks) { // is human involved. stockfish handels for computer involvement//check = kingAttack(fPiece.color); // checks if check. cleans possible afterwards
            if(cnt.game.contains("h")){
                check = kingAttack(fPiece.color);
                if(check){ // calculate avoidment moves.
                    calcCheckAvoid(whiteTurn);
                }else{
                    check = false;
                }
            }
        }

        // piece specific
        if (fPiece.type == 'k') {
            if (Math.abs(x - xt) != 1 && Math.abs(yt - y) == 0) { // castleing. assumes is legal (threats from black)
                if (!whiteCastle || !blackCastle) {
                    moveString = (((xt - x) == 2) ? "0-0" : "0-0-0"); // true if short castle.
                    board[y][((xt - x) == 2) ? 7 : 0].removePiece(); // remove tower
                    makeTile((xt + x) / 2, y, 'r', fPiece.color); // place new tower
                }
            }
            if (fPiece.color) { // king or tower moved. no castle
                whiteCastle = true;
            } else {
                blackCastle = true;
            }
        }
        if (fPiece.type == 'r') {
            board[yt][xt].chessPiece.rookMoved = true;
        }

        if (fPiece.type == 'p') {
            if ((fPiece.color && yt == 0) || (!fPiece.color && yt == 7)) { // PROMOTION
                makeTile(xt, yt, (promotionTo == '-') ? 'q' : promotionTo, fPiece.color);
                piecesLeft[5] -= 1; // remove pawn from pieces left
                updatePieceLeft(board[yt][xt].chessPiece, 1); // add new piece to piece left.
                promotionTo = '-';
            } else if (yt - y != 1) { // 2 squares up
                enPassantSquare = board[(fPiece.color) ? 5 : 2][xt].tileName;
                pawnPassant = new int[]{xt, yt};
            }
        }
    }

    // computer move
    public void move(String move) { // e2e4, promotion: e6e7q
        String column = "abcdefgh";
        if (move.length() >= 4) {
            int x = column.indexOf(move.charAt(0));
            int y = Character.getNumericValue(move.charAt(1));
            int xt = column.indexOf(move.charAt(2));
            int yt = Character.getNumericValue(move.charAt(3));

            if (move.length() > 4) { // is promotion, set char
                promotionTo = move.charAt(4); // used in SPECIALMOVE
            }
            move(x, 8 - y, xt, 8 - yt); // internalY = 8-External
        } else {
            System.out.println("Engine error");
            if (move.isEmpty()) {
                System.out.println("Den er tom");
            }
            System.exit(1);
        }
    }

    // validates before moving.
    public void move(int x, int y, int xt, int yt, char promotionTo) {
        move(x, y, xt, yt); // move with setting promotionTO.
        this.promotionTo = promotionTo;
    }

    // TODO: update king functionality (castle/check/mate)
    public void move(int x, int y, int xt, int yt) { // from x,y to xt, yt
        Piece fPiece; // piece from
        Piece tPiece; // piece to (taken)
        Boolean taking;
        resetHighlight();
        if (legalMove(x, y, xt, yt)) { // checks if legal
            fPiece = board[y][x].chessPiece;
            taking = board[yt][xt].hasPiece;

            repetition(xt, yt, fPiece); // count repetition of moves
            if (taking) { // update number of officers left.
                tPiece = board[yt][xt].chessPiece;
                updatePieceLeft(tPiece, -1);
            }

            fPiece.position = new int[]{xt, yt};
            fPiece.lastPosition = new int[]{x, y};
            updatePieceList(fPiece); // update the lists that holds where the pieces are
            fPiece.removePossible();
            moveStringSet(fPiece, x, y, xt, yt); // update movelist

            board[yt][xt].chessPiece = fPiece; // move piece to new tile
            board[yt][xt].hasPiece = true;
            board[y][x].removePiece();

            whiteTurn = !whiteTurn; // change turn/count moves
            if (whiteTurn) {

                moveCount++; // used in fen
            }
            specialMoves(x, y, xt, yt, fPiece); // OBS! fpiece is not added afterwards.

            moves.add(moveString);

            // If move was made by local player, send the move via connection
            if (cnt.game.equals("h-o") && !cnt.waitingForMove) {
                Move move = new Move(x, y, xt, yt);
                move.moveString = moves.get(moves.size() - 1);
                try {
                    System.out.println((cnt.isServer ? "Server" : "Client") +
                                        " is trying to send move " + move.moveString);
                    cnt.connection.send(move);
                } catch (Exception e) {
                    System.out.println("Failed to send");
                }
            }

            // If move is coming in via connection, we are no longer waiting for move
            if (cnt.waitingForMove) {
                cnt.waitingForMove = false;
            }

        } else {
            System.out.println("wrong move"); // freezes after this
        }
    }

    public void updatePieceList(Piece cp) {
        int index;
        if(cp.color){
            index = arrContains(whitePieces, cp.lastPosition);
            if (index != -1) {
                whitePieces.remove(index);
            }
            whitePieces.add(cp.position.clone());

        }else{
            index = arrContains(blackPieces, cp.lastPosition);
            if (index != -1) {
                blackPieces.remove(index);
            }
            blackPieces.add(cp.position.clone());
        }
    }

    public void updatePieceLeft(Piece piece, int inc) { // call every time a piece is taken
        try {
            int index = piecesLeftIndex.indexOf(piece.type);
            piecesLeft[index] += inc; // inc=-1 or 1; adds or remove piece
        } catch (Exception ignored) {
        }

    }

    public boolean checkStaleMate() { // takes care of automatic draws.
        // "rnbkqp";
        if (piecesLeft[5] == 0) { // no pawns left on board
            if (Arrays.equals(piecesLeft, new int[]{0, 0, 0, 2, 0, 0})) { // tests if arrays are equal
                System.out.println("king vs king = stalemate");
                return true;
            }
            if (Arrays.equals(piecesLeft, new int[]{0, 1, 0, 2, 0, 0})) {
                System.out.println("king vs bishop & king = stalemate");
                return true;
            }
            if (Arrays.equals(piecesLeft, new int[]{0, 1, 0, 2, 0, 0})) {
                System.out.println("king vs bishop & king = stalemate");
                return true;
            }
            if (Arrays.equals(piecesLeft, new int[]{0, 0, 2, 2, 0, 0})) {
                // tests if the bishops are different colors.
                int i = 0;
                Boolean[] ab = new Boolean[2];
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        if (board[y][x].hasPiece) {
                            if (board[y][x].chessPiece.type == 'b') {
                                ab[i] = board[y][x].tileColorWhite;
                                i += 1;
                            }
                        }
                    }
                }
                return (ab[0] && ab[1]) || (!ab[0] && !ab[1]);
            }
        }
        return false;
    }

    // -----------------LEGALMOVE ---------------------------
    // is blank square
    public Boolean blankSq(int xt, int yt) {
        if (inside(xt, yt)) {
            return !board[yt][xt].hasPiece;
        } else {
            return false;
        }
    }

    // inside valuerange
    public Boolean inside(int xt, int yt) {
        return (xt >= 0) && (xt < 8) && (yt >= 0) && (yt < 8);
    }

    // from (x,y) ==> to (xt,yt)
    public Boolean legalMove(int x, int y, int xt, int yt) { // if given move is lega
        if (inside(x, y) && inside(xt, yt)) {
            Piece fPiece = board[y][x].chessPiece;
            Piece tPiece = board[yt][xt].chessPiece; // from piece and topiece
            boolean isBlank = blankSq(xt, yt);
            boolean isOpposite = false;
            if (!isBlank) { // landing on piece
                try {
                    isOpposite = tPiece.color != fPiece.color;
                } catch (Exception e) {
                    System.out.println(x);
                    System.out.println(y);
                    System.out.println(xt);
                    System.out.println(yt);
                    isBlank = false;
                }
            }
            return board[y][x].hasPiece && (isBlank || isOpposite);
        } else {
            return false;
        }
    }

    // ------------BOARD CONVERSION ----------
    public String toFen() {
        Tile myTile;
        Piece myPiece;
        String turn = ((whiteTurn) ? "w" : "b");
        String wCastle = ((!whiteCastle) ? "KQ" : "");
        String bCastle = ((!blackCastle) ? "kq" : "");
        String result;

        StringBuilder fen = new StringBuilder();
        int empty;

        // state information, castle, enpassant movecount
        if (wCastle.equals("") && bCastle.equals("")) {
            wCastle = "-";
        }
        result = " " + turn + " " + wCastle + bCastle;
        result += " " + enPassantSquare + " 0 " + moveCount;

        // board position
        for (int y = 0; y < 8; y++) {
            empty = 0; // set number of empty squares to 0
            for (int x = 0; x < 8; x++) {
                myTile = board[y][x];
                if (!myTile.hasPiece) {
                    empty += 1;
                } else {
                    myPiece = myTile.chessPiece; // get the chess piece
                    if (empty != 0) { // flush empty squares to fen
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
    public void setFen(String fen){ // piecesLeftIndex
        int i = -1;
        String part;
        char partC;
        whiteTurn = fen.contains("w");
        whiteCastle = fen.contains("KQ");
        blackCastle = fen.contains("kq");

        for(int y = 0; y<8; y++){
            for(int x = 0; x<8; x++){
                board[y][x] = new Tile();
                i++;
                part = fen.substring(i, i+1);

                while(part.equals("/")){
                    i++;
                    part = fen.substring(i, i+1);
                }
                partC = fen.charAt(i);
                if(!isDigit(partC)){
                    makeTile(x,y, Character.toLowerCase(partC), Character.isUpperCase(partC));

                }else{
                    for(int j = x; j<x+Character.getNumericValue(partC); j++){
                        board[y][j] = new Tile();
                        makeTile(j, y, '-', false);
                    }
                    x += Character.getNumericValue(partC)-1;
                }

            }
        }
    }

    public GridPane createBoard() {
        final int size = 8;
        final int squareSize = 50;
        GridPane gridPane = new GridPane();

        for (int row = 1; row <= size; row++) {
            for (int col = 1; col <= size; col++) {
                createSquare(gridPane, row, col, squareSize);
            }
        }
        return gridPane;
    }

    public void createSquare(GridPane gridPane, int col, int row, int size) {
        StackPane square = new StackPane();     //creates new square obj
        Tile thisTile = board[col - 1][row - 1];
        Piece piece;
        String color;

        square.setPrefSize(size, size);          //sets a preferred size
        thisTile.setPrefSize(50, 50);

        if (thisTile.highLight) {
            color = setColor(highlightColor);
        } else if (thisTile.tileColorWhite) {
            color = setColor(tileColorA);
        } else {
            color = setColor(tileColorB);
        }

        if (thisTile.hasPiece) { // if has piece
            piece = thisTile.chessPiece;
            ImageView pieceIcon = new ImageView(piece.icon);
            pieceIcon.setFitHeight(size);
            pieceIcon.setFitWidth(size);
            square.getChildren().add(pieceIcon);
        }
        thisTile.setOnAction(e -> cnt.click(row - 1, col - 1));
        thisTile.setOpacity(0);
        square.getChildren().add(thisTile);
        square.setStyle("-fx-background-color: " + color + ";");
        gridPane.add(square, row, col);
    }
    // tests if move is promotion
    // x,y = from position
    // xt, yt = to position
    public boolean isPromotion(int x, int y, int xt, int yt){
        boolean piece = (board[y][x].chessPiece.type == 'p');
        return piece && (yt == 0 || y == 7);
    }

    public boolean humanClick(int x, int y) { // maybe possible of board should be known beforehand
        System.out.println("click");
        if (x == humanPiece[0] && y == humanPiece[1]) { // clicks on piece again, reset.
            humanPiece[0] = -1;
            humanPiece[1] = -1; // should be able to click on this piece again
            resetHighlight();
            return false;
        } else if (board[y][x].hasPiece && board[y][x].chessPiece.color == whiteTurn) { // clicks on piece with correct turn.
            resetHighlight(); // reset previous highlight.
            if(!check){
                board[y][x].possible(this, true); // calculate possible moves by this piece
                humanPiece = new int[]{x, y}; // set human piece. piece responsible for current highlight
            }else{ // is in a check. and clicks piece that avoids
                if(arrContains(checkAvoid, new int[]{x, y}) != -1){
                    board[y][x].setHighlight(this);
                    humanPiece = new int[]{x, y}; // set human piece. piece responsible for current highlight
                }
            }
            return false;
        } else if (board[y][x].highLight) { // clicks on highlight, move piece
            if(isPromotion(humanPiece[0], humanPiece[1], x, y)){
                setPromotion();
            }
            move(humanPiece[0], humanPiece[1], x, y); // removes highlight/possible
            return true;
        } else { // clicks away the highlight
            resetHighlight();
            return false;
        }
    }

    public void setPromotion(){
        Tile tempTile = new Tile();
        Piece promotedPiece = board[humanPiece[1]][humanPiece[0]].chessPiece;

        PromotionDialog promotionDialog = new PromotionDialog((Pawn)promotedPiece);
        promotionDialog.showAndWait();

        promotionTo = promotionDialog.getType();
        tempTile.updatePiece(promotionTo);

        System.out.println(promotionDialog.getType());
    }

    public String displayMoves(){ //display all moves
        String singleMove = new String();  //string to handle single move
        String allMoves = new String();  //string to handle both moves
        int moveNr = 1; //move number
        int lastMove = 1; //when both have done move

        for(int i = 0; i < moves.size(); i++){ //loops through move arraylist
            if(i % 2 == 0 && i != 0){ //is true every other move
                moveNr++; //increment moveNr
                allMoves = allMoves.concat("\n"); //new line
            }
            if(lastMove == moveNr){ //when "new round" starts
                lastMove++;
                singleMove = String.format("%o. %s \t",moveNr, moves.get(i)); //first move for that "round"
            }else {
                singleMove = String.format("%s \t", moves.get(i)); //countermove
            }
            allMoves = allMoves.concat(singleMove); //add move to string
        }
        return allMoves; //return  string
    }


    public String setColor(String color){
        return color;
    }

    public boolean mateCheck(){ //if mate, sets ismate = true
        return ismate;
    }


}
/*
MATT:
1. kongen er under trussel (aka sjakk)
2. kongen har ingen felt å gå til
3. det er ikke noe trekk kongens farge kan gjøre for å forhindre trusselen til kongen
 */
