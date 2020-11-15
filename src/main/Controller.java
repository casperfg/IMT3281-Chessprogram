package main;

public class Controller{
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true;
    public ChessProgram programPtr;

    public Controller() {
        chessboard = new Chessboard();
        engineHandler = new EngineHandler();
    }
    public void startEngine(){
        System.out.println("Engine starting...");
        if (engineHandler.checkWorker().equals("-1")) {
            engineHandler.getBest(chessboard);
        }
    }
    public void stopEngine(){
        engineRunning = false;
        System.out.println("Engine stopped...");
    }

    public void gameCheck(String ret){
        if(ret.equals("MaTe") || chessboard.repetition == 4) {
            System.out.println("Game Finished: ");
            engineRunning = false;
        }
    }
    // FOOLS MATE:
    //cb.move("f2f3"); cb.move("e7e5"); cb.move("g2g4");
    public Boolean engVsEng(){
        String ret = engineHandler.checkWorker(); // check workerThread
        if(firstRun){
            firstRun = false;
            startEngine();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            gameCheck(ret);
            if(engineRunning){
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
        if(game.equals("e-e") && engineRunning){
            change = engVsEng();
        } else if(game.equals("h-e") ){
            change = humVsEng();
        }

        return change;
    }

    private Boolean humVsEng() {
        String ret; // check workerThread
        Boolean thinking = false;

        if(!chessboard.whiteTurn){
            if(!thinking) {
                engineHandler.getBest(chessboard); // calculate best move
                thinking = true;
            }else{ // is thinking
                ret = engineHandler.checkWorker();
                if(!ret.equals("-1") && engineRunning){
                    gameCheck(ret);
                    chessboard.move(ret); // move the best move
                    thinking = false;
                    return true;
                }else{
                    System.out.println("Thinking");
                }
            }
        }else{ // humanTurn (white)
            return true;
        }
        return false;
    }
}
