package server;

import sun.awt.image.GifImageDecoder;
import transmission.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

class WriteSocketRunner implements Runnable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private WriteSignal signal;

    WriteSocketRunner(Socket socket, WriteSignal signal) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.signal = signal;
        } catch (IOException e) {
            System.out.println("ERROR: Write Socket new output stream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (signal.isContinueRun()) {
                signal.await();
                Packet packet = signal.getPacket();
                System.out.println("write to: " + socket.getPort() + ", type: " +packet.getPacketType());
                System.out.println("content: " + packet.getText());
                outputStream.writeObject(packet);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("ERROR: Write Socket Runner: " + e.getMessage());
        }
    }
}


