package main;

import javafx.application.Application;

public class Controller {
    public Controller(String[] args) {
        Chessboard ch = new Chessboard();
        EngineHandler eng = new EngineHandler();
        Application.launch(ChessProgram.class, args);
    }
}
