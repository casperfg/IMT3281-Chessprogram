package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java
public class EngineHandler{
    private Process engine;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    public int eloRating = 2500;
    public int thinkTime = 1000; // ms

    private engineWorker worker; // workThread that lets engine wait and calculate
    private static String PATH = "./res/stockfishWin.exe"; // path for window
    private static final String PATHmac = "./res/stockfish"; // path max

    public EngineHandler(){ // start the engine
        startEngine();
    }
    public EngineHandler(int elo, int thkTime){ // start the engine
        eloRating = elo; thinkTime = thkTime;
        startEngine();
    }
    public void getBest(Chessboard cboard){ // get best move
        worker = new engineWorker(processReader, processWriter, thinkTime, cboard);
        worker.start(); // make new Workerthread and start it
    }
    public String checkWorker(){ // check if the workerthread is finished
        if(worker != null) {
            if (worker.done) {
                worker.done = false;
                return worker.Best;
            }
        }
        return "-1";
    }
    public void checkMate(Chessboard cboard){
        thinkTime = 100;
        getBest(cboard);
    }
    private void startEngine(){
        String os = System.getProperty("os.name");
        if(!os.contains("Windows")){ // mac functionality
            PATH = PATHmac;
        }
        try{
            engine = Runtime.getRuntime().exec(PATH); // execute exe
            // made reader / writer stream
            processReader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            processWriter = new OutputStreamWriter(engine.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void sendCommand(String cmd){
        worker = new engineWorker(processReader, processWriter);
        worker.sendCommand(cmd);
    }
    private void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException ignored) {
        }
    }

    public void setElo(int Elo){
        sendCommand("setoption name UCI_LimitStrength value true");
        sendCommand("setoption name UCI_Elo value "+String.valueOf(Elo));
    }

}
