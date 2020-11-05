package main;

public class Chessboard {
    public Tile[][] board = new Tile[8][8];
    public String line = "rnbkqbnr"; // n=knight
    public boolean isWhite = true; // true if white is at bottom (y=6 || y=7)
    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

    public void makeStart(){
        int[] startLine = new int[]{7,0};
        for(int y = 0; y<8; y++){
            for(int x = 0; x<8; x++){
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
    public Chessboard(boolean stBool){
        this.isWhite = stBool;
        makeStart();
    }
}
