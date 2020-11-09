package main.pieces;

import javafx.scene.image.Image;

public class Rook extends Piece {
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_rook.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_rook.png"));
        }
    }
    public void possible() {
        System.out.println("rook");
    }
}
