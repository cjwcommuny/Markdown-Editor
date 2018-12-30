package client;

import client.exception.WrongConnectionParameterFormatException;
import transmission.TextPacket;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connector {
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream toServerStream;
    private ObjectInputStream fromServerStream;

    public void connectToServer(String ip, String port)
            throws WrongConnectionParameterFormatException, UnknownHostException, IOException {
        checkParameter(ip, port);
        this.ip = ip;
        this.port = Integer.parseInt(port);
        establishConnection();
    }

    public void disconnectToServer() throws IOException {
        closeConnection();
    }

    private void checkParameter(String ip, String port) throws WrongConnectionParameterFormatException {
        //TODO
    }

    private void establishConnection() throws UnknownHostException, IOException {
        socket = new Socket(ip, port);
        toServerStream = new ObjectOutputStream(socket.getOutputStream());
        fromServerStream = new ObjectInputStream(socket.getInputStream());
    }

    private void closeConnection() throws IOException {
        socket.close();
        socket = null;
        toServerStream = null;
        fromServerStream = null;
    }

    public boolean isConnected() {
        //TODO
        return socket != null;
    }

    public void sendToServer(TextPacket packet) throws IOException {
        if (isConnected()) {
            toServerStream.writeObject(packet);
        }
    }
}
