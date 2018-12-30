package client;

import client.view.MainFrame;
import transmission.TextPacket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {
    private Connector connector = new Connector();
    private MainFrame frame;
    public void connectToServer(String ip, String port) {
        try {
            connector.connectToServer(ip, port);
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage());
        }
    }

    public void disconnectToServer() {
        try {
            connector.disconnectToServer();
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage());
        }
    }

    public void handleTextChanges(String text) {
        sendTextToServer(text);
    }

    private void sendTextToServer(String text) {
        TextPacket packet = new TextPacket(text);
        try {
            connector.sendToServer(packet);
        } catch (IOException e) {
            frame.createPopupDialog("Send failed", e.getMessage());
        }
    }

    public static void main(String[] args) {
        Controller clientController = new Controller();
        clientController.frame = MainFrame.createAndShowGUI(clientController);
    }

    public void readFile(File file) {
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            String text = new String(encoded, Charset.defaultCharset());
            frame.changeText(text);
        } catch (IOException e) {
            frame.createPopupDialog("Open File Error", "Cannot read file");
        }
    }

    public void saveFile(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(frame.getText());
            writer.close();
        } catch (IOException e) {
            frame.createPopupDialog("Save File Error", "Cannot save file");
        }
    }
}
