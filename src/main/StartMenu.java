package main;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.pieces.Pawn;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class StartMenu extends Application {
    final int WIDTH = 290;
    final int HEIGHT = 50;
    Stage mm = new Stage(); //main menu stage
    BorderPane borderPane = new BorderPane();
    ImageView imageView = new ImageView();
    FlowPane flowPane = new FlowPane();
    FlowPane langPane = new FlowPane();
    Image logo_en = new Image(getClass().getResourceAsStream("/images/logo_en.png"));
    Image logo_no = new Image(getClass().getResourceAsStream("/images/logo_no.png"));
    Scene scene = new Scene(borderPane, 600, 600);
    Button btn_play = new Button();
    Button btn_multi = new Button();
    Button btn_settings = new Button();
    Button btn_cpu = new Button();
    Button btn_exit = new Button();
    Button btn_no = new Button();
    Button btn_en = new Button();

    //================== Fetching icons ==================
    Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg"), 30, 20, false, true); //fetches from res folder
    Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg"), 30, 20, false, true); //fetches from res folder

    //================== Initialize internationalization ==================
    String defaultLanguage = "en";
    String defaultCountry = "UK";
    String currentLanguage;
    String currentCountry;
    ResourceBundle messages; //initializing resource bundle

    public void start(Stage menuStage) {
        mm = menuStage;
        setLanguage(defaultLanguage, defaultCountry);
        createMenu();
        addLogo();
        disableButtonsNotWorkingYetGeitGeitGeit(); //TODO IMPLEMENT BUTTON FUNCTIONS

        menuStage.setScene(scene);
        menuStage.show();

        Stage cpuStage = new Stage();

        btn_cpu.setOnAction(actionEvent -> {
            ChessProgram cp = null;
            try {
                cp = new ChessProgram("e-e");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            cp.setStartUpLanguage(currentLanguage, currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_play.setOnAction(actionEvent -> {
            ChessProgram cp = null;
            try {
                cp = new ChessProgram("h-e");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            cp.setStartUpLanguage(currentLanguage, currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_multi.setOnAction(actionEvent -> {
            ChessProgram cp = null;
            try {
                cp = new ChessProgram("h-o");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            cp.setStartUpLanguage(currentLanguage,currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_exit.setOnAction(actionEvent -> menuStage.close());
    }

    void addLogo() {
        String language = currentLanguage.toUpperCase();
        switch (language) {
            case "NO" -> imageView.setImage(logo_no);
            case "EN" -> imageView.setImage(logo_en);
        }

        borderPane.setTop(imageView);
        BorderPane.setAlignment(imageView, Pos.CENTER);
        BorderPane.setMargin(imageView, new Insets(10, 10, 1, 1));
    }

    void createMenu() {
        setButton(btn_play, WIDTH, HEIGHT, "Pvc");
        setButton(btn_multi, WIDTH, HEIGHT, "Pvp");
        setButton(btn_cpu, WIDTH, HEIGHT, "Cvc");
        setButton(btn_settings, WIDTH, HEIGHT, "Settings");
        setButton(btn_exit, WIDTH, HEIGHT, "Exit");

        //================== Language Buttons  ==================
        setButton(btn_no, 30, 20, "");
        setButton(btn_en, 30, 20, "");


        //================== Set background image  ==================
        btn_en.setBackground(setBackgroundImage(UKFlag));
        btn_no.setBackground(setBackgroundImage(norFlag));

        //================== Layout positioning  ==================
        langPane.setOrientation(Orientation.HORIZONTAL);
        langPane.setAlignment(Pos.CENTER);
        langPane.setHgap(10);
        langPane.getChildren().addAll(btn_no, btn_en);


        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setVgap(30);
        flowPane.getChildren().addAll(btn_play, btn_multi ,btn_cpu ,btn_settings ,btn_exit);

        borderPane.setCenter(flowPane);
        borderPane.setBottom(langPane);


        //================== Action listener  ==================
        btn_no.setOnAction(e -> {
            setLanguage("no", "NO"); //function to change language
            updateButton();
        });

        btn_en.setOnAction(e -> {
            setLanguage("en", "UK"); //function to change language
            updateButton();
        });

        Pawn pawn = new Pawn();
        pawn.color = true;
    }

    void setButton(Button btn, int width, int height, String input) {
        if (!input.isEmpty()) {
            btn.setText(messages.getString(input));
        }
        btn.setFont(Font.font(20));
        btn.setPrefSize(width, height);
    }

    void disableButtonsNotWorkingYetGeitGeitGeit(){
        btn_settings.setDisable(true);
    }


    public Background setBackgroundImage(Image flag) {
        BackgroundImage backgroundImage = new BackgroundImage(flag, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        return background;
    }

    public void setLanguage(String language, String country) {  //set language for program
        language = language.toUpperCase(); //makes uppercase
        country = country.toUpperCase(); //makes uppercase
        Locale currentLocale; //defines locale variable
        currentLocale = new Locale(language, country); //sets language to english.

        currentLanguage = language; //current language
        currentCountry = country; //current country
        messages = ResourceBundle.getBundle("languages/MessagesBundle", currentLocale); //fetches resource bundle
        updateButton();

    }

    void updateButton() {
        mm.setTitle(messages.getString("Chess")); //updates title
        btn_play.setText(messages.getString("Pvc"));
        btn_multi.setText(messages.getString("Pvp"));
        btn_cpu.setText(messages.getString("Cvc"));
        btn_settings.setText(messages.getString("Settings"));
        btn_exit.setText(messages.getString("Exit"));
        addLogo();
    }

    public void setStartUpLanguage(String language, String country) {
        defaultLanguage = language;
        defaultCountry = country;
    }
}
