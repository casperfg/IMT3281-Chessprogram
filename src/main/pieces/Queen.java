package main.pieces;

import javafx.scene.image.Image;

public class Queen extends Piece {
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_queen.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_queen.png"));
        }
    }
    public void possible() {
        System.out.println("queen");
    }
}
