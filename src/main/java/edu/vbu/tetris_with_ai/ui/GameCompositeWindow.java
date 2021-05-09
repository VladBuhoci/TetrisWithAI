package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Displays one or more games, each with their own viewport.
 */
public class GameCompositeWindow extends JFrame {

    private final Set<GameViewport> gameViewports;

    public GameCompositeWindow(String windowTitle, int viewportRows, int viewportColumns) {
        super(windowTitle);

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(viewportRows, viewportColumns));

        setIcon();

        this.gameViewports = new HashSet<>(10);
    }

    public void addViewport(GameViewport newViewport) {
        gameViewports.add(newViewport);

        add(newViewport);
    }

    public void display() {
        if (!gameViewports.isEmpty()) {
            // Workaround for making user input work via this window as it was configured for the first viewport (assuming a human-playable game is started).
            GameViewport firstViewport = gameViewports.iterator().next();

            if (firstViewport.isUserInputMapped()) {
                KeyListener userInputMappings = firstViewport.getKeyListeners()[0];

                addKeyListener(userInputMappings);
            }
        }

        pack();

        // Position to center of screen.
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void setIcon() {
        URL tetrisIconResource = ClassLoader.getSystemResource(Constants.WINDOW_ICON_PATH);
        Image tetrisIcon = Toolkit.getDefaultToolkit().createImage(tetrisIconResource);

        setIconImage(tetrisIcon);
    }
}
