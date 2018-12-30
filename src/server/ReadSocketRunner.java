package server;

import transmission.TextPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ReadSocketRunner implements Runnable {
    private ObjectInputStream inputStream;
    private SessionCollection sessionCollection;
    private Session session;
    private WriteSignal signal;
    private boolean continueRun = true;

    public ReadSocketRunner(Socket socket, SessionCollection sessionCollection, WriteSignal signal) {
        try {
//            this.socket = socket;
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
                TextPacket textPacket = (TextPacket) inputStream.readObject();
                handlePacket(textPacket);
            }
        } catch (ClassNotFoundException | IOException e) {
            //TODO
        }
    }

    private void handlePacket(TextPacket packet) {
        TextPacket.PacketType type = packet.getPacketType();
        switch (type) {
            case ESTABLISH:
                establishSession(packet.getId(), packet.getText());
                break;
            case JOIN:
                joinSession(packet.getId(), packet.getText());
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
            TextPacket packet = new TextPacket(TextPacket.PacketType.REPLY, "Cooperation Already Exists");
            signal.setPacket(packet);
            signal.signal();
        }
        this.session = sessionCollection.newSession(id, text, signal);
    }

    private void joinSession(int id, String text) {
        Session session = sessionCollection.get(id);
        if (session == null) {
            TextPacket packet = new TextPacket(TextPacket.PacketType.REPLY, "Id Not Exists");
            signal.setPacket(packet);
            signal.signal();
        }
        this.session = session;
        session.addWriteSignal(signal);
        signal.setPacket(new TextPacket(TextPacket.PacketType.TEXT, session.getText()));
        signal.signal();
    }

    private void mergeText(String text) {
        String newText = TextMerger.merge(session.getText(), text);
        session.setText(newText);
        session.signalAllWrite();
    }

    private void closeConnection() {
        signal.stopRunning();
        continueRun = false;
    }
}
