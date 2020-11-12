package main;

public class Controller {
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public EngineHandler mateEngine;
    public Boolean firstRun = true;
    public Boolean gameGoing = true;

    public Controller() {
        chessboard = new Chessboard();
        engineHandler = new EngineHandler();
    }
    public void startEngine(){
        if (engineHandler.checkWorker().equals("-1")) {
            engineHandler.getBest(chessboard);
        }
    }
    public void stopEngine(){
        gameGoing = false;
    }

    public void gameCheck(String ret){
        if(ret.equals("MaTe") || chessboard.repetition == 4) {
            System.out.println("Game Finished: ");
            gameGoing = false;
        }
    }
    // FOOLS MATE:
    //cb.move("f2f3"); cb.move("e7e5"); cb.move("g2g4");
    public Boolean engVsEng(){
        String ret = engineHandler.checkWorker(); // check workerThread
        if(firstRun){
            startEngine();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            gameCheck(ret);
            System.out.println(chessboard.repetition);
            if(gameGoing){
                if(chessboard.whiteTurn){
                    engineHandler.thinkTime = 1000;
                    engineHandler.setElo(100);
                }else{
                    engineHandler.thinkTime = 1000;
                    engineHandler.setElo(2500);
                }
                chessboard.move(ret); // move the best move
                engineHandler.getBest(chessboard); // start new
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
