package server;

import transmission.TextPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

class WriteSocketRunner implements Runnable {
    private ObjectOutputStream outputStream;
    private WriteSignal signal;

    WriteSocketRunner(Socket socket, WriteSignal signal) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.signal = signal;
        } catch (IOException e) {
            //TODO
        }
    }

    @Override
    public void run() {
        try {
            while (signal.isContinueRun()) {
                signal.await();
                TextPacket packet = signal.getPacket();
                outputStream.writeObject(packet);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("ERROR: Write Socket Runner: " + e.getMessage());
        }
    }
}


