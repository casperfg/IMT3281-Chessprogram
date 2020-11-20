package main;

public class Move implements java.io.Serializable {
    public final int x;
    public final int y;
    public final int xt;
    public final int yt;
    public String moveString;

    public Move(int x, int y, int xt, int yt) {
        this.x = x;
        this.y = y;
        this.xt = xt;
        this.yt = yt;
    }
}
