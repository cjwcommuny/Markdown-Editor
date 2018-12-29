package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private JScrollPane outlinePane;
    private JScrollPane editorPane;


    private MainFrame(String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(createMenuBar());
        this.setContentPane(createSplitPane());
        this.setMinimumSize(new Dimension(1000, 500));
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("menu 1");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("menu iterm 1", KeyEvent.VK_T);
        menu.add(menuItem);
        return menuBar;
    }

    private JSplitPane createSplitPane() {
        String[] imageNames = { "Bird", "Cat", "Dog", "Rabbit", "Pig", "dukeWaveRed",
                "kathyCosmo", "lainesTongue", "left", "middle", "right", "stickerface"};
        JList outlineList = new JList(imageNames);
        outlinePane = new JScrollPane(outlineList);

        JTextArea editorArea = new JTextArea(5,30);
        editorArea.setEditable(true);
        editorPane = new JScrollPane(editorArea);
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
