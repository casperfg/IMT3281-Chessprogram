package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java
public class EngineHandler {
    public int eloRating;
    public int thinkTime = 1000; // ms
    private Process engine;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    private engineWorker worker; // workThread that lets engine wait and calculate
    private static String PATH = "stockfishWin.exe"; // path for window
    private static final String PATHmac = "stockfish"; // path max

    public EngineHandler() { // start the engine
        startEngine();
    }

    public EngineHandler(int elo, int thkTime) { // start the engine
        eloRating = elo;
        thinkTime = thkTime; // with time and elo.
        startEngine();
    }

    public void getBest(Chessboard cboard) { // get best move (start thread)
        worker = new engineWorker(processReader, processWriter, thinkTime, cboard); // start new thread
        worker.start(); // make new Workerthread and start it
    }

    public String checkWorker() { // check if the workerthread is finished
        if (worker != null) {
            if (worker.done) { // worker is done calculating
                worker.done = false; // reset done
                return worker.Best;
            }
        }
        return "-1"; // still not done
    }

    public boolean checkMate() {
        return worker.playerMated;
    }

    private void startEngine() { // works for both windows and max
        String os = System.getProperty("os.name"); // get operatingsystem name
        if (!os.contains("Windows")) { // mac functionality
            PATH = PATHmac; // set path to the mac path
        }
        try {
            engine = Runtime.getRuntime().exec(PATH); // execute exe
            // make reader / writer stream
            processReader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            processWriter = new OutputStreamWriter(engine.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // set the option that makes elo possible
        sendCommand("setoption name UCI_LimitStrength value true");
        setElo(eloRating);
    }

    private void sendCommand(String cmd) { // send command to worker.
        worker = new engineWorker(processReader, processWriter);
        worker.sendCommand(cmd);
    }

    public void stopEngine() { // stop the engine
        try {
            processReader.close(); // close reader/writer
            processWriter.close();
        } catch (IOException ignored) {
        }
        worker = null;
    }

    public void setElo(int Elo) { // set elo of engine
        sendCommand("setoption name UCI_Elo value " + Elo);
    }

}
