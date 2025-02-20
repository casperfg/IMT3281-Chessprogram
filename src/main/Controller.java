package main;

import javafx.application.Platform;
import main.networking.Client;
import main.networking.NetworkConnection;
import main.networking.Server;

import java.io.Serializable;

public class Controller {
    public Chessboard chessboard;
    public final String game;
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true; // wether engine is running or not
    public ChessProgram programPtr = null; // has pointer to chessprogram for board updation. IF there is a human involved
    public final int thinkTime = 1000;
    public int elo = 900;
    public final Config cfg;
    public NetworkConnection connection;
    public boolean isServer = true;
    public String ip;
    public int port;
    public boolean waitingForMove;

    // FOOLS MATE:
    // chessboard.move("f2f3"); chessboard.move("e7e5");
    // CHECK:
    // chessboard.move("d2d4"); chessboard.move("e7e6"); chessboard.move("e2e4"); chessboard.move("f8b4");
    // Mads spesial:
    //chessboard.move("f2f3"); chessboard.move("e7e5"); chessboard.move("g2g4"); chessboard.move("d8h4");

    public Controller(String game) {
        this.game = game;
        chessboard = new Chessboard(this);
        if(game.contains("e")){
            engineHandler = new EngineHandler(elo, thinkTime);
            System.out.println("Elo rating: " + this.elo);
        }

        // Load properties
        cfg = new Config();
        ip = cfg.props.getProperty("ip");
        port = Integer.parseInt(cfg.props.getProperty("port"));

        if (game.contains("h")) {
            waitingForMove = false;
        }
        if (game.equals("h-h") || game.equals("h-o")) {
            engineRunning = false;
        }
    }

    public void startConnection() {
        connection = (isServer ? createServer() : createClient());
        waitingForMove = !isServer;  // Server moves first
        connection.start();
        if (programPtr != null) {
            programPtr.info.setText("Waiting for opponent...");
        }
    }

    public void startEngine(){
        System.out.println("Engine starting...");
        if (!engineRunning) {
            engineRunning = true;
        }
        if (engineHandler.checkWorker().equals("-1")) {
            engineHandler.getBest(chessboard);
        }
    }

    public void stopEngine() {
        if(game.contains("e")){
            engineHandler.stopEngine();
        }
        engineRunning = false;
        System.out.println("Engine stopped...");
    }

    public void gameCheck(String ret) { // Computerplayer is mated.
        if (ret.equals("MaTe") || chessboard.repetition == 4 || chessboard.checkStaleMate()) {
            System.out.println("Game Finished");
            stopEngine();
        }
    }

    public void gameCheck() { // this move mates the other player
        if ((engineHandler.checkMate() || chessboard.repetition == 4 || chessboard.checkStaleMate()) && engineRunning) {
            System.out.println("Game Finished");
            stopEngine();
        }
    }

    public Boolean engVsEng() {
        String ret = engineHandler.checkWorker(); // check workerThread
        if (firstRun) {
            firstRun = false;
            startEngine();
        }
        if (!ret.equals("-1")) { // is -1 when workerthread is still working
            gameCheck();
            gameCheck(ret);
            if (!ret.equals("MaTe")) {
                chessboard.move(ret); // move the best move
            }
            if (engineRunning) {
                engineHandler.getBest(chessboard); // start new
            }
            return true;
        }
        return false;
    }

    public boolean mainLoop() {
        Boolean change = false;

        if (engineRunning) {
            if (game.equals("e-e")) {
                change = engVsEng();
                // check if the player was mated
            } else if (game.equals("h-e")) {
                change = humVsEng();
            } else if(game.equals("h-o") && !waitingForMove) {
                change = true;
            }
            if (!engineRunning) {
                return true;
            }
        }
        return change;
    }

    public void click(int x, int y) { // clicked by human
        if (programPtr != null) { // is not null when human is involved.
            boolean change = false;
            // Block input if not your turn in online mode
            if ((game.equals("h-o") && !waitingForMove) | !game.equals("h-o")) {
                change = chessboard.humanClick(x, y);
                programPtr.updateBoard(); // update the board
            }

            if (change) { // other player makes move.
                if (engineRunning) {
                    engineHandler.getBest(chessboard); // start the thread calculation
                    programPtr.animationEngMove();  // start the animationloop for once.
                }
                waitingForMove = true;
            }
        }
    }

    private Boolean humVsEng() {
        String ret = engineHandler.checkWorker(); // check workerThread
        if (!ret.equals("-1")) {
            gameCheck(ret);
            chessboard.move(ret);
            return true;
        }
        return false;
    }

    private Server createServer() {
        return new Server(port, data -> Platform.runLater(() -> handleData(data)));
    }

    private Client createClient() {
        return new Client(ip, port, data -> Platform.runLater(() -> handleData(data)));
    }

    // This is called everytime the connection receives data
    private void handleData(Serializable data) {
        if (data instanceof Move) {
            Move move = (Move) data;
            System.out.println("Move received from " + (isServer ? "client" : "server") + ": " + move.moveString);
            chessboard.move(move.x, move.y, move.xt, move.yt);
            if (programPtr != null) {
                programPtr.animationHumHum();
                programPtr.updateBoard();
            }
        }
        else if (data instanceof String) {
            String msg = (String) data;
            programPtr.info.setText(msg);
        }
        else {
            System.out.print("Data recieved is not of type Move or String");
            System.exit(-1);
        }
    }
}
