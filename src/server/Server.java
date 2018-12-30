package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 123456;
    private int clientNumber = 0;
    private SessionCollection sessionCollection = new SessionCollection();
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ++clientNumber;
                createThread(socket);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void createThread(Socket socket) {
        WriteSignal signal = new WriteSignal();
        threadPool.execute(new WriteSocketRunner(socket, signal));
        threadPool.execute(new ReadSocketRunner(socket, sessionCollection, signal));
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }
}
