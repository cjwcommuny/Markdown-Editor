package server;

import transmission.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ReadSocketRunner implements Runnable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private SessionCollection sessionCollection;
    private Session session;
    private WriteSignal signal;
    private boolean continueRun = true;

    public ReadSocketRunner(Socket socket, SessionCollection sessionCollection, WriteSignal signal) {
        try {
            this.socket = socket;
            inputStream = new ObjectInputStream(socket.getInputStream());
            this.signal = signal;
            this.sessionCollection = sessionCollection;
        } catch (IOException e) {
            //TODO
        }
    }

    @Override
    public void run() {
        try {
            while (continueRun) {
                Packet packet = (Packet) inputStream.readObject();
                handlePacket(packet);
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("ERROR: Read Socket Runner: " + e.getMessage());
        }
    }

    private void handlePacket(Packet packet) {
        Packet.PacketType type = packet.getPacketType();
        switch (type) {
            case ESTABLISH:
                if (session == null) {
                    establishSession(packet.getId(), packet.getText());
                }
                break;
            case JOIN:
                if (session == null) {
                    joinSession(packet.getId());
                }
                break;
            case TEXT:
                mergeText(packet.getText());
                break;
            case CLOSE:
                closeConnection();
                break;
            default:
        }
    }

    private void establishSession(int id, String text) {
        if (sessionCollection.get(id) != null) {
            reply(Packet.PacketType.REPLY, "Connection Has Established");
        }
        this.session = sessionCollection.newSession(id, text, signal);
        System.out.println("session " + id + " established");
    }

    private void reply(Packet.PacketType type, String message) {
        Packet packet = new Packet(type, message);
        signal.setPacket(packet);
        signal.signal();
    }

    private void joinSession(int id) {
        Session session = sessionCollection.get(id);
        if (session == null) {
            reply(Packet.PacketType.REPLY, "Id Not Exists");
        }
        this.session = session;
        session.addWriteSignal(signal);
        reply(Packet.PacketType.TEXT, session.getText());

    }

    private void mergeText(String text) {
        System.out.println("get text from " + socket.getPort());
        System.out.println("get text content: " + text + "$$$$$$$$$");
        String newText = TextMerger.merge(session.getText(), text);
        session.setText(newText);
        session.signalAllWrite();
    }

    private void closeConnection() {
        signal.stopRunning();
        session.deleteClient(signal.getIdInSession());
        continueRun = false;
    }
}
