package client;

import client.exception.WrongConnectionParameterFormatException;
import transmission.Packet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Connector {
    private String ip;
    private int port;
    private Socket socket;
    private ObjectOutputStream toServerStream;
    private ObjectInputStream fromServerStream;
    private Controller controller;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public Connector(Controller controller) {
        this.controller = controller;
    }

    void connectToServer(String ip, String port)
            throws WrongConnectionParameterFormatException, UnknownHostException, IOException {
        this.port = checkParameter(ip, port);
        this.ip = ip;
        establishConnection();
    }

    void startReadSocket() {
        threadPool.execute(() -> {
            try {
                while (fromServerStream != null) {
                    Packet packet = (Packet) fromServerStream.readObject();
                    controller.handlePacketReceived(packet);
                }
            } catch (ClassNotFoundException | IOException e) {
                //TODO
                System.out.println("connector read failed");
            }
        });
    }

    void establishCooperation(String idStr, String text)
            throws WrongConnectionParameterFormatException, IOException {
        int id = parseId(idStr);
        Packet packet = new Packet(Packet.PacketType.ESTABLISH, text, id);
        toServerStream.writeObject(packet);
    }

    void joinCooperation(String idRaw) throws WrongConnectionParameterFormatException, IOException {
        int id = parseId(idRaw);
        Packet packet = new Packet(Packet.PacketType.JOIN, null, id);
        toServerStream.writeObject(packet);
    }

    private int parseId(String id) throws WrongConnectionParameterFormatException {
        return Integer.parseInt(id);
    }

    void disconnectToServer() throws IOException {
        closeConnection();
    }

    private int checkParameter(String ip, String port) throws WrongConnectionParameterFormatException {
        //TODO: check ip format
        return Integer.parseInt(port);
    }

    private void establishConnection() throws UnknownHostException, IOException {
        socket = new Socket(ip, port);
        toServerStream = new ObjectOutputStream(socket.getOutputStream());
        fromServerStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("local address: " + socket.getLocalAddress() + ", local port: " + socket.getLocalPort());
    }

    private void closeConnection() throws IOException {
        Packet packet = new Packet(Packet.PacketType.CLOSE, null);
        toServerStream.writeObject(packet);
        socket.close();
        socket = null;
        toServerStream = null;
        fromServerStream = null;
    }

    boolean isConnected() {
        return socket != null;
    }

    void sendToServer(Packet packet) throws IOException {
        if (isConnected()) {
            toServerStream.writeObject(packet);
        }
    }
}
