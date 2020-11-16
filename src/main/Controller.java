package main;

public class Controller{
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true;
    public ChessProgram programPtr = null;
    public int thinkTime = 1000;
    public int elo = 2500;
    public Controller() {
        chessboard = new Chessboard(this);
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
        engineHandler.stopEngine();
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
                    engineHandler.thinkTime = thinkTime;
                    engineHandler.setElo(100);
                }else{
                    engineHandler.thinkTime = thinkTime;
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
        } else if(game.equals("h-e") && engineRunning){
            change = humVsEng();
        }
        return change;
    }
    public void click(int x, int y){ // clicked by human
        if(programPtr != null){ // is not null when human is involved.
            boolean change = chessboard.humanClick(x, y);
            programPtr.updateBoard();
            if(change){ // other player makes move.
                engineHandler.setElo(elo);
                engineHandler.getBest(chessboard);
                programPtr.animationEngMove();
            }
        }
    }
    private Boolean humVsEng() {
        String ret = engineHandler.checkWorker(); // check workerThread
        Boolean thinking = false;
        if(!ret.equals("-1")){
            chessboard.move(ret);
            return true;
        }
        return false;
    }
}
