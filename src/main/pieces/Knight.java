package main.pieces;

import javafx.scene.image.Image;

public class Knight extends Piece { // Hest
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}, {1,-2} };
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_knight.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_knight.png"));
        }
    }
    public void possible() {
        System.out.println("knight");
    }
}
