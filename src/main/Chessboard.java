package main;

public class Chessboard {
    public Piece[][] board = new Piece[8][8];
    public String line = "rhbkqbhr"; // h=knight
    public boolean isWhite = true; // starting color

    public void makeStart(){
        int[] startLine = new int[]{7,1};
        int y = 7;
        for(int i = 0; i<2; i++){
            y = startLine[i];
            for(int x = 0; x<8; x++){
                switch (line.charAt(x)) {
                    case 'h' -> board[y][x] = new Knight();
                    case 'b' -> board[y][x] = new Bishop();
                    case 'r' -> board[y][x] = new Rook();
                    case 'k' -> board[y][x] = new King();
                    case 'q' -> board[y][x] = new Queen();
                }
                board[y][x].color = (y == 7) == isWhite;
            }
        }
    }
    public Chessboard(boolean stBool){
        this.isWhite = stBool;
        makeStart();
    }
}
