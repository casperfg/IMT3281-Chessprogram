package main;

import javafx.application.Platform;
import main.networking.Client;
import main.networking.NetworkConnection;
import main.networking.Server;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Controller {
    public Chessboard chessboard;
    public String game = "e-e";
    public EngineHandler engineHandler;
    public Boolean firstRun = true;
    public Boolean engineRunning = true; // wether engine is running or not
    public ChessProgram programPtr = null; // has pointer to chessprogram for board updation. IF there is a human involved
    public int thinkTime = 1000;
    public int elo = 1000;
    public Config cfg;
    public NetworkConnection connection;
    public boolean isServer;
    public String ip;
    public int port;
    public boolean waitingForMove;

    public Controller(boolean isSer, String game) {
        this.isServer = isSer;
        this.game = game;
        chessboard = new Chessboard(this);
        chessboard.move("d2d4"); chessboard.move("e7e6"); chessboard.move("e2e4"); chessboard.move("f8b4");
        engineHandler = new EngineHandler(elo, thinkTime);
        System.out.println("Elo rating: " + this.elo);

        cfg = new Config();

        ip = cfg.props.getProperty("ip");
        port = Integer.parseInt(cfg.props.getProperty("port"));
    }

    File difficultyFile = new File("./res/text/difficulty.txt");

    public void startConnection() {
        connection = (isServer ? createServer() : createClient());
        waitingForMove = !isServer;
        connection.start();
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
        engineRunning = false;
        engineHandler.stopEngine();
        System.out.println("Engine stopped...");
    }

    public void gameCheck(String ret) { // Computerplayer is mated.
        if (ret.equals("MaTe") || chessboard.repetition == 4 || chessboard.checkStaleMate()) {
            System.out.println("Game Finished");
            stopEngine();
        }
    }

    public boolean gameCheck() { // this move mates the other player
        if ((engineHandler.checkMate() || chessboard.repetition == 4 || chessboard.checkStaleMate()) && engineRunning) {
            System.out.println("Game Finished");
            stopEngine();
            return true;
        }
        return false;
    }

    // FOOLS MATE:
    // chessboard.move("f2f3"); chessboard.move("e7e5");
    // CHECK:
    // chessboard.move("d2d4"); chessboard.move("e7e6"); chessboard.move("e2e4"); chessboard.move("f8b4");
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
            } else if(game.equals("h-h") && !waitingForMove) {
                change = true;
            }
            //gameCheck();
            if (!engineRunning) {
                return true;
            }
        }
        return change;
    }

    public void click(int x, int y) { // clicked by human
        if (programPtr != null && engineRunning || (!waitingForMove && game.equals("h-h"))) { // is not null when human is involved.
            boolean change = chessboard.humanClick(x, y);
            programPtr.updateBoard(); // update the board
            if (change) { // other player makes move.
                if (engineRunning) {
                    engineHandler.getBest(chessboard); // start the thread calculation
                    programPtr.animationEngMove();  // start the animationloop for once.
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
        if (!ret.equals("-1")) {
            gameCheck(ret);
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

    public void readEloRating() throws IOException {
        System.out.println("Reading from file difficulty.txt...\n");
        Scanner scanner = new Scanner(difficultyFile);
        elo = scanner.nextInt();
        System.out.println("Elo rating after file is read: " + elo + "\n");
    }

    public void writeEloRatingToFile(int elo) throws IOException {
        System.out.println("Writing to file difficulty.txt...");
        FileWriter fileWriter = new FileWriter(difficultyFile.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String rating = String.valueOf(elo);

        bufferedWriter.write(rating);
        bufferedWriter.close();

        System.out.println("Done... | Rating = " + rating + "\n");
    }

    public void ISMATE() {
        System.out.println("MATE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}
