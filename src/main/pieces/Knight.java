package main.pieces;

import javafx.scene.image.Image;
import main.Chessboard;

public class Knight extends Piece { // Hest

    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}, {1,-2} };

    public Knight(boolean color) {
        super(color, "knight", 'n');
    }

    public Knight() {}

    public void setIcon() {
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_knight.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_knight.png"));
        }
    }

    public void possible(Chessboard board) {
        nonRepeat(moveVector, board);
    }
}
