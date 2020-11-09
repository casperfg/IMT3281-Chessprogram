package main.pieces;

import javafx.scene.image.Image;

public class Pawn extends Piece{
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_pawn.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_pawn.png"));
        }
    }
    public void possible() {
        System.out.println("pawn");
    }
}
