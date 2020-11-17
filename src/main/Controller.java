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
    public void gameCheck(String ret){ // Computerplayer is mated.
        if(ret.equals("MaTe") || chessboard.repetition == 4) {
            System.out.println("Game Finished");
            stopEngine();
        }
    }
    public void gameCheck(){ // this move mates the other player
        if((engineHandler.checkMate() || chessboard.repetition == 4) && engineRunning) {
            System.out.println("Game Finished");
            stopEngine();
        }
    }
    // FOOLS MATE:
    // chessboard.move("f2f3"); chessboard.move("e7e5");
    public Boolean engVsEng(){
        String ret = engineHandler.checkWorker(); // check workerThread
        if(firstRun){
            firstRun = false;
            startEngine();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            gameCheck(); gameCheck(ret);
            if(!ret.equals("MaTe")){
                chessboard.move(ret); // move the best move
            }
            if(engineRunning){
                engineHandler.getBest(chessboard); // start new
            }
            return true;
        }
        return false;
    }

    public boolean mainLoop(){
        Boolean change = false;
        //gameCheck();
        if(engineRunning) {
            if (game.equals("e-e")){
                change = engVsEng();
                // check if the player was mated
            } else if (game.equals("h-e")) {
                change = humVsEng();
            }
            gameCheck();
        }else{
            return true;
        }
        return change;
    }
    public void click(int x, int y){ // clicked by human
        if(programPtr != null && engineRunning){ // is not null when human is involved.
            boolean change = chessboard.humanClick(x, y);
            programPtr.updateBoard(); // update the board
            if(change){ // other player makes move.
                if(engineRunning) {
                    engineHandler.getBest(chessboard); // start the thread calculation
                    programPtr.animationEngMove();  // start the animationloop for once.
                }
            }
        }
    }
    private Boolean humVsEng() {
        String ret = engineHandler.checkWorker(); // check workerThread
        Boolean thinking = false;
        if(!ret.equals("-1")){
            gameCheck(ret);
            chessboard.move(ret);
            return true;
        }
        return false;
    }
}
