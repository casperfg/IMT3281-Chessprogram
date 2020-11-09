package main;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
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

    String language = "en";
    String country = "UK";


    Locale currentLocale = new Locale(language, country);
    ResourceBundle messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale);
    

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Menu file = new Menu(messages.getString("File")); //Må endres til å lese fra fil mtp internasjonalisering.
        Menu settings = new Menu(messages.getString("Settings"));
        Menu help = new Menu(messages.getString("Help"));
        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(file,settings,help);
        GridPane chessboard = createChessBoard();


        borderPane.setTop(menubar);
        borderPane.setCenter(chessboard);
        primaryStage.setScene(new Scene(borderPane, 800, 800));
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public GridPane createChessBoard(){
        final int size = 10;
        final String[] letterLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
        final String[] numbers = {"8", "7", "6", "5", "4", "3","2", "1"};

        GridPane gridPane = new GridPane();
        for (int row = 1; row < size-1; row++) {
            for (int col = 1; col < size-1; col ++) {
                StackPane tileSquare = new StackPane();
                String color;
                if ((row + col) % 2 == 0) {
                    color = "white";
                } else {
                    color = "gray";
                }
                tileSquare.setStyle("-fx-background-color: "+color+";");
                gridPane.add(tileSquare,row, col);
            }
        }
        for (int i = 0; i < size; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE,
                                                Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE,
                                                Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }

        for(int row = 8; row > 0; row-- ) {
            for (int col = 8; col > 0; col--) {
                gridPane.add(new Label(letterLabels[row - 1]), row, 0);
                gridPane.add(new Label(letterLabels[row - 1]), row, 9);
                gridPane.add(new Label(numbers[col-1]), 9, col);
                gridPane.add(new Label(numbers[col-1]), 0, col);
            }
        }


        return gridPane;
    }
}
