package main;

import main.pieces.Piece;

public class Move implements java.io.Serializable {
    public int x, y, xt, yt;
    public String moveString;

    public Move(int x, int y, int xt, int yt) {
        this.x = x;
        this.y = y;
        this.xt = xt;
        this.yt = yt;
    }
}
