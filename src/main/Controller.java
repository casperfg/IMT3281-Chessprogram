package main;

import javafx.application.Platform;
import main.networking.Client;
import main.networking.NetworkConnection;
import main.networking.Server;

public class Controller{
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true;
    public ChessProgram programPtr = null;
    public int thinkTime = 1000;
    public int elo;
    public Config cfg;
    public NetworkConnection connection;
    public boolean isServer;
    public String ip;
    public int port;
    public boolean waitingForMove;

    public Controller(int elo, boolean isSer) {
        this.elo = elo;
        this.isServer = isSer;
        chessboard = new Chessboard(this);
        engineHandler = new EngineHandler();
        System.out.println("Elo rating: " + this.elo);

        cfg = new Config();

        ip = cfg.props.getProperty("ip");
        port = Integer.parseInt(cfg.props.getProperty("port"));

        connection = (isServer ? createServer() : createClient());
        connection.start();
        waitingForMove = !isServer;
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
        } else if(game.equals("h-h") && !waitingForMove){
            change = true;
        }
        return change;
    }
    public void click(int x, int y){ // clicked by human
        if(programPtr != null){ // is not null when human is involved.
            boolean change = chessboard.humanClick(x, y);
            programPtr.updateBoard();
            if(change){ // other player makes move.
                if (game.equals("h-e")) {
                    engineHandler.setElo(elo);
                    engineHandler.getBest(chessboard);
                    programPtr.animationEngMove();
                }
                else if (game.equals("h-h")) {
                    programPtr.animationHumHum();
                }
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

    private Server createServer() {
        return new Server(port, data -> {
            Platform.runLater(() -> {
                handleData((Move) data);
            });
        });
    }

    private Client createClient() {
        return new Client(ip, port, data -> {
            Platform.runLater(() -> {
                handleData((Move) data);
            });
        });
    }

    private void handleData(Move move) {
        System.out.println("Move received from " + (isServer ? "client" : "server") + ": " + move.moveString);
        chessboard.move(move.x, move.y, move.xt, move.yt);
    }
}
