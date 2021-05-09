package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameViewport extends JPanel {

    private static final Logger LOG = LogManager.getLogger(GameViewport.class);

    private final TetrisGame tetrisGame;
    private final boolean userInputMapped;

    private JPanel gameFuture;
    private JPanel gameStats;
    private JPanel topSideFiller;
    private JPanel bottomSideFiller;


    public GameViewport(String gameLabel, TetrisGame tetrisGame, boolean mapUserInput) {
        this.tetrisGame = tetrisGame;

        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        this.userInputMapped = mapUserInput;

        if (this.userInputMapped) {
            setUpUserInputMappings();
        }
        setUpCallbacks();

        add(createMainPanel(gameLabel));
    }

    public boolean isUserInputMapped() {
        return userInputMapped;
    }

    private JPanel createMainPanel(String gameLabel) {
        Color mainPanelColour = Color.black;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColour);

        gameStats = createStatsPanel(mainPanelColour);
        gameFuture = createFuturePiecePanel(mainPanelColour);
        topSideFiller = createTopSideFillerPanel(mainPanelColour);
        bottomSideFiller = createBottomSideFillerPanel(mainPanelColour, gameLabel);

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

    private JPanel createBottomSideFillerPanel(Color backgroundColour, String gameLabel) {
        JPanel bottomFillerPanel = new JPanel();
        bottomFillerPanel.setBackground(backgroundColour);
        bottomFillerPanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, (int) (Constants.WINDOW_HEIGHT * Constants.BOTTOM_FILLER_SPACE_HEIGHT_PERCENTAGE)));
        bottomFillerPanel.setLayout(new BoxLayout(bottomFillerPanel, BoxLayout.Y_AXIS));

        bottomFillerPanel.add(new LabelComponent(backgroundColour, gameLabel));

        return bottomFillerPanel;
    }

    private void updateLevelAndScoreLabel(int level, int score) {
        ((ScoreComponent) gameStats.getComponent(0)).setLevelAndScore(level, score);
    }

    private void updateUpcomingPieceLabel(String upcomingPieceName) {
        ((UpcomingPieceComponent) gameFuture.getComponent(0)).setUpcomingPieceName(upcomingPieceName);
    }
}
