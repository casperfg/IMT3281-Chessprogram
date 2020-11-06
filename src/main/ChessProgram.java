package main;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ChessProgram extends Application {

    @Override
    public void start(Stage primaryStage) {
        final int size = 8;
        Chessboard board = new Chessboard(true);
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col ++) {
                StackPane square = new StackPane();
                String color ;
                if ((row + col) % 2 == 0) {
                    color = "white";
                } else {
                    color = "black";
                }
                square.setStyle("-fx-background-color: "+color+";");
                gridPane.add(square, col, row);
            }
        }

        for (int i = 0; i < size; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }

        Menu file = new Menu("File"); //Må endres til å lese fra fil mtp internasjonalisering.
        Menu settings = new Menu("Settings");
        Menu help = new Menu("Help");
        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(file,settings,help);


        borderPane.setTop(menubar);
        borderPane.setCenter(gridPane);
        primaryStage.setScene(new Scene(borderPane, 600, 600));
        primaryStage.show();
    }
}
