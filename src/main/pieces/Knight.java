package main.pieces;

public class Knight extends Piece { // Hest
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}, {1,-2} };
}
