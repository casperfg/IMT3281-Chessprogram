package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java
public class EngineHandler {
    private Process engine;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    private int eloRating = 2500;
    private static final String PATH = "./res/stockfish.exe";
    public boolean startEngine(){
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
    public void sendCommand(String command){
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void getFen(){

    }
    public void sendMove(String move, String fen){ // if fen="" use startpos
        if(fen == ""){
            fen = "startpos";
        }else{
            fen = "fen "+fen;
        }
        sendCommand("position "+fen+" move "+move);
    }
}
