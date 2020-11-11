package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class ChessProgram extends Application {

    Chessboard cb = new Chessboard();
    EngineHandler eng = new EngineHandler();

    boolean aniGoing = true; // animation timer is running

    //================== Internationalization variables ==================
    String defaultLanguage = "en";
    String defaultCountry = "UK";
    String currentLanguage;
    ResourceBundle messages; //initializing resource bundle

    //================== Menu-bar  ==================
    MenuBar menubar = new MenuBar(); //creates menubar
    Menu file = new Menu(); //creating file in menu bar
    Menu settings = new Menu(); //creating settings in menu bar
    Menu langSubMenu = new Menu(); //submenu for language
    Menu help = new Menu();
    MenuItem about = new MenuItem();
    MenuItem Norwegian = new MenuItem(); //Norwegian as a choice
    Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg")); //fetches from res folder
    MenuItem English = new MenuItem(); //English as a choice
    Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg")); //fetches from res folder
    boolean startUp = true;

    Stage aboutStage = new Stage();



    @Override
    public void start(Stage primaryStage) {
        setLanguage(defaultLanguage,defaultCountry); //sets default language
        English.setDisable(true); //disable default language button

        final int size = 8;
        BorderPane borderPane = new BorderPane();
        setMenuBar();

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
                //cb.humanClick(1,0);
                if(!aniGoing){
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




    public void setLanguage(String language, String country){  //set language for program
        language = language.toUpperCase(); //makes uppercase
        country = country.toUpperCase(); //makes uppercase
        Locale currentLocale; //defines locale variable
        switch (language) { //switch for different languages.
            case "EN" -> { English.setDisable(true); Norwegian.setDisable(false); currentLocale = new Locale(language, country); }//sets language to english.
            case "NO" -> { Norwegian.setDisable(true); English.setDisable(false); currentLocale = new Locale(language, country);} //sets language to norwegian.
            default -> currentLocale = new Locale("en", "UK"); //sets language to english as default;
        }
        currentLanguage = language;
        messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle
        setMenuBar();

    }


    public ImageView setIcon(Image flagIcon) { //gets an image as parameter and adds to imageView
        ImageView flag = new ImageView(flagIcon); //creates new imageview
        flag.setFitHeight(10); //set height
        flag.setFitWidth(15); //set width
        return flag; //return ImageView-object
    }


    public void setMenuBar(){
        //================== File ==================
        file.setText(messages.getString("File"));
        System.out.println(messages.getString("File"));

        //================== Settings ==================
        settings.setText(messages.getString("Settings")); //creating settings in menu bar
        langSubMenu.setText(messages.getString("Language")); //submenu for language

        //================== Language Item + Icons ==================
        Norwegian.setText(messages.getString("Norwegian")); //Norwegian as a choice
        Norwegian.setGraphic(setIcon(norFlag)); //set icon

        English.setText(messages.getString("English")); //English as a choice
        English.setGraphic(setIcon(UKFlag)); //set icon

        //================== Help ==================
        help.setText(messages.getString("Help"));
        about.setText(messages.getString("About"));

        //================== Language Item event handler ==================

        Norwegian.setOnAction(e -> {
            setLanguage("no", "NO"); //function to change language

        });

        English.setOnAction(e -> {
            setLanguage("en", "UK"); //function to change language

        });

        about.setOnAction(e -> {
            try {
                getAbout();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });


        if(startUp){
            startUp = false;
            help.getItems().add(about);
            langSubMenu.getItems().addAll(Norwegian, English); //adds item to language
            settings.getItems().add(langSubMenu); //adds language under settings
            menubar.getMenus().addAll(file, settings, help); //add all menus to menubar
        }



    }

    private void getAbout() throws IOException {

        VBox layout = new VBox(); //creates vbox layout
        Scene aboutScene = new Scene(layout); //creates scene
        Text titleText = new Text(); //creates text object
        Text dateText = new Text();
        Text inputText = new Text();
        String input = new String(); //creates string to insert the input into
        String displayText = new String(); //creates string that will display the entire file
        BufferedReader txtReader; //creates BufferedReader object
        System.out.println(currentLanguage);

        txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/text/about_"+currentLanguage.toUpperCase()+".txt")));


        String title = txtReader.readLine(); //reads first line
        String date = txtReader.readLine(); //reads date
        while(txtReader.ready()) { //while there still is text to ready
            input = input.concat(txtReader.readLine()); //read line and add text to string
        }
        txtReader.close(); //closes the reader


        titleText = setStyling(titleText,title,30, "BOLD"); //defines styling parameters
        inputText = setStyling(inputText,input, 15, "NORMAL"); //not good practice hardcoding in values
        dateText = setStyling(dateText,date,20, "SEMI-BOLD"); //but the textfile is static



        layout.getChildren().addAll(titleText, inputText, dateText); //adds all text objects to layout

        aboutStage.setScene(aboutScene); //add scene to stage(frame)
        aboutStage.show(); //display the stage

    }


    public Text setStyling(Text text, String input,  int fontSize, String weight){ //sets styling to text
        text.setText(input + "\n"); //create seperator
        text.setTextAlignment(TextAlignment.CENTER); //center text
        text.setFont(Font.font("verdana", FontWeight.findByName(weight), FontPosture.REGULAR, fontSize)); //sets font, boldness, posture and size
        text.setWrappingWidth(400); //size before wrapping
        return text; //returns text object
    }



}
