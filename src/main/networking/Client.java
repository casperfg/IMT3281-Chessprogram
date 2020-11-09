package sample.networking;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends NetworkConnection {

    private String IP;
    private int port;

    public Client(String IP, int port, Consumer<Serializable> callOnReceive) {
        super(callOnReceive);
        this.IP = IP;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return IP;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
