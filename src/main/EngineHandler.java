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
}
