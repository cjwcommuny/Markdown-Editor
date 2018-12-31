package client.controller;

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
    private Connector connector = new Connector(this);
    private MainFrame frame;
    private Model model = new Model();

    public void establishCooperation(String ip, String port, String id) {
        try {
            connector.connectToServer(ip, port);
            connector.startReadSocket();
            connector.establishCooperation(id, frame.getText());
            frame.disconnectMenuItemSetEnabled(true);
            frame.establishJoinMenuItemSetEnabled(false);
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void joinCooperation(String ip, String port, String id) {
        try {
            connector.connectToServer(ip, port);
            connector.startReadSocket();
            connector.joinCooperation(id);
            frame.disconnectMenuItemSetEnabled(true);
            frame.establishJoinMenuItemSetEnabled(false);
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnectToServer() {
        try {
            connector.disconnectToServer();
            frame.disconnectMenuItemSetEnabled(false);
            frame.establishJoinMenuItemSetEnabled(true);
        } catch (Exception e) {
            frame.createPopupDialog("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleTextChanges(String text) {
        if (connector.isConnected()) {
            sendTextToServer(text);
        }
    }

    public void generateOutline(String text) {
        List<MarkdownParser.Heading> list = MarkdownParser.getHeadingOfMarkdown(text);
        DefaultListModel<String> outlineListModel = model.getOutlineListModel();
        outlineListModel.clear();
        for (MarkdownParser.Heading heading: list) {
            String content = heading.getText();
            StringBuilder stringBuilder = new StringBuilder();
            int rank = heading.getRank();
            for (int i = 0; i < rank * MainFrame.OUTLINE_INDENT; ++i) {
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
            frame.createPopupDialog("Send failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Controller clientController = new Controller();
        clientController.frame =
                MainFrame.createAndShowGUI(clientController, clientController.model.getOutlineListModel());
        clientController.frame.startTextUnChangeListening();
    }

    public void readFile(File file) {
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            String text = new String(encoded, Charset.defaultCharset());
            frame.setText(text);
        } catch (IOException e) {
            frame.createPopupDialog("Open File Error", "Cannot read file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFile(File file) {
        try {
            saveTextToFile(file, frame.getText());
        } catch (IOException e) {
            frame.createPopupDialog("Save File Error", "Cannot save file", JOptionPane.ERROR_MESSAGE);
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
            frame.createPopupDialog("Export File Error", "Cannot Export File", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConnected() {
        return connector.isConnected();
    }

    public void handlePacketReceived(Packet packet) throws IOException {
        Packet.PacketType type = packet.getPacketType();
        switch (type) {
            case TEXT:
                updateText(packet.getText());
                break;
            case REPLY:
                displayMessage(packet.getText());
                break;
            case CLOSE:
                frame.createPopupDialog("Invalid Parameter", packet.getText(), JOptionPane.ERROR_MESSAGE);
                connector.disconnectToServer(); //TODO: handle IOException
                frame.disconnectMenuItemSetEnabled(false);
                frame.establishJoinMenuItemSetEnabled(true);
            default:
        }
    }

    private void updateText(String text) {
        //TODO: thread safety
        frame.removeDocumentListener();
        frame.setText(text);
        frame.addDocumentListener();
        generateOutline(text);
        frame.setTextUnChange();
    }

    private void displayMessage(String message) {
        frame.createPopupDialog("Message", message, JOptionPane.PLAIN_MESSAGE);
    }
}
