package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.ui.GameCompositeWindow;
import edu.vbu.tetris_with_ai.ui.GameViewport;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisForHumanPlayer {

    private static final Logger LOG = LogManager.getLogger(TetrisForHumanPlayer.class);

    public static void main(String[] args) {
        LOG.info("Starting a standalone Tetris game with UI for a human player...");

        TetrisGame tetrisGame = new TetrisGame();
        GameViewport gameViewport = new GameViewport("Tetris for a human", tetrisGame, true);
        GameCompositeWindow gameCompositeWindow = new GameCompositeWindow("Tetris", 1, 1);

        gameCompositeWindow.addViewport(gameViewport);
        gameCompositeWindow.display();

        tetrisGame.startGame(Constants.GAME_START_INITIAL_DELAY);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
