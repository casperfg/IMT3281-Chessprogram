package main.pieces;

import javafx.scene.image.Image;

public class Bishop extends Piece{
    // Defines how the piece moves (Dx, Dy)
    public int[][] moveVector = new int[][]{{1,-1},{1, 1},{-1,1},{-1, -1}};
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_bishop.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_bishop.png"));
        }
    }
    public void possible() {
        System.out.println("bishop");
    }

}
