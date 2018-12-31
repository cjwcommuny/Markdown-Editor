package client.view;

import client.Controller;
import client.exception.DialogNotShowException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFrame extends JFrame {
    private JList<String> outlineList;
    private JTextArea editorArea;
    private Controller controller;
    private static final int SLEEP_SEGMENT = 1000;
    private final AtomicBoolean textChanged = new AtomicBoolean(false);
    private ExecutorService threadPool = Executors.newCachedThreadPool();

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
        menu.add(createJoinCooperationMenuItem());
        menu.add(createEstablishCooperationMenuItem());
        menu.add(createDisconnectCooperationMenuItem());
        return menu;
    }

    private JMenuItem createEstablishCooperationMenuItem() {
        JMenuItem menuItem = new JMenuItem("Establish Cooperation");
        menuItem.addActionListener((ActionEvent event) -> {
            if (controller.isConnected()) {
                return;
            }
            try {
                String[] parameters = getConnectionParameter("Establish Cooperation");
                controller.establishCooperation(parameters[0], parameters[1], parameters[2]);
            } catch (DialogNotShowException e) {
                createPopupDialog("Bad Dialog", e.getMessage());
            }
        });
        return menuItem;
    }

    private JMenuItem createJoinCooperationMenuItem() {
        JMenuItem menuItem = new JMenuItem("Join Cooperation");
        menuItem.addActionListener((ActionEvent event) -> {
            if (controller.isConnected()) {
                return;
            }
            try {
                String[] parameters = getConnectionParameter("Join Cooperation");
                controller.joinCooperation(parameters[0], parameters[1], parameters[2]);
            } catch (DialogNotShowException e) {
                createPopupDialog("Bad Dialog", e.getMessage());
            }
        });
        return menuItem;
    }

    private JMenuItem createDisconnectCooperationMenuItem() {
        JMenuItem menuItem = new JMenuItem("Disconnect");
        menuItem.addActionListener((ActionEvent event) -> {
            //TODO: not connected
            if (!controller.isConnected()) {
                return;
            }
            controller.disconnectToServer();
        });
        return menuItem;
    }

    private String[] getConnectionParameter(String windowTitle) throws DialogNotShowException {
        JTextField ipField = new JTextField("127.0.0.1");
        JTextField portField = new JTextField("12345");
        JTextField idField = new JTextField("1");
        Object[] messages = {
                "Ip: ", ipField,
                "Port: ", portField,
                "Id: ", idField,
        };
        int option = JOptionPane.showConfirmDialog(MainFrame.this,
                messages,
                windowTitle,
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ip = ipField.getText();
            String port = portField.getText();
            String id = idField.getText();
            return new String[]{ip, port, id};
        }
        throw new DialogNotShowException();
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
        addDocumentListener();
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

    public void startTextUnChangeListening() {
        threadPool.execute(() -> {
            try {
                while (true) {
                    Thread.sleep(SLEEP_SEGMENT);
                    if (textChanged.compareAndSet(true, false)) {
                        controller.handleTextChanges(getText());
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Text Unchanged Listening Thread Interrupted: " + e.getMessage());
            }
        });
    }

    public void setTextUnChange() {
        textChanged.set(false);
    }

    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            //a char is inserted
            System.out.println("insert");
            System.out.println(getText());
            System.out.println("$$$$$$$$$$");
            controller.generateOutline(getText());
//                controller.handleTextChanges(getText());
            textChanged.set(true);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            //a char is removed
            System.out.println("remove");
            System.out.println(getText());
            System.out.println("$$$$$$$$$$");
            controller.generateOutline(getText());
//                controller.handleTextChanges(getText());
            textChanged.set(true);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            System.out.println("change update");
            System.out.println(getText());
            System.out.println("$$$$$$$$$$");
            controller.generateOutline(getText());
//                controller.handleTextChanges(getText());
            textChanged.set(true);
        }
    };

    public void removeDocumentListener() {
        editorArea.getDocument().removeDocumentListener(documentListener);
    }

    public void addDocumentListener() {
        editorArea.getDocument().addDocumentListener(documentListener);
    }
}
