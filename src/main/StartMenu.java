package main;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StartMenu extends Application {
    BorderPane borderPane = new BorderPane();
    ImageView imageView = new ImageView();
    FlowPane flowPane = new FlowPane();


    Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
    Scene scene = new Scene(borderPane, 600, 600);

    final int WIDTH = 290;
    final int HEIGHT = 50;

    Button btn_play = new Button("PLAYER vs. CPU");
    Button btn_multi = new Button("PLAYER vs. PLAYER");
    Button btn_cpu = new Button("CPU vs. CPU");
    Button btn_settings = new Button("SETTINGS");
    Button btn_exit = new Button("EXIT");



    public void start(Stage menuStage){
        addLogo();
        createMenu();
        disableButtonsNotWorkingYetGeitGeitGeit(); //TODO IMPLEMENT BUTTON FUNCTIONS

        menuStage.setTitle("Chess - Main Menu");
        menuStage.setScene(scene);
        menuStage.show();

        Stage cpuStage = new Stage();

        btn_cpu.setOnAction(actionEvent -> new ChessProgram("e-e").start(cpuStage));
        btn_play.setOnAction(actionEvent -> new ChessProgram("h-e").start(cpuStage));
        btn_exit.setOnAction(actionEvent -> menuStage.close());
    }

    void addLogo(){
        imageView.setImage(logo);
        borderPane.setTop(imageView);
        BorderPane.setAlignment(imageView, Pos.CENTER);
        BorderPane.setMargin(imageView, new Insets(10,10,1,1));
    }

    void createMenu() {
        addButton(btn_play);
        addButton(btn_multi);
        addButton(btn_cpu);
        addButton(btn_settings);
        addButton(btn_exit);

        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setVgap(30);

        borderPane.setCenter(flowPane);
    }

    void addButton(Button btn){
        btn.setFont(Font.font(20));
        btn.setPrefSize(WIDTH, HEIGHT);
        flowPane.getChildren().add(btn);
    }

    void disableButtonsNotWorkingYetGeitGeitGeit(){
        btn_multi.setDisable(true);
        btn_settings.setDisable(true);
    }


}