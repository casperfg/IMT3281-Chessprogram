package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ChessProgram extends Application {
    public GridPane chessboard;
    public boolean aniGoing = true;
    //================== Initialize internationalization ==================
    String currentLanguage;
    String currentCountry;
    ResourceBundle messages; //initializing resource bundle

    //================== Menu-bar  ==================
    MenuBar menubar = new MenuBar(); //creates menubar
    Menu file = new Menu(); //creating file in menu bar
    Menu settings = new Menu(); //creating settings in menu bar
    Menu langSubMenu = new Menu(); //submenu for language
    Menu help = new Menu();
    MenuItem about = new MenuItem();
    MenuItem rules = new MenuItem();

    Menu currentDiff = new Menu();

    //================== Difficulties ==================
    Menu difficulty = new Menu();
    MenuItem diff_carlsen = new MenuItem();
    MenuItem diff_gm = new MenuItem();
    MenuItem diff_lahl = new MenuItem();
    MenuItem diff_hard = new MenuItem();
    MenuItem diff_normal = new MenuItem();
    MenuItem diff_easy = new MenuItem();



    MenuItem restartMenu = new MenuItem();
    MenuItem Norwegian = new MenuItem(); //Norwegian as a choice
    Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg")); //fetches from res folder
    MenuItem English = new MenuItem(); //English as a choice
    Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg")); //fetches from res folder
    boolean startUp = true;
    public String game;
    BorderPane borderPane;
    Stage cb = new Stage(); //main chessboard stage


    //================== Confirmation-dialogue ==================
    Alert confirmation = new Alert(AlertType.CONFIRMATION); //alert object type confirmation


    //================== about window ==================
    Stage helpStage = new Stage();

    //================== final variables ==================
    final int WINDOW_WITH = 600;
    final int WINDOW_HEIGHT = 600;

    final int CARLSEN = 2862;
    final int GRANDMASTER = 2500;
    final int LAHLUM = 2192;
    final int HARD = 1200;
    final int NORMAL = 900;
    final int EASY = 300;

    Controller controller = new Controller();

    public ChessProgram(String game) throws IOException {
        controller.game = game;
        this.game = game;
        if(game.contains("h")){ // set program pointer
            controller.programPtr = this; // used on humanclick in chessboard
            // to update the chessprogram board.
        }
    }
    @Override
    public void start(Stage chessBoardStage) {
        setLanguage(currentLanguage,currentCountry); //sets language
        cb = chessBoardStage;
        borderPane = new BorderPane();
        currentDiff.setText("ELO rating: " + controller.elo);
        setMenuBar();

        chessboard = createChessBoard();
        borderPane.setCenter(createChessBoard());
        borderPane.setTop(menubar);

        chessBoardStage.setScene(new Scene(borderPane, WINDOW_WITH, WINDOW_HEIGHT));
        chessBoardStage.setResizable(false);

        chessBoardStage.setOnCloseRequest( windowEvent -> {controller.stopEngine();
        StartMenu sm = new StartMenu();
        Stage smStage = new Stage();
        sm.setStartUpLanguage(currentLanguage, currentCountry);
        sm.start(smStage);
        });

        chessBoardStage.show();
        if(controller.game.equals("e-e")){
            animationEngEng();
        }


    }

    public void animationEngEng(){ // use on engine vs engine
        new AnimationTimer() { // mainloop of the program (controller??)
            @Override
            public void handle(long currentNanoTime) {
                if(currentNanoTime%100 == 0) {
                    System.out.println(currentNanoTime);
                    if (controller.mainLoop()) {
                        System.out.println("update");
                        updateBoard();
                    }
                }
            }
        }.start(); // start main animation loop
    }
    public void animationEngMove(){
        new AnimationTimer() { // mainloop of the program (controller??)
            @Override
            public void handle(long currentNanoTime) {
                if(controller.mainLoop()){
                    updateBoard();
                    this.stop();
                }
            }
        }.start(); // start main animation loop
    }

    public void updateBoard(){
        chessboard = createChessBoard(); // update new chessboard view
        borderPane.setCenter(chessboard); // set the new chessboardView
    }
    public GridPane createChessBoard() {
        final int size = 10;
        GridPane gridPane = controller.chessboard.createBoard();

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
        currentLanguage = language; //current language
        currentCountry = country; //current country
        messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle
        setMenuBar(); //call function to update text

    }


    public ImageView setIcon(Image flagIcon) { //gets an image as parameter and adds to imageView
        ImageView flag = new ImageView(flagIcon); //creates new imageview
        flag.setFitHeight(10); //set height
        flag.setFitWidth(15); //set width
        return flag; //return ImageView-object
    }


    public void setMenuBar(){ //fetches text and initialises the menu bar
        addFileMenu();
        addSettingsMenu();
        addHelpMenu();
        cb.setTitle(messages.getString("Chess")); //set title, its here because it need to update

        //================== add items  ==================
        if(startUp){ //runs only when the program first starts
            startUp = false; //set to false
            help.getItems().addAll(about, rules); //adds items to help menu
            langSubMenu.getItems().addAll(Norwegian, English); //adds item to language

            settings.getItems().add(langSubMenu); //adds language under settings
            settings.getItems().add(difficulty);

            difficulty.getItems().addAll(diff_carlsen, diff_gm, diff_lahl, diff_hard, diff_normal, diff_easy);

            file.getItems().add(restartMenu);
            menubar.getMenus().addAll(file, settings, help,currentDiff); //add all menus to menubar
        }
    }

    private void getHelp(String type) throws IOException {
        type = type.toUpperCase();

        //================== initialising attributes ==================
        VBox layout = new VBox(); //creates vbox layout
        Scene aboutScene = new Scene(layout); //creates scene
        Text titleText = new Text(); //creates text object
        Text dateText = new Text(); //date text
        Text inputText = new Text(); //input text
        String input = ""; //creates string to insert the input into
        BufferedReader txtReader; //creates BufferedReader object



        //================== Read text from file ==================
        switch (type) { //switch for different languages.
            case "ABOUT" -> txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/text/about_" + currentLanguage.toUpperCase() + ".txt")));
            case "RULES" -> txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/text/rules_" + currentLanguage.toUpperCase() + ".txt")));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        String title = txtReader.readLine(); //reads first line
        String date = txtReader.readLine(); //reads date
        while(txtReader.ready()) { //while there still is text to ready
            input = input.concat(txtReader.readLine()); //read line and add text to string
        }
        txtReader.close(); //closes the reader

        //================== Set styling to text ==================
        titleText = setStyling(titleText,title,30, "BOLD"); //defines styling parameters
        inputText = setStyling(inputText,input, 15, "NORMAL"); //not good practice hardcoding in values
        dateText = setStyling(dateText,date,20, "SEMI-BOLD"); //but the textfile is static

        layout.getChildren().addAll(titleText, inputText, dateText); //adds all text objects to layout

        helpStage.setScene(aboutScene); //add scene to stage(frame)
        helpStage.show(); //display the stage
    }


    public Text setStyling(Text text, String input,  int fontSize, String weight){ //sets styling to text
        text.setText(input + "\n"); //create seperator
        text.setTextAlignment(TextAlignment.CENTER); //center text
        text.setFont(Font.font("verdana", FontWeight.findByName(weight), FontPosture.REGULAR, fontSize)); //sets font, boldness, posture and size
        text.setWrappingWidth(400); //size before wrapping
        return text; //returns text object
    }

    public void setStartUpLanguage(String language, String country){
        currentLanguage = language;
        currentCountry = country;
    }


    public void restartGame() { //restart game by closing and creating new window.
        controller.stopEngine();
        controller.chessboard = new Chessboard(controller);
        controller.engineHandler = new EngineHandler(controller.elo, controller.thinkTime);
        controller.engineRunning = true;
        updateBoard();
    }

    public void setConfirmation() throws IOException { //set confirmation text and function to handle action.
        ButtonType cancel = new ButtonType(messages.getString("Cancel"), ButtonData.CANCEL_CLOSE);
        ButtonType ok = new ButtonType(messages.getString("OK"), ButtonData.OK_DONE);
        confirmation.setTitle(messages.getString("Confirm")); //set title text
        confirmation.setHeaderText(messages.getString("RestartMessage")); //set header text

        confirmation.getButtonTypes().setAll(ok, cancel); //add buttons

        Optional<ButtonType> result = confirmation.showAndWait(); //display dialogue and wait for input
        if (result.get() == ok){ //if presses, calls restart function and closes message.
            confirmation.close();
            restartGame();
        }
        confirmation.close();
    }



    public void addLanguageMenu(){
        settings.setText(messages.getString("Settings")); //creating settings in menu bar
        langSubMenu.setText(messages.getString("Language")); //submenu for language

        Norwegian.setText(messages.getString("Norwegian")); //Norwegian as a choice
        Norwegian.setGraphic(setIcon(norFlag)); //set icon

        English.setText(messages.getString("English")); //English as a choice
        English.setGraphic(setIcon(UKFlag)); //set icon

        Norwegian.setOnAction(e -> {
            setLanguage("no", "NO"); //function to change language
        });

        English.setOnAction(e -> {
            setLanguage("en", "UK"); //function to change language
        });
    }

    public void addDifficulties(){
        difficulty.setText(messages.getString("Difficulty"));

        diff_carlsen.setText("Magnus Carlsen");
        diff_gm.setText(messages.getString("Grandmaster"));
        diff_lahl.setText("Hans Olav Lahlum");
        diff_hard.setText(messages.getString("Hard"));
        diff_normal.setText(messages.getString("Normal"));
        diff_easy.setText(messages.getString("Easy"));

        diff_carlsen.setOnAction(e -> setDifficulty(CARLSEN));
        diff_gm.setOnAction(e -> setDifficulty(GRANDMASTER));
        diff_lahl.setOnAction(e -> setDifficulty(LAHLUM));
        diff_hard.setOnAction(e -> setDifficulty(HARD));
        diff_normal.setOnAction(e -> setDifficulty(NORMAL));
        diff_easy.setOnAction(e -> setDifficulty(EASY));
    }

    public void addHelpMenu(){
        help.setText(messages.getString("Help"));
        rules.setText(messages.getString("Rules"));
        about.setText(messages.getString("About"));

        about.setOnAction(e -> { //calls function to display about
            try { getHelp("About"); //function
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        rules.setOnAction(e -> { //calls function to display rules
            try {
                getHelp("Rules"); //function
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void addSettingsMenu(){
        addLanguageMenu();
        addDifficulties();
    }

    public  void addFileMenu(){
        file.setText(messages.getString("File"));
        restartMenu.setText(messages.getString("Restart"));

        restartMenu.setOnAction(e -> {
            confirmation.setContentText(messages.getString("sure"));
            try {
                setConfirmation();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void setDifficulty(int elo) {
        controller.elo = elo;
        currentDiff.setText("ELO rating: " + controller.elo);
        System.out.println("Elo rating  " + controller.elo);
    }

}
