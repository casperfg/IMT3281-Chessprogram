package main;

public class Bishop extends Piece{
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{1,-1},{1, 1},{-1,1},{-1, -1}};
}
