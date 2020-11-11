package main;

import javafx.scene.layout.GridPane;

public class Controller {
    public Chessboard cb;
    public String game = "e-e";
    public EngineHandler eng;
    public Boolean firstRun = true;
    public Controller() {
        cb = new Chessboard();
        eng = new EngineHandler();

    }
    public void startEng(){
        if (eng.checkWorker().equals("-1")) {
            eng.getBest(cb);
        }
    }
    public Boolean engVsEng(){
        String ret = eng.checkWorker(); // check workerThread
        if(firstRun){
            startEng();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            cb.move(ret); // move the best move
            eng.getBest(cb); // start new
            return true;
        }
        return false;
    }
    public boolean mainLoop(){
        Boolean change = false;
        if(game.equals("e-e")){
            change = engVsEng();
        }
        firstRun = false;
        return change;
    }
}
