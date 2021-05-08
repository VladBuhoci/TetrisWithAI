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

    public void displayWindow() {
        setVisible(true);
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

                    tetrisGame.endGame();
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

    private void updateLevelAndScoreLabel(int level, int score) {
        ((ScoreComponent) gameStats.getComponent(0)).setLevelAndScore(level, score);
    }

    private void updateUpcomingPieceLabel(String upcomingPieceName) {
        ((UpcomingPieceComponent) gameFuture.getComponent(0)).setUpcomingPieceName(upcomingPieceName);
    }
}
