package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class engineWorker extends Thread{
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    Chessboard cb;
    Boolean done = false;
    String Best;
    private int thinkTime = 1000;

    public engineWorker(BufferedReader pr, OutputStreamWriter pw, int thTime, Chessboard cboard){
        processReader = pr; processWriter = pw;
        thinkTime = thTime;
        cb = cboard;
    }
    public engineWorker(BufferedReader pr, OutputStreamWriter pw){
        processReader = pr; processWriter = pw;
    }
    public void run(){
        getBestMove();
    }
    public boolean sendCommand(String command){
        try {
            processWriter.write(command + "\n");
            processWriter.flush(); // "send"
        } catch (IOException e) {
            e.printStackTrace();
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
                if(text != null) {
                    if (text.contains("mate 0")) {
                        return "MaTe";
                    }
                    if (text.equals("readyok")) {
                        break;
                    } else {
                        buffer.append(text + "\n");
                    }
                }else{
                    Best = "broken"; done = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public void getBestMove() {
        String out;
        done = false;
        sendCommand("position fen " + cb.toFen());
        sendCommand("go movetime " + String.valueOf(thinkTime));
        out = getOutput(thinkTime + 30);
        for (int i = 0; i < 3; i++){ // makes sure the bestmove call is caught
            try {
                if(out.contains("MaTe")){
                    break;
                }
                out = out.split("bestmove ")[1].split(" ")[0];
                break;
            } catch (Exception e) {
                out = getOutput(250);
            }
        }
        Best = out;
        done = true;
    }
}
