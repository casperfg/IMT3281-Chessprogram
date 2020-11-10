package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java
public class EngineHandler extends Thread{
    private Process engine;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    public int eloRating = 2500;
    public int thinkTime = 500; // ms
    public boolean done = false;
    String Best;
    private static String PATH = "./res/stockfishWin.exe";
    private static final String PATHmac = "./res/stockfish";


    public EngineHandler(){
        startEngine();
    }
    public void run(Chessboard ch){
        getBestMove(ch);
    }
    public boolean startEngine(){
        String os = System.getProperty("os.name");
        if(!os.contains("Windows")){
            PATH = PATHmac;
        }
        try{
            engine = Runtime.getRuntime().exec(PATH); // execute exe
            // made reader / writer stream
            processReader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            processWriter = new OutputStreamWriter(engine.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean sendCommand(String command){
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            startEngine();
            sendCommand(command);
        }
        return true;
    }
    public String getOutput(int waitTime) {
        StringBuffer buffer = new StringBuffer();
        try {
            Thread.sleep(waitTime);
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    buffer.append(text + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public void setElo(int Elo){
        sendCommand("setoption name UCI_Elo value "+String.valueOf(Elo));
    }
    public String getBestMove(Chessboard cb) {
        String out;

        done = false;
        sendCommand("position fen " + cb.toFen());
        sendCommand("go movetime " + String.valueOf(thinkTime));
        out = getOutput(thinkTime + 30);
        for (int i = 0; i < 3; i++){ // makes sure the bestmove call is caught
            try {
                out = out.split("bestmove ")[1].split(" ")[0];
                break;
            } catch (Exception e) {
                out = getOutput(250);
            }
        }
        Best = out;
        done = true;
        return out;
    }
}
