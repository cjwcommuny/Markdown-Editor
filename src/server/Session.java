package server;

import transmission.TextPacket;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class Session {
    private List<WriteSignal> writeSignals = Collections.synchronizedList(new LinkedList<>());
    private String text;

    Session(String text) {
        this.text = text;
    }

    synchronized void addWriteSignal(WriteSignal signal) {
        writeSignals.add(signal);
    }

    synchronized void setText(String text) {
        this.text = text;
    }

    synchronized String getText() {
        return text;
    }

    synchronized void signalAllWrite() {
        for (WriteSignal signal: writeSignals) {
            signal.setPacket(new TextPacket(TextPacket.PacketType.TEXT, text));
            signal.signal();
        }
    }
}
