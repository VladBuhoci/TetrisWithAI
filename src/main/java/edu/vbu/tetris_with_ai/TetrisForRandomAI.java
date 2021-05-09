package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.ai.AgentsMaster;
import edu.vbu.tetris_with_ai.ai.RandomMoveAgent;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.ui.GameCompositeWindow;
import edu.vbu.tetris_with_ai.ui.GameViewport;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisForRandomAI {

    private static final Logger LOG = LogManager.getLogger(TetrisForRandomAI.class);

    public static void main(String[] args) {
        LOG.info("Starting a standalone Tetris game with UI for an AI with random moves...");

        TetrisGame tetrisGame = new TetrisGame();
        GameViewport gameViewport = new GameViewport("Tetris for random AI #1", tetrisGame, false);
        GameCompositeWindow gameCompositeWindow = new GameCompositeWindow("Tetris (random moves AI)", 1, 1);

        gameCompositeWindow.addViewport(gameViewport);
        gameCompositeWindow.display();

        tetrisGame.startGame(Constants.GAME_START_INITIAL_DELAY);

        RandomMoveAgent randomAgent = new RandomMoveAgent(1);
        AgentsMaster agentsMaster = new AgentsMaster();

        agentsMaster.addAgent(tetrisGame, randomAgent);
        agentsMaster.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
