package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private JList outlineList;
    private JTextArea editorArea;
    JMenuBar menuBar;


    private MainFrame(String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(createMenuBar());
        this.setContentPane(createSplitPane());
        this.setMinimumSize(new Dimension(1000, 500));
    }

    private JMenuBar createMenuBar() {
        menuBar = new JMenuBar();

        JMenu menu = new JMenu("menu 1");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("menu iterm 1", KeyEvent.VK_T);
        menu.add(menuItem);
        return menuBar;
    }

    private JSplitPane createSplitPane() {
        String[] imageNames = { "Bird", "Cat", "Dog", "Rabbit", "Pig", "dukeWaveRed",
                "kathyCosmo", "lainesTongue", "left", "middle", "right", "stickerface"};
        outlineList = new JList(imageNames);
        JScrollPane outlinePane = new JScrollPane(outlineList);

        editorArea = new JTextArea(5,30);
        editorArea.setEditable(true);
        JScrollPane editorPane = new JScrollPane(editorArea);
        Dimension minimumSize = new Dimension(100, 50);
        outlinePane.setMinimumSize(minimumSize);
        editorPane.setMinimumSize(minimumSize);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outlinePane, editorPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

        return splitPane;
    }

    private static void createAndShowGUI() {
        MainFrame frame = new MainFrame("editor");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
