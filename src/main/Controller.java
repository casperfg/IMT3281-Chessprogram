package main;

import javafx.scene.layout.GridPane;

public class Controller {
    public Chessboard cb;
    public String game = "e-e";
    public EngineHandler eng;
    public EngineHandler mateEng;
    public Boolean firstRun = true;
    public Boolean gameGoing = true;

    public Controller() {
        cb = new Chessboard();
        eng = new EngineHandler();

    }
    public void startEng(){
        if (eng.checkWorker().equals("-1")) {
            eng.getBest(cb);
        }
    }
    public void gameCheck(String ret){
        if(ret.equals("MaTe") || cb.repetition == 4) {
            System.out.println("Game Finished: ");
            gameGoing = false;
        }
    }
    // FOOLS MATE:
    //cb.move("f2f3"); cb.move("e7e5"); cb.move("g2g4");
    public Boolean engVsEng(){
        String ret = eng.checkWorker(); // check workerThread
        if(firstRun){
            startEng();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            gameCheck(ret);
            System.out.println(cb.repetition);
            if(gameGoing){
                if(cb.whiteTurn){
                    eng.thinkTime = 1000;
                    eng.setElo(100);
                }else{
                    eng.thinkTime = 1000;
                    eng.setElo(2500);
                }
                cb.move(ret); // move the best move
                eng.getBest(cb); // start new
            }
            return true;
        }
        return false;
    }

    public boolean mainLoop(){
        Boolean change = false;
        if(game.equals("e-e") && gameGoing){
            change = engVsEng();
        }
        firstRun = false;
        return change;
    }
}
