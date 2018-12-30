package client.view;

import client.Controller;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainFrame extends JFrame {
    private JList<String> outlineList;
    private JTextArea editorArea;
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private MainFrame(String title, Controller controller, ListModel<String> listModel) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(createMenuBar());
        this.setContentPane(createSplitPane(listModel));
        this.setMinimumSize(new Dimension(1000, 500));
        this.controller = controller;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileOperationMenu());
        menuBar.add(createConnectionMenu());
        return menuBar;
    }

    private JMenu createConnectionMenu() {
        JMenu menu = new JMenu("Connection");
        menu.add(createConnectMenuItem());
        return menu;
    }

    private JMenuItem createConnectMenuItem() {
        JMenuItem menuItem = new JMenuItem("Connect", KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField ipField = new JTextField();
                JTextField portField = new JTextField();
                Object[] messages = {
                        "Input ip: ", ipField,
                        "Input port: ", portField,
                };
                int option = JOptionPane.showConfirmDialog(MainFrame.this,
                        messages,
                        "Enter server info",
                        JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String ip = ipField.getText();
                    String port = portField.getText();

                    System.out.println(ip);
                    System.out.println(port);
                }
            }
        });
        return menuItem;
    }

    private JMenu createFileOperationMenu() {
        JMenu menu = new JMenu("File");
        menu.add(createOpenMenuItem());
        menu.add(createSaveMenuItem());
        menu.add(createExportMenuItem());
        return menu;
    }

    private JMenuItem createOpenMenuItem() {
        JMenuItem menuItem = new JMenuItem("Open");
        menuItem.addActionListener((ActionEvent e)-> {
            JFileChooser fileChooser = createOpenFileChooser();
            int returnValueOfDialog = fileChooser.showOpenDialog(MainFrame.this);
            if (returnValueOfDialog == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                controller.readFile(file);
            }
        });
        return menuItem;
    }

    private JFileChooser createOpenFileChooser() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Markdown (*.md)", "md");
        chooser.addChoosableFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        return chooser;
    }

    private JFileChooser createSaveFileChooser() {
        return new JFileChooser();
    }

    private JMenuItem createSaveMenuItem() {
        JMenuItem menuItem = new JMenuItem("Save");
        menuItem.addActionListener((ActionEvent e)-> {
            JFileChooser fileChooser = createSaveFileChooser();
            int returnValueOfDialog = fileChooser.showSaveDialog(MainFrame.this);
            if (returnValueOfDialog == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                controller.saveFile(file);
            }
        });
        return menuItem;
    }

    private JMenuItem createExportMenuItem() {
        JMenuItem menuItem = new JMenuItem("Export");
        menuItem.addActionListener((ActionEvent e)-> {
            JFileChooser fileChooser = createSaveFileChooser();
            int returnValueOfDialog = fileChooser.showSaveDialog(MainFrame.this);
            if (returnValueOfDialog == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                controller.exportFile(file);
            }
        });
        return menuItem;
    }

    private JSplitPane createSplitPane(ListModel<String> listModel) {
        createEditorArea();
        JScrollPane outlinePane = createOutlinePane(listModel);
        JScrollPane editorPane = createEditorPane();
        return createSplitPane(outlinePane, editorPane);
    }

    private JSplitPane createSplitPane(JScrollPane leftPane, JScrollPane rightPane) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
        return splitPane;
    }

    private JScrollPane createEditorPane() {
        JScrollPane editorPane = new JScrollPane(editorArea);
        Dimension minimumSize = new Dimension(100, 50);
        editorPane.setMinimumSize(minimumSize);
        return editorPane;
    }

    private JScrollPane createOutlinePane(ListModel<String> listModel) {
        outlineList = new JList<>(listModel);
        JScrollPane outlinePane = new JScrollPane(outlineList);
        Dimension minimumSize = new Dimension(100, 50);
        outlinePane.setMinimumSize(minimumSize);
        return outlinePane;
    }

    private void createEditorArea() {
        editorArea = new JTextArea(5,30);
        editorArea.setEditable(true);
        editorArea.setLineWrap(true);
        editorArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //a char is inserted
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //a char is removed
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.handleTextChanges(getText());
            }
        });
    }

    public static MainFrame createAndShowGUI(Controller controller, ListModel<String> listModel) {
        MainFrame frame = new MainFrame("editor", controller, listModel);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public void createPopupDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void setText(String text) {
        editorArea.setText(text);
    }

    public String getText() {
        return editorArea.getText();
    }

    public JList<String> getOutlineList() {
        return outlineList;
    }
}
