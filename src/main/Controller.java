package main;

public class Controller{
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true; // wether engine is running or not
    public ChessProgram programPtr = null; // has pointer to chessprogram for board updation. IF there is a human involved
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
            stopEngine();
        }
    }
    public void gameCheck(){
        if(engineHandler.checkMate() || chessboard.repetition == 4) {
            System.out.println("Game Finished: ");
            stopEngine();
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
                chessboard.move(ret); // move the best move
                engineHandler.getBest(chessboard); // start new
            }
            return true;
        }
        return false;
    }

    public boolean mainLoop(){
        Boolean change = false;
        if(engineRunning) {
            if (game.equals("e-e")) {
                change = engVsEng();
                gameCheck(); // check if the player was mated
            } else if (game.equals("h-e")) {
                change = humVsEng();
                gameCheck(); // check if the player was mated
                System.out.println(chessboard.toFen());
            }
        }
        return change;
    }
    public void click(int x, int y){ // clicked by human
        if(programPtr != null && engineRunning){ // is not null when human is involved.
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
            gameCheck(ret);
            if(engineRunning) {
                chessboard.move(ret);
            }
            return true;
        }
        return false;
    }
}
