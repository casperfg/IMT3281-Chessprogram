package main.pieces;

import main.Chessboard;
import main.Tile;

import java.util.ArrayList;
import javafx.scene.image.Image;
public class King extends Piece{
    public ArrayList<int[]> moves = new ArrayList<>();
    public void setIcon(){
        if(color){
            icon = new Image(getClass().getResourceAsStream("/images/white_king.png"));
        }else{
            icon = new Image(getClass().getResourceAsStream("/images/black_king.png"));
        }
    }
    public void possible() {
        System.out.println("king");
    }
}
