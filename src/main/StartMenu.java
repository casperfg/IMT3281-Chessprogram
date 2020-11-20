package main;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.pieces.Pawn;

import java.util.Locale;
import java.util.ResourceBundle;

public class StartMenu extends Application {
    final int WIDTH = 290;
    final int HEIGHT = 50;
    Stage mm = new Stage(); //main menu stage
    final BorderPane borderPane = new BorderPane();
    final ImageView imageView = new ImageView();
    final FlowPane flowPane = new FlowPane();
    final FlowPane langPane = new FlowPane();
    final Image logo_en = new Image(getClass().getResourceAsStream("/images/logo_en.png"));
    final Image logo_no = new Image(getClass().getResourceAsStream("/images/logo_no.png"));
    final Scene scene = new Scene(borderPane, 600, 600);
    final Button btn_play = new Button();
    final Button btn_pvp = new Button();
    final Button btn_online = new Button();
    final Button btn_cpu = new Button();
    final Button btn_exit = new Button();
    final Button btn_no = new Button();
    final Button btn_en = new Button();

    //================== Fetching icons ==================
    final Image norFlag = new Image(getClass().getResourceAsStream("/images/NorwayFlag.jpg"), 30, 20, false, true); //fetches from res folder
    final Image UKFlag = new Image(getClass().getResourceAsStream("/images/UnitedKingdomFlag.jpg"), 30, 20, false, true); //fetches from res folder

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

        menuStage.setScene(scene);
        menuStage.show();
        menuStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/black_knight.png")));

        Stage cpuStage = new Stage();

        btn_cpu.setOnAction(actionEvent -> {
            ChessProgram cp = new ChessProgram("e-e"); // TODO IS SERVER!
            cp.setStartUpLanguage(currentLanguage, currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_play.setOnAction(actionEvent -> {
            ChessProgram cp = new ChessProgram("h-e");
            cp.setStartUpLanguage(currentLanguage, currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_pvp.setOnAction(actionEvent -> {
            ChessProgram cp = new ChessProgram("h-h");
            cp.setStartUpLanguage(currentLanguage,currentCountry);
            cp.start(cpuStage);
            menuStage.close();
        });

        btn_online.setOnAction(actionEvent -> {
            ChessProgram cp = new ChessProgram("h-o");
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
        setButton(btn_pvp, WIDTH, HEIGHT, "Pvp");
        setButton(btn_online, WIDTH, HEIGHT, "Online");
        setButton(btn_cpu, WIDTH, HEIGHT, "Cvc");
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
        flowPane.getChildren().addAll(btn_play, btn_pvp , btn_online, btn_cpu ,btn_exit);

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


    public Background setBackgroundImage(Image flag) {
        BackgroundImage backgroundImage = new BackgroundImage(flag, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        return new Background(backgroundImage);
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
        btn_pvp.setText(messages.getString("Pvp"));
        btn_online.setText(messages.getString("Online"));
        btn_cpu.setText(messages.getString("Cvc"));
        btn_exit.setText(messages.getString("Exit"));
        addLogo();
    }

    public void setStartUpLanguage(String language, String country) {
        defaultLanguage = language;
        defaultCountry = country;
    }
}
