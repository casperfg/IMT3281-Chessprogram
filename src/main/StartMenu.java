package main;


import java.util.EventListener;

import javafx.application.Application;
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
    Scene scene = new Scene(borderPane, 500, 500);
    final int FONTSIZE = 20;
    final int WIDTH = 290;
    final int HEIGHT = 50;



    public void start(Stage menuStage){
        addLogo();
        createMenu();

        menuStage.setTitle("Chess - Main Menu");
        menuStage.setScene(scene);
        menuStage.show();

    }

    void addLogo(){
        imageView.setImage(logo);
        borderPane.setTop(imageView);
        BorderPane.setAlignment(imageView, Pos.CENTER);
        BorderPane.setMargin(imageView, new Insets(10,10,1,1));
    }

    void createMenu() {
        Button btn_play = new Button("PLAYER vs. CPU");
        Button btn_multi = new Button("PLAYER vs. PLAYER");
        Button btn_cpu = new Button("CPU vs. CPU");
        Button btn_settings = new Button("CPU vs. CPU");
        Button btn_exit = new Button("EXIT");

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
        btn.setFont(Font.font(FONTSIZE));
        btn.setPrefSize(WIDTH, HEIGHT);
        flowPane.getChildren().add(btn);
    }


}