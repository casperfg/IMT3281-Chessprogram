package main;

import javafx.scene.control.*;
import main.Chessboard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Random;
import java.util.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;



import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.pieces.*;

import javax.swing.*;


public class ChessProgram extends Application {


    Locale currentLocale = setLanguage("en", "UK");




    @Override
    public void start(Stage primaryStage) {
        ResourceBundle messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle.
        System.out.println(currentLocale);
        final int size = 8;
        Chessboard board = new Chessboard();
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane square = new StackPane();
                String color;
                if ((row + col) % 2 == 0) {
                    color = "white";
                } else {
                    color = "black";
                }
                square.setStyle("-fx-background-color: " + color + ";");
                gridPane.add(square, col, row);
            }
        }

        for (int i = 0; i < size; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }

//================== File ==================
        Menu file = new Menu(messages.getString("File"));

//================== Settings ==================
        Menu settings = new Menu(messages.getString("Settings")); //creating settings in menu bar
        Menu langSubMenu = new Menu(messages.getString("Language")); //submenu for language

//================== Language Item + Icons ==================
        MenuItem Norwegian = new MenuItem(messages.getString("Norwegian")); //Norwegian as a choice
        Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg")); //fetches from res folder
        Norwegian.setGraphic(setIcon(norFlag)); //set icon

        MenuItem English = new MenuItem(messages.getString("English")); //English as a choice
        Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg")); //fetches from res folder
        English.setGraphic(setIcon(UKFlag)); //set icon

//================== Language Item event handler ==================
        Norwegian.setOnAction(e -> {
            Locale currentLocale = setLanguage("no", "NO");
            start(primaryStage);
                });




        langSubMenu.getItems().addAll(Norwegian, English); //adds item to language
        settings.getItems().add(langSubMenu); //adds language under settings


//================== Help ==================
        Menu help = new Menu(messages.getString("Help"));
        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(file, settings, help);


        borderPane.setTop(menubar);
        borderPane.setCenter(gridPane);
        primaryStage.setScene(new Scene(borderPane, 600, 600));
        primaryStage.show();
    }




    public Locale setLanguage(String language, String country){  //set language for program
        language = language.toUpperCase(); //makes uppercase
        country = country.toUpperCase(); //makes uppercase
        Locale currentLocale = null; //defines locale variable
        switch (language) { //switch for different languages.
            case "EN" -> currentLocale = new Locale(language, country); //sets language to english.
            case "NO" -> currentLocale = new Locale(language, country); //sets language to norwegian.
            default -> currentLocale = new Locale("en", "UK"); //sets language to english as default;
        }
        return currentLocale; //return locale object.
    }

    public Locale getLanguage(){

        return  null;
    }



    public ImageView setIcon(Image flagIcon){ //gets an image as parameter and adds to imageView
        ImageView flag = new ImageView(flagIcon); //creates new imageview
        flag.setFitHeight(10); //set height
        flag.setFitWidth(15); //set width
        return  flag; //return ImageView-object
    }

}
