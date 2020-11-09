package main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
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


        borderPane.setTop(menubar);
        borderPane.setCenter(createTileGUI());
        primaryStage.setScene(new Scene(borderPane, 700, 700));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public GridPane createTileGUI(){
        final int size = 8;
        GridPane gridPane = new GridPane();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col ++) {
                StackPane tileSquare = new StackPane();
                String color;
                if ((row + col) % 2 == 0) {
                    color = "white";
                } else {
                    color = "black";
                }
                tileSquare.setStyle("-fx-background-color: "+color+";");
                gridPane.add(tileSquare,row, col);
            }
        }
        for (int i = 0; i < size; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }

        return gridPane;
    }
}
