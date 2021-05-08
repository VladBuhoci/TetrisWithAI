package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class GameWindow extends JFrame {

    private static final Logger LOG = LogManager.getLogger(GameWindow.class);

    private final TetrisGame tetrisGame;

    private JPanel gameFuture;
    private JPanel gameStats;
    private JPanel topSideFiller;

    public GameWindow(String windowTitle, TetrisGame tetrisGame) {
        super(windowTitle);

        this.tetrisGame = tetrisGame;

        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIcon();
        setUpUserInputMappings();
        setUpCallbacks();

        add(createMainPanel());

        // Position to center of screen.
        setLocationRelativeTo(null);
    }

    private void setIcon() {
        URL tetrisIconResource = ClassLoader.getSystemResource(Constants.WINDOW_ICON_PATH);
        Image tetrisIcon = Toolkit.getDefaultToolkit().createImage(tetrisIconResource);

        setIconImage(tetrisIcon);
    }

    private JPanel createMainPanel() {
        Color mainPanelColour = Color.black;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColour);

        gameStats = createStatsPanel(mainPanelColour);
        gameFuture = createFuturePiecePanel(mainPanelColour);
        topSideFiller = createTopSideFillerPanel(mainPanelColour);

        JPanel bottomSideFiller = new JPanel();
        bottomSideFiller.setBackground(mainPanelColour);
        bottomSideFiller.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, (int) (Constants.WINDOW_HEIGHT * Constants.BOTTOM_FILLER_SPACE_HEIGHT_PERCENTAGE)));

        mainPanel.add(tetrisGame.getGameGrid(), BorderLayout.CENTER);
        mainPanel.add(gameStats, BorderLayout.WEST);
        mainPanel.add(gameFuture, BorderLayout.EAST);
        mainPanel.add(topSideFiller, BorderLayout.NORTH);
        mainPanel.add(bottomSideFiller, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void setUpUserInputMappings() {
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
                    tetrisGame.movePieceDownOneRow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    tetrisGame.movePieceLeftOneColumn();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tetrisGame.movePieceRightOneColumn();
                }

                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    tetrisGame.rotatePieceLeftOnce();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    tetrisGame.rotatePieceRightOnce();
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

    private void setUpCallbacks() {
        tetrisGame.setOnGameOverCallback(() -> ((StatusComponent) topSideFiller.getComponent(0)).setStatusGameOver(true));
        tetrisGame.setOnSpawnPieceCallback(this::updateUpcomingPieceLabel);
        tetrisGame.setOnScoreIncreasedCallback(this::updateLevelAndScoreLabel);
    }

    private JPanel createStatsPanel(Color backgroundColour) {
        JPanel statsPanel = new JPanel(true);
        statsPanel.setBackground(backgroundColour);
        statsPanel.setPreferredSize(new Dimension((int) (Constants.WINDOW_WIDTH * Constants.WEST_SPACE_WIDTH_PERCENTAGE), Constants.WINDOW_HEIGHT));
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        statsPanel.add(new ScoreComponent(backgroundColour));

        return statsPanel;
    }

    private JPanel createFuturePiecePanel(Color backgroundColour) {
        JPanel futurePiecePanel = new JPanel(true);
        futurePiecePanel.setBackground(backgroundColour);
        futurePiecePanel.setPreferredSize(new Dimension((int) (Constants.WINDOW_WIDTH * Constants.EAST_SPACE_WIDTH_PERCENTAGE), Constants.WINDOW_HEIGHT));
        futurePiecePanel.setLayout(new BoxLayout(futurePiecePanel, BoxLayout.Y_AXIS));

        futurePiecePanel.add(new UpcomingPieceComponent(backgroundColour));

        return futurePiecePanel;
    }

    private JPanel createTopSideFillerPanel(Color backgroundColour) {
        JPanel topFillerPanel = new JPanel();
        topFillerPanel.setBackground(backgroundColour);
        topFillerPanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, (int) (Constants.WINDOW_HEIGHT * Constants.TOP_FILLER_SPACE_HEIGHT_PERCENTAGE)));
        topFillerPanel.setLayout(new BoxLayout(topFillerPanel, BoxLayout.Y_AXIS));

        topFillerPanel.add(new StatusComponent(backgroundColour));

        return topFillerPanel;
    }

    public void displayWindow() {
        setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Game flow
    ////////////////////////////////////////////////////////////////////////////////

    private void updateLevelAndScoreLabel(int level, int score) {
        ((ScoreComponent) gameStats.getComponent(0)).setLevelAndScore(level, score);
    }

    private void updateUpcomingPieceLabel(String upcomingPieceName) {
        ((UpcomingPieceComponent) gameFuture.getComponent(0)).setUpcomingPieceName(upcomingPieceName);
    }

//    private void startGame() {
//        LOG.info("Starting game thread.");
//
//        // Spawn initial piece.
//        spawnNewPiece();
//
//        // Init session vars.
//        isGameSessionRunning = true;
//        pieceMoveDownTimesPerSecond = 1.0;
//        score = 0;
//        level = 0;
//        clearedLinesCount = 0;
//
//        // Initial delay.
//        waitForMillis(300L);
//
//        // Create a thread that brings the current piece down at fixed rates.
//        pieceDescendingThread = new Thread(() -> {
//            while (isGameSessionRunning) {
//                if (gameGrid.isPieceCollidingBottom()) {
//                    int clearedRows = gameGrid.tryClearCompletedHorizLines();
//                    if (clearedRows > 0) {
//                        // If there were any completed (and cleared by now) horizontal lines, raise the score accordingly.
//                        increaseScore(clearedRows);
//                    }
//
//                    try {
//                        spawnNewPiece();
//                    } catch (IllegalStateException e) {
//                        // Failure to apply the new piece's colours in one or more cells means the place is already (partially) occupied by other piece(s).
//                        // Consider it to be game over.
//                        endGame();
//                    }
//                } else {
//                    gameGrid.movePieceDownOneRow();
//                }
//
//                waitForMillis(getWaitTimeForCurrentLevel());
//            }
//        }, "PieceElevatorThread");
//        pieceDescendingThread.setDaemon(true);
//        pieceDescendingThread.start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> isGameSessionRunning = false));
//    }
//
//    private void endGame() {
//        LOG.info("Stopping game thread.");
//
//        isGameSessionRunning = false;
//        ((StatusComponent) topSideFiller.getComponent(0)).setStatusGameOver(true);
//    }
//
//    private Shape upcomingPiece;
//    private boolean isGameSessionRunning;
//    private Thread pieceDescendingThread;
//    private double pieceMoveDownTimesPerSecond;
//    private int score;
//    private int level;
//    private int clearedLinesCount;
//
//    private static final int LINES_REQUIRED_FOR_LEVEL_UP = 10;
//
//    private void spawnNewPiece() {
//        gameGrid.setCurrentFallingPiece(getUpcomingPiece());
//        updateUpcomingPieceLabel();
//    }
//
//    private Shape getUpcomingPiece() {
//        Shape currentUpcomingPiece = upcomingPiece != null ? upcomingPiece : determineNewUpcomingPiece();
//        upcomingPiece = determineNewUpcomingPiece();
//
//        return currentUpcomingPiece;
//    }
//
//    private Shape determineNewUpcomingPiece() {
//        Shape chosenShape = Shapes.getRandomShape();
//
//        LOG.debug("Chosen new random upcoming shape: {}", () -> chosenShape);
//
//        return chosenShape;
//    }
//
//    private void increaseScore(int clearedLines) {
//        int deltaScore = 0;
//
//        clearedLinesCount += clearedLines;
//        level = clearedLinesCount / LINES_REQUIRED_FOR_LEVEL_UP;
//
//        switch (clearedLines) {
//            case 1:
//                deltaScore = 40 * (level + 1);
//                break;
//            case 2:
//                deltaScore = 100 * (level + 1);
//                break;
//            case 3:
//                deltaScore = 300 * (level + 1);
//                break;
//            case 4:
//                deltaScore = 1200 * (level + 1);
//                break;
//        }
//
//        score += deltaScore;
//        updateScoreLabel();
//    }
//
//    private void updateScoreLabel() {
//        ((ScoreComponent) gameStats.getComponent(0)).setLevelAndScore(level, score);
//    }
//
//    private void updateUpcomingPieceLabel() {
//        ((UpcomingPieceComponent) gameFuture.getComponent(0)).setUpcomingPieceName(upcomingPiece.getName());
//    }
//
//    private void waitForMillis(long millis) {
//        try {
//            Thread.sleep(millis);
//        } catch (InterruptedException e) {
//            LOG.warn("An error occurred while trying to wait before moving the piece down again: {}", () -> e);
//        }
//    }
//
//    private long getWaitTimeForCurrentLevel() {
//        return (long) (1.0 / pieceMoveDownTimesPerSecond * 1000.0);
//    }
}
