package main;

import java.util.Arrays;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import main.pieces.Bishop;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Piece;
import main.pieces.Queen;
import main.pieces.Rook;

public class PromotionDialog extends Dialog<Piece> {
    private Piece selectedPiece;

    int size = 80;
    String color;

    public PromotionDialog(Pawn pawn){
        setTitle("Promote Pawn");
        setResultConverter(f -> selectedPiece);

        HBox hBox = new HBox();
        hBox.getChildren().add(new PromotionCandidate(new Queen(pawn.getColor())));
        hBox.getChildren().add(new PromotionCandidate(new Knight(pawn.getColor())));
        hBox.getChildren().add(new PromotionCandidate(new Rook(pawn.getColor())));
        hBox.getChildren().add(new PromotionCandidate(new Bishop(pawn.getColor())));
        getDialogPane().setContent(hBox);
}

    private class PromotionCandidate extends Label {
        Piece piece;

        PromotionCandidate(Piece piece){
            piece.setIcon();
            ImageView pieceIcon = new ImageView(piece.icon);
            pieceIcon.setFitHeight(size);
            pieceIcon.setFitWidth(size);
            setGraphic(pieceIcon);
            this.piece = piece;
            setOnMouseReleased(this::onMouseReleased);
        }

        private void onMouseReleased(MouseEvent mouseEvent) {
            selectedPiece = piece;
            checkColor(selectedPiece);
            System.out.println(color + " pawn upgraded to " + piece.name);
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            close();
            mouseEvent.consume();
        }

        private void checkColor(Piece piece){
            if (piece.color)
                color = "White";
            else
                color = "Black";
        }
    }
    public char getType(){
        return selectedPiece.type;
    }
}

