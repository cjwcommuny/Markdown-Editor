package server;

import transmission.Packet;

import java.util.*;

class Session {
    private Map<Integer, WriteSignal> writeSignalMap = Collections.synchronizedMap(new HashMap<>());
    private String text;

    Session(String text) {
        this.text = text;
    }

    synchronized void addWriteSignal(WriteSignal signal) {

        writeSignalMap.put(signal.getIdInSession(), signal);
    }

    synchronized void setText(String text) {
        this.text = text;
    }

    synchronized String getText() {
        return text;
    }

    synchronized void signalAllWrite() {
        for (Map.Entry<Integer, WriteSignal> entry: writeSignalMap.entrySet()) {
            WriteSignal signal = entry.getValue();
            signal.setPacket(new Packet(Packet.PacketType.TEXT, text));
            signal.signal();
        }
    }

    synchronized void deleteClient(int key) {
        writeSignalMap.remove(key);
        if (writeSignalMap.size() == 0) {
            //TODO
        }
    }
}
