package main;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class ChessProgram extends Application {

    Chessboard cb = new Chessboard();
    EngineHandler eng = new EngineHandler();
    String defaultLanguage = "en";
    String defaultCountry = "UK";
    Locale currentLocale = setLanguage(defaultLanguage, defaultCountry); //sets default language to english.
    boolean aniGoing = true; // animation timer is running

    // TODO: slit start and text setting.
    @Override
    public void start(Stage primaryStage) {
        ResourceBundle messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle.

        System.out.println(currentLocale);
        final int size = 8;
        BorderPane borderPane = new BorderPane();

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
            currentLocale = setLanguage("no", "NO");
            aniGoing = false;
            start(primaryStage);
        });

        English.setOnAction(e -> {
            currentLocale = setLanguage("en", "UK");
            aniGoing = false;
            start(primaryStage);
        });


        langSubMenu.getItems().addAll(Norwegian, English); //adds item to language
        settings.getItems().add(langSubMenu); //adds language under settings


//================== Help ==================
        Menu help = new Menu(messages.getString("Help"));
        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(file, settings, help);

        GridPane chessboard = createChessBoard();
        borderPane.setTop(menubar);
        borderPane.setCenter(chessboard);

        primaryStage.setScene(new Scene(borderPane, 500, 500));
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.show();

        borderPane.setCenter(createChessBoard()); // set the new chessboard

        if (eng.checkWorker().equals("-1")) {
            eng.getBest(cb);
        }
        // needs to be outside mainloop somehow.
        // starts the engine thread
        new AnimationTimer() { // mainloop of the program (controller??)
            @Override
            public void handle(long currentNanoTime) {
                String ret = eng.checkWorker(); // check workerThread
                if (!ret.equals("-1")) { // is -1 when workerthread is still working
                    cb.move(ret); // move the best move
                    GridPane chessboard = createChessBoard(); // update new chessboard view
                    borderPane.setCenter(chessboard); // set the new chessboardView
                    eng.getBest(cb); // start new
                }
<<<<<<< HEAD
                //cb.humanClick(1,0);
                if(!aniGoing){
=======
                cb.humanClick(1, 0);
                if (!aniGoing) {
>>>>>>> 7d197855d8b51b3ff9528dd4de319ba75c55cdcb
                    aniGoing = true;
                    this.stop();
                }
            }
        }.start(); // start main animation loop

    }

    public GridPane createChessBoard() {
        final int size = 10;
        GridPane gridPane = cb.createBoard();

        for (int i = 0; i < size; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE,
                    Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE,
                    Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
        labelBoard(gridPane);
        return gridPane;
    }


    void labelBoard(GridPane gridPane) {
        final String[] letterLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
        final String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};
        for (int i = 8; i > 0; i--) {
            gridPane.add(new Label(letterLabels[i - 1]), i, 0);
            gridPane.add(new Label(letterLabels[i - 1]), i, 9);
            gridPane.add(new Label(numbers[i - 1]), 9, i);
            gridPane.add(new Label(numbers[i - 1]), 0, i);
        }
    }


    public Locale setLanguage(String language, String country) {  //set language for program
        language = language.toUpperCase(); //makes uppercase
        country = country.toUpperCase(); //makes uppercase
        Locale currentLocale; //defines locale variable
        switch (language) { //switch for different languages.
            case "EN" -> currentLocale = new Locale(language, country); //sets language to english.
            case "NO" -> currentLocale = new Locale(language, country); //sets language to norwegian.
            default -> currentLocale = new Locale("en", "UK"); //sets language to english as default;
        }
        return currentLocale; //return locale object.
    }


    public ImageView setIcon(Image flagIcon) { //gets an image as parameter and adds to imageView
        ImageView flag = new ImageView(flagIcon); //creates new imageview
        flag.setFitHeight(10); //set height
        flag.setFitWidth(15); //set width
        return flag; //return ImageView-object
    }

}
