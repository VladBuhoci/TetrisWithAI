package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.ai.AgentsMaster;
import edu.vbu.tetris_with_ai.ai.RandomMoveAgent;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.ui.GameWindow;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisForRandomAI {

    private static final Logger LOG = LogManager.getLogger(TetrisForRandomAI.class);

    public static void main(String[] args) {
        LOG.info("Starting a standalone Tetris game with UI for an AI with random moves...");

        TetrisGame tetrisGame = new TetrisGame();
        GameWindow gameWindow = new GameWindow("Tetris (random moves AI)", tetrisGame, false);

        gameWindow.displayWindow();
        tetrisGame.startGame(Constants.GAME_START_INITIAL_DELAY);

        RandomMoveAgent randomAgent = new RandomMoveAgent(1);
        AgentsMaster agentsMaster = new AgentsMaster();

        agentsMaster.addAgent(tetrisGame, randomAgent);
        agentsMaster.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
