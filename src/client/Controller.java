package client;

import client.model.Model;
import client.view.MainFrame;
import transmission.Packet;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class Controller {
    private Connector connector = new Connector();
    private MainFrame frame;
    private Model model = new Model();

    public void establishCooperation(String ip, String port, String id) {
        try {
            connector.connectToServer(ip, port);
            connector.establishCooperation(id, frame.getText());
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage());
        }
    }

    public void joinCooperation(String ip, String port, String id) {
        try {
            connector.connectToServer(ip, port);
            connector.joinCooperation(id);
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
        generateOutline(text);
        if (connector.isConnected()) {
            sendTextToServer(text);
        }
    }

    private void generateOutline(String text) {
        List<MarkdownParser.Heading> list = MarkdownParser.getHeadingOfMarkdown(text);
        DefaultListModel<String> outlineListModel = model.getOutlineListModel();
        outlineListModel.clear();
        for (MarkdownParser.Heading heading: list) {
            String content = heading.getText();
            StringBuilder stringBuilder = new StringBuilder();
            int rank = heading.getRank();
            for (int i = 0; i < rank; ++i) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(content);
            outlineListModel.addElement(stringBuilder.toString());
        }
    }

    private void sendTextToServer(String text) {
        Packet packet = new Packet(Packet.PacketType.TEXT, text);
        try {
            connector.sendToServer(packet);
        } catch (IOException e) {
            frame.createPopupDialog("Send failed", e.getMessage());
        }
    }

    public static void main(String[] args) {
        Controller clientController = new Controller();
        clientController.frame =
                MainFrame.createAndShowGUI(clientController, clientController.model.getOutlineListModel());
    }

    public void readFile(File file) {
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            String text = new String(encoded, Charset.defaultCharset());
            frame.setText(text);
        } catch (IOException e) {
            frame.createPopupDialog("Open File Error", "Cannot read file");
        }
    }

    public void saveFile(File file) {
        try {
            saveTextToFile(file, frame.getText());
        } catch (IOException e) {
            frame.createPopupDialog("Save File Error", "Cannot save file");
        }
    }

    private void saveTextToFile(File file, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
        writer.close();
    }

    public void exportFile(File file) {
        String makrdownText = frame.getText();
        String html = MarkdownParser.parseMarkdown(makrdownText);
        try {
            saveTextToFile(file, html);
        } catch (IOException e) {
            frame.createPopupDialog("Export File Error", "Cannot Export File");
        }
    }

    public boolean isConnected() {
        return connector.isConnected();
    }
}
