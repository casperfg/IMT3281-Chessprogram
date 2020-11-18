package main.networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public abstract class NetworkConnection {

    private final ConnectionThread thread;
    // Consumer function will be called when receiving data.
    // Use Platform.runLater() to prevent GUI problems
    private final Consumer<Serializable> callOnReceive;

    public NetworkConnection(Consumer<Serializable> callOnReceive) {
        this.callOnReceive = callOnReceive;

        thread = new ConnectionThread();

        thread.setDaemon(true);  // Background thread
    }

    protected abstract boolean isServer();  // true if creating a server

    protected abstract String getIP();

    protected abstract int getPort();

    public void start() {
        thread.start();  // Thread class will start the thread and call run()
    }

    public void close() throws Exception {
        thread.socket.close();
    }

    public void send(Serializable data) throws Exception {
        thread.out.writeObject(data);
    }

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            // Only initiate new server socket if isServer() is true
            // Accept socket if server, create socket if client
            try (ServerSocket server = (isServer() ? new ServerSocket(getPort()) : null);
                 Socket socket = (isServer() ? server.accept() : new Socket(getIP(), getPort()));
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ) {
                this.socket = socket;
                this.out = out;

                // Lower latency by disabling Nagle's algorithm. We're only using small sets of data
                socket.setTcpNoDelay(true);

                System.out.println("ip: " + getIP() + ", port: " + getPort());

                // Wait for data to be received
                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    callOnReceive.accept(data);
                }
            } catch (Exception e) {
                System.out.println("Connection with " + (isServer() ? "client" : "server") + " closed");
            }
        }
    }
}
