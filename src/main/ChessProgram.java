package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ChessProgram extends Application {
    //================== final variables ==================
    final int WINDOW_WITH = 800;
    final int WINDOW_HEIGHT = 600;
    final int CARLSEN = 2862;
    final int GRANDMASTER = 2500;
    final int LAHLUM = 2192;
    final int HARD = 1200;
    final int NORMAL = 900;
    final int EASY = 300;
    public GridPane chessboard;
    public final String game;
    //================== Initialize internationalization ==================
    String currentLanguage;
    String currentCountry;
    ResourceBundle messages; //initializing resource bundle
    //================== Menu-bar  ==================
    final MenuBar menubar = new MenuBar(); //creates menubar
    final Menu file = new Menu(); //creating file in menu bar
    final Menu settings = new Menu(); //creating settings in menu bar
    final Menu langSubMenu = new Menu(); //submenu for language
    final Menu themeSubMenu = new Menu();
    //================== Help Menu ==================
    final Menu help = new Menu();
    final MenuItem about = new MenuItem();
    final MenuItem rules = new MenuItem();
    //================== Themes ==================
    final MenuItem standardTheme = new MenuItem();
    final MenuItem darkTheme = new MenuItem();
    final MenuItem classicTheme = new MenuItem();
    final MenuItem blueTheme = new MenuItem();
    final MenuItem greenTheme = new MenuItem();
    final MenuItem districtTheme = new MenuItem();
    //================== Difficulties ==================
    final Menu difficultySubMenu = new Menu();
    final MenuItem diff_carlsen = new MenuItem();
    final MenuItem diff_gm = new MenuItem();
    final MenuItem diff_lahl = new MenuItem();
    final MenuItem diff_hard = new MenuItem();
    final MenuItem diff_normal = new MenuItem();
    final MenuItem diff_easy = new MenuItem();
    final MenuItem restartMenu = new MenuItem();
    final MenuItem Norwegian = new MenuItem(); //Norwegian as a choice
    final Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg")); //fetches from res folder
    final MenuItem English = new MenuItem(); //English as a choice
    final Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg")); //fetches from res folder
    boolean startUp = true;
    BorderPane borderPane;
    Stage cb = new Stage(); //main chessboard stage
    //================== Confirmation-dialogue ==================
    final Alert confirmation = new Alert(AlertType.CONFIRMATION); //alert object type confirmation
    //================== about window ==================
    final Stage helpStage = new Stage();

    final Controller controller;
    int refreshRate = 5000;

    //================== info panel ==================
    final GridPane infoLayout = new GridPane(); //grid layout for infoscreen
    final TextArea moveLog = new TextArea(); //show moves
    final TextArea info = new TextArea(); //info panel
    Text moveLogTitle = new Text();  //title of move log
    Text infoscreen = new Text(); //info panel

    String lastHighlight = "yellow";
    String lastColorA = "white";
    String lastColorB = "gray";

    final RowConstraints row1 = new RowConstraints(); //row constraints
    final RowConstraints row2 = new RowConstraints();
    final RowConstraints row3 = new RowConstraints();
    final RowConstraints row4 = new RowConstraints();
    final ColumnConstraints column1 = new ColumnConstraints(); //column constraint
    final Insets inset = new Insets(10, 20, 20, 20); //padding around the infopanel


    public ChessProgram(String game) {
        controller = new Controller(game);
        this.game = game;
        if (game.contains("h")) { // set program pointer
            controller.programPtr = this; // used on humanclick in chessboard
            // to update the chessprogram board.
        }
        String os = System.getProperty("os.name"); // get operatingsystem name
        if (!os.contains("Windows")) {
            refreshRate = 100;
        }
    }

    @Override
    public void start(Stage chessBoardStage) {
        setLanguage(currentLanguage, currentCountry); //sets language
        cb = chessBoardStage;
        borderPane = new BorderPane();

        //currentDiff.setText("ELO rating: " + controller.elo);
        setMenuBar();
        setInfoScreen();

        chessboard = createChessBoard();
        borderPane.setCenter(createChessBoard());
        borderPane.setTop(menubar);

        chessBoardStage.setScene(new Scene(borderPane, WINDOW_WITH, WINDOW_HEIGHT));
        chessBoardStage.setTitle("Chess");
        chessBoardStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/black_knight.png")));
        chessBoardStage.setResizable(false);


        chessBoardStage.setOnCloseRequest(windowEvent -> close());

        chessBoardStage.show();
        if (controller.game.equals("e-e")) {
            animationEngEng();
        }

        if(game.equals("h-o")) {
            connectionDialog();
        }
    }

    public void close() {
        controller.stopEngine();
        StartMenu sm = new StartMenu();
        Stage smStage = new Stage();
        sm.setStartUpLanguage(currentLanguage, currentCountry);
        sm.start(smStage);
        controller.cfg.props.setProperty("ip", controller.ip);
        controller.cfg.props.setProperty("port", String.valueOf(controller.port));
        controller.cfg.saveProps();
        if (game.equals("h-o")) {
            try {
                controller.connection.close();
            } catch (Exception e) {
                System.out.println("Couldnt close connection");
            }
        }
    }

    public void animationEngEng() { // use on engine vs engine
        new AnimationTimer() { // mainloop of the program (controller??)
            @Override
            public void handle(long currentNanoTime) {
                if (currentNanoTime % refreshRate == 0) { // different refreshrate for windows and mac
                    if (controller.mainLoop()) {
                        updateBoard();
                    }
                }
            }
        }.start(); // start main animation loop
    }

    public void animationEngMove() {
        new AnimationTimer() { // mainloop of the program (controller??)
            @Override
            public void handle(long currentNanoTime) {
                if (controller.mainLoop()) {
                    updateBoard();
                    this.stop();
                }
            }
        }.start(); // start main animation loop
    }

    public void animationHumHum() {
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

    public void updateBoard() {
        chessboard = createChessBoard(); // update new chessboard view
        borderPane.setCenter(chessboard); // set the new chessboardView
        moveLog.setText(controller.chessboard.displayMoves()); //display moves in movelog
        moveLog.appendText(""); //to make the scrollbar follow the moves downwards
        isFinished(); //display mate or stalemate + who won
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
        if(!controller.isServer && game.equals("h-o")) {
            Collections.reverse(Arrays.asList(numbers));
        }
        for (int i = 8; i > 0; i--) {
            gridPane.add(new Label(letterLabels[i - 1]), i, 0);
            gridPane.add(new Label(letterLabels[i - 1]), i, 9);
            gridPane.add(new Label(numbers[i - 1]), 9, i);
            gridPane.add(new Label(numbers[i - 1]), 0, i);
        }
    }

    public void setLanguage(String language, String country) {  //set language for program
        language = language.toUpperCase(); //makes uppercase
        country = country.toUpperCase(); //makes uppercase
        Locale currentLocale; //defines locale variable
        switch (language) { //switch for different languages.
            case "EN" -> {
                English.setDisable(true);
                Norwegian.setDisable(false);
                currentLocale = new Locale(language, country);
            }//sets language to english.
            case "NO" -> {
                Norwegian.setDisable(true);
                English.setDisable(false);
                currentLocale = new Locale(language, country);
            } //sets language to norwegian.
            default -> currentLocale = new Locale("en", "UK"); //sets language to english as default;
        }
        currentLanguage = language; //current language
        currentCountry = country; //current country
        messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle
        setMenuBar(); //call function to update text
        updateInfoScreen();
    }

    public ImageView setIcon(Image flagIcon) { //gets an image as parameter and adds to imageView
        ImageView flag = new ImageView(flagIcon); //creates new imageview
        flag.setFitHeight(10); //set height
        flag.setFitWidth(15); //set width
        return flag; //return ImageView-object
    }

    public void setMenuBar() { //fetches text and initialises the menu bar
        addFileMenu();
        addSettingsMenu();
        addHelpMenu();
        cb.setTitle(messages.getString("Chess")); //set title, its here because it need to update

        //================== add items  ==================
        if (startUp) { //runs only when the program first starts
            startUp = false; //set to false
            help.getItems().addAll(about, rules); //adds items to help menu
            langSubMenu.getItems().addAll(Norwegian, English); //adds item to language

            settings.getItems().add(langSubMenu); //adds language under settings
            settings.getItems().add(difficultySubMenu);
            settings.getItems().add(themeSubMenu);

            difficultySubMenu.getItems().addAll(diff_carlsen, diff_gm, diff_lahl, diff_hard, diff_normal, diff_easy);
            themeSubMenu.getItems().addAll(standardTheme, darkTheme, classicTheme, blueTheme, greenTheme, districtTheme);

            file.getItems().add(restartMenu);
            menubar.getMenus().addAll(file, settings, help); //add all menus to menubar
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
        while (txtReader.ready()) { //while there still is text to ready
            input = input.concat(txtReader.readLine()); //read line and add text to string
        }
        txtReader.close(); //closes the reader

        //================== Set styling to text ==================
        titleText = setStyling(titleText, title, 30, "BOLD",true); //defines styling parameters
        inputText = setStyling(inputText, input, 15, "NORMAL",true); //not good practice hardcoding in values
        dateText = setStyling(dateText, date, 20, "SEMI-BOLD", true); //but the textfile is static

        layout.getChildren().addAll(titleText, inputText, dateText); //adds all text objects to layout

        helpStage.setScene(aboutScene); //add scene to stage(frame)
        helpStage.setResizable(false);
        helpStage.show(); //display the stage
    }

    public Text setStyling(Text text, String input, int fontSize, String weight, boolean seperator) { //sets styling to text
        if(seperator){
            text.setText(input + "\n"); //create seperator
            text.setWrappingWidth(500); //size before wrapping
        }
        text.setTextAlignment(TextAlignment.CENTER); //center text
        text.setFont(Font.font("verdana", FontWeight.findByName(weight), FontPosture.REGULAR, fontSize)); //sets font, boldness, posture and size
        return text; //returns text object
    }

    public void setStartUpLanguage(String language, String country) {
        currentLanguage = language;
        currentCountry = country;
    }

    public void restartGame() { //restart game by closing and creating new window.

        controller.stopEngine();
        controller.chessboard = new Chessboard(controller);
        controller.engineHandler = new EngineHandler(controller.elo, controller.thinkTime);
        controller.engineRunning = true;
        info.setText("ELO rating: " + controller.elo); //only display elo rating when restart game
        setLastTheme();    //Makes it so theme doesn't change when restarting game
        updateBoard();
    }

    public void setConfirmation() { //set confirmation text and function to handle action.
        ButtonType cancel = new ButtonType(messages.getString("Cancel"), ButtonData.CANCEL_CLOSE);
        ButtonType ok = new ButtonType(messages.getString("OK"), ButtonData.OK_DONE);
        confirmation.setTitle(messages.getString("Confirm")); //set title text
        confirmation.setHeaderText(messages.getString("RestartMessage")); //set header text

        confirmation.getButtonTypes().setAll(ok, cancel); //add buttons

        Optional<ButtonType> result = confirmation.showAndWait(); //display dialogue and wait for input
        if (result.isPresent() && result.get() == ok) { //if presses, calls restart function and closes message.
            confirmation.close();
            restartGame();
        }
        confirmation.close();
    }

    public void connectionDialog() {
        class connectionCfg {
            public final String ip;
            public final int port;
            public final boolean host;

            public connectionCfg(String ip, int port, boolean host) {
                this.ip = ip;
                this.port = port;
                this.host = host;
            }
        }

        Dialog<connectionCfg> connectionDialog = new Dialog<>();
        connectionDialog.setTitle(messages.getString("ConnectionTitle"));

        ButtonType connect = new ButtonType(messages.getString("Connect"), ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(messages.getString("Cancel"), ButtonData.CANCEL_CLOSE);
        connectionDialog.getDialogPane().getButtonTypes().addAll(connect, cancel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 40, 10, 10));

        TextField ipField = new TextField();
        ipField.setText(controller.cfg.props.getProperty("ip"));

        TextField portField = new TextField();
        portField.setText(controller.cfg.props.getProperty("port"));

        CheckBox host = new CheckBox(messages.getString("Host"));

        grid.add(new Label("IP"), 0, 0);
        grid.add(ipField, 1, 0);

        grid.add(new Label("Port"), 0, 1);
        grid.add(portField, 1, 1);

        grid.add(host, 0, 2);

        connectionDialog.getDialogPane().setContent(grid);

        connectionDialog.initStyle(StageStyle.UTILITY);

        connectionDialog.setResultConverter(buttonType -> {
            if (buttonType == connect) {
                return new connectionCfg(ipField.getText(),
                        Integer.parseInt(portField.getText()),
                        host.isSelected());
            }
            return null;
        });

        Optional<connectionCfg> result = connectionDialog.showAndWait();

        if (result.isPresent()) {
            controller.ip = result.get().ip;
            controller.port = result.get().port;
            controller.isServer = result.get().host;
            controller.startConnection();
            System.out.println(controller.ip + " " + controller.port + " " + controller.isServer);
        }
    }

    public void addLanguageMenu() {
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

    public void addDifficulties() {
        difficultySubMenu.setText(messages.getString("Difficulty"));

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

    public void addThemeMenu() {
        themeSubMenu.setText(messages.getString("BoardColor"));
        standardTheme.setText(messages.getString("Standard"));
        darkTheme.setText(messages.getString("Dark"));
        classicTheme.setText(messages.getString("Classic"));
        blueTheme.setText(messages.getString("Blue"));
        greenTheme.setText(messages.getString("Green"));
        districtTheme.setText(messages.getString("District"));

        themeActionEvent(standardTheme,"yellow", "white", "grey" );
        themeActionEvent(darkTheme,"#800e13","#737373", "#353535");
        themeActionEvent(classicTheme,"#d90429", "#FFCE9E","#D18B47");
        themeActionEvent(blueTheme,"#023047", "#7DAFEA", "#6092CF");
        themeActionEvent(greenTheme,"yellow",  "#EEEED2","#769656");
        themeActionEvent(districtTheme, "cyan", "#f39dff", "#e80b35");
    }

    public void setColor(String highlight, String A, String B){
        controller.chessboard.highlightColor = highlight;
        controller.chessboard.tileColorA = A;
        controller.chessboard.tileColorB = B;
        lastHighlight = highlight;
        lastColorA = A;
        lastColorB = B;
        updateBoard();
    }

    public void themeActionEvent(MenuItem theme, String highlight, String A, String B ){
        theme.setOnAction(actionEvent -> setColor(highlight, A, B));
    }

    public void addHelpMenu() {
        help.setText(messages.getString("Help"));
        rules.setText(messages.getString("Rules"));
        about.setText(messages.getString("About"));

        about.setOnAction(e -> { //calls function to display about
            try {
                getHelp("About"); //function
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

    public void addSettingsMenu() {
        addLanguageMenu();
        addDifficulties();
        addThemeMenu();
    }

    public void addFileMenu() { //setting text
        file.setText(messages.getString("File"));
        restartMenu.setText(messages.getString("Restart"));

        restartMenu.setOnAction(e -> {
            confirmation.setContentText(messages.getString("sure"));
            setConfirmation();
        });
    }

    public void setDifficulty(int elo) {
        controller.elo = elo;
        controller.engineHandler.setElo(elo);
        info.setText("ELO rating: " + controller.elo);
        System.out.println("Elo rating  " + controller.elo);
    }

    public void setInfoScreen(){ //set info screen
        moveLog.setEditable(false); //can not edit
        info.setEditable(false);
        updateInfoScreen(); //calls function to set text
        info.setText("ELO rating: " + controller.elo); //set elo rating in information

        moveLogTitle = setStyling(moveLogTitle,moveLogTitle.getText(), 13, "BOLD", false); //set styling
        infoscreen = setStyling(infoscreen, infoscreen.getText(), 13, "BOLD", false);

        moveLog.setFocusTraversable(false); //when program start, the textarea doesnt get focused
        info.setFocusTraversable(false);

        column1.setMaxWidth(200); //max width of column
        row1.setPercentHeight(5); //set percentage of height
        row2.setPercentHeight(65);
        row3.setPercentHeight(5);
        row4.setPercentHeight(25);

        infoLayout.getColumnConstraints().add(column1); //add constraint to layout
        infoLayout.getRowConstraints().addAll(row1,row2,row3, row4); //add constraints to layout

        infoLayout.setPadding(inset); //top, right, bottom,left
        infoLayout.setVgap(5); //vertical gap between boxes

        infoLayout.add(moveLogTitle, 0, 0); //adding text to layout
        infoLayout.add(moveLog, 0, 1);
        infoLayout.add(infoscreen, 0, 2); //adding textarea to layout
        infoLayout.add(info, 0, 3);


        GridPane.setHalignment(moveLogTitle, HPos.CENTER); //alignment of title
        GridPane.setHalignment(infoscreen, HPos.CENTER); //alignment of information

        borderPane.setLeft(infoLayout); //set layout to left side of borderpane
    }

    public void updateInfoScreen(){
        moveLogTitle.setText(messages.getString("Movelog"));
        infoscreen.setText(messages.getString("Infoscreen"));
    }

    public void isFinished(){ //checks for mate and stalemate
        if (controller.chessboard.checkStaleMate()){
            info.appendText("\n"+messages.getString("Stalemate"));
            winColor();
        } else if (controller.chessboard.mateCheck() || (!controller.engineRunning && controller.game.contains("e"))){
            info.appendText("\n"+messages.getString("Mate"));
            winColor();
            controller.chessboard.isMate = false;
        }
    }

    public void winColor(){ //which color wins
        if (controller.chessboard.whiteTurn){
            info.appendText("\n"+messages.getString("Blackwin"));
        }else{
            info.appendText("\n"+messages.getString("Whitewin"));
        }
    }

    public void setLastTheme(){
        controller.chessboard.highlightColor = lastHighlight;
        controller.chessboard.tileColorA = lastColorA;
        controller.chessboard.tileColorB = lastColorB;
    }
}
