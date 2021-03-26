package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.Square;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame {

    private static final Logger LOG = LogManager.getLogger(GameWindow.class);

    private static final int CELL_COUNT_HORIZONTALLY = 10;
    private static final int CELL_COUNT_VERTICALLY = 20;

    private static final int CELL_SIZE = 4;     // horizontally, as well as vertically, measured in pixels.

    private static final int WINDOW_WIDTH = 300 + CELL_COUNT_HORIZONTALLY * CELL_SIZE * 10;
    private static final int WINDOW_HEIGHT = 200 + CELL_COUNT_VERTICALLY * CELL_SIZE * 10;

    // Hoe much of the total window height should be allocated for each empty area (up and bottom).
    private static final double TOP_FILLER_SPACE_HEIGHT_PERCENTAGE = 0.1;
    private static final double BOTTOM_FILLER_SPACE_HEIGHT_PERCENTAGE = 0.1;

    // Hoe much of the total window width should be allocated for each side area (west/stats panel and east/future pieces panel).
    private static final double WEST_SPACE_WIDTH_PERCENTAGE = 0.225;
    private static final double EAST_SPACE_WIDTH_PERCENTAGE = 0.225;

    private GameGrid gameGrid;

    public GameWindow() {
        super("Tetris with AI");

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    LOG.debug("Closing game window via Escape key press event.");
                    dispose();
                }

                // TODO: testing only
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    LOG.warn("Spawning dev shape");
                    Square devSquare = new Square();

                    gameGrid.setCurrentFallingPiece(devSquare);
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    gameGrid.movePieceDownOneRow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    gameGrid.movePieceLeftOneColumn();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    gameGrid.movePieceRightOneColumn();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private JPanel createMainPanel() {
        Color mainPanelColour = Color.black;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColour);

        gameGrid = new GameGrid(CELL_COUNT_HORIZONTALLY, CELL_COUNT_VERTICALLY, true);
//        gameGrid.setBackground(Color.gray);

        JPanel gameStats = new JPanel(true);
        gameStats.setBackground(Color.black);//green);
        gameStats.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * WEST_SPACE_WIDTH_PERCENTAGE), WINDOW_HEIGHT));

        JPanel gameFuture = new JPanel(true);
        gameFuture.setBackground(Color.black);//red);
        gameFuture.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * EAST_SPACE_WIDTH_PERCENTAGE), WINDOW_HEIGHT));

        JPanel topSideFiller = new JPanel();
        topSideFiller.setBackground(mainPanelColour);
        topSideFiller.setPreferredSize(new Dimension(WINDOW_WIDTH, (int) (WINDOW_HEIGHT * TOP_FILLER_SPACE_HEIGHT_PERCENTAGE)));

        JPanel bottomSideFiller = new JPanel();
        bottomSideFiller.setBackground(mainPanelColour);
        bottomSideFiller.setPreferredSize(new Dimension(WINDOW_WIDTH, (int) (WINDOW_HEIGHT * BOTTOM_FILLER_SPACE_HEIGHT_PERCENTAGE)));

        mainPanel.add(gameGrid, BorderLayout.CENTER);
        mainPanel.add(gameStats, BorderLayout.WEST);
        mainPanel.add(gameFuture, BorderLayout.EAST);
        mainPanel.add(topSideFiller, BorderLayout.NORTH);
        mainPanel.add(bottomSideFiller, BorderLayout.SOUTH);

        return mainPanel;
    }

    public void start() {
        setVisible(true);
    }
}
