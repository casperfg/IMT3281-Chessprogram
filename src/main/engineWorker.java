package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class engineWorker extends Thread{
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    Chessboard cb; // board to calculate on
    Boolean done = false; // true if the calculation of bestmove is done
    String Best; // bestmove
    Boolean playerMated = false; // if the move done by engine mates the player
    private int thinkTime = 1000;

    public engineWorker(BufferedReader pr, OutputStreamWriter pw, int thTime, Chessboard cboard){
        processReader = pr; processWriter = pw;
        //playerMated = false;
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
                    if(text.contains("mate 0")){ // is in mate.
                        return "MaTe";
                    }
                    if (text.equals("readyok")) {
                        break;
                    } else{
                        buffer.append(text + "\n");
                    }
                    if(text.contains("mate 1 ")){ // this move mates the other player
                        playerMated = true;
                        break;
                    }
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
        out = getOutput(thinkTime+30);
        for (int i = 0; i < 3; i++){ // makes sure the bestmove call is caught
            try {
                if(out.contains("MaTe")){
                    break;
                }
                if(out.contains(" pv ") && !out.contains("bestmove")){
                    String[] lines = out.split("info ");
                    out = lines[lines.length-1];
                    out = out.split(" pv ")[1].split(" ")[0];
                    out = out.trim();
                }else{
                    out = out.split("bestmove ")[1].split(" ")[0];
                }
                break;
            } catch (Exception e) {
                out = getOutput(250);
            }
        }
        Best = out;
        System.out.println(Best);
        System.out.println(playerMated);
        done = true;
    }
}
