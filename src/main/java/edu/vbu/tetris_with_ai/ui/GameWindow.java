package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.core.shapes.Shapes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

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
        setIcon();

        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Not required.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    LOG.debug("Closing game window via Escape key press event.");
                    dispose();
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

                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    gameGrid.rotatePieceLeftOnce();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    gameGrid.rotatePieceRightOnce();
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    // TODO: drop the current piece all the way down.
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not required.
            }
        });
    }

    private void setIcon() {
        URL tetrisIconResource = ClassLoader.getSystemResource("tetris.png");
        Image tetrisIcon = Toolkit.getDefaultToolkit().createImage(tetrisIconResource);

        setIconImage(tetrisIcon);
    }

    private JPanel createMainPanel() {
        Color mainPanelColour = Color.black;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColour);

        gameGrid = new GameGrid(CELL_COUNT_HORIZONTALLY, CELL_COUNT_VERTICALLY, true);

        JPanel gameStats = new JPanel(true);
        gameStats.setBackground(Color.black);//green);
        gameStats.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * WEST_SPACE_WIDTH_PERCENTAGE), WINDOW_HEIGHT));

        JPanel gameFuture = createFuturePiecePanel();

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

    private JPanel createFuturePiecePanel() {
        JPanel gameFuture = new JPanel(true);
        gameFuture.setBackground(Color.red);
        gameFuture.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * EAST_SPACE_WIDTH_PERCENTAGE), WINDOW_HEIGHT));
        gameFuture.setLayout(new BoxLayout(gameFuture, BoxLayout.Y_AXIS));

        gameFuture.add(new UpcomingPieceComponent(Color.red));
        gameFuture.add(new UpcomingPieceComponent(Color.yellow));
        gameFuture.add(new UpcomingPieceComponent(Color.pink));

        return gameFuture;
    }


    public void start() {
        setVisible(true);
        startGameThread();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Game flow
    ////////////////////////////////////////////////////////////////////////////////

    private void startGameThread() {
        // Initial piece.
        gameGrid.setCurrentFallingPiece(getUpcomingPiece());

        // Create a thread that brings the current piece down at fixed rates.
        isGameSessionRunning = true;
        waitTimeBetweenAutomatedPieceDescending = 1000L;

        pieceDescendingThread = new Thread(() -> {
            while (isGameSessionRunning) {
                try {
                    Thread.sleep(waitTimeBetweenAutomatedPieceDescending);
                } catch (InterruptedException e) {
                    LOG.warn("An error occurred while trying to wait before moving the piece down again: {}", () -> e);
                }

                if (gameGrid.isPieceCollidingBottom()) {
                    gameGrid.setCurrentFallingPiece(getUpcomingPiece());
                } else {
                    gameGrid.movePieceDownOneRow();
                }
            }
        }, "PieceElevatorThread");
        pieceDescendingThread.setDaemon(true);
        pieceDescendingThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> isGameSessionRunning = false));
    }

    private Shape upcomingPiece;
    private boolean isGameSessionRunning;
    private Thread pieceDescendingThread;
    private long waitTimeBetweenAutomatedPieceDescending;

    private Shape getUpcomingPiece() {
        Shape currentUpcomingPiece = upcomingPiece != null ? upcomingPiece : determineNewUpcomingPiece();
        upcomingPiece = determineNewUpcomingPiece();

        return currentUpcomingPiece;
    }

    private Shape determineNewUpcomingPiece() {
        Shape chosenShape = Shapes.getRandomShape();

        LOG.debug("Chosen new random upcoming shape: {}", () -> chosenShape);

        return chosenShape;
    }
}
