package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.ai.AgentsMaster;
import edu.vbu.tetris_with_ai.ai.RandomMoveAgent;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.ui.GameCompositeWindow;
import edu.vbu.tetris_with_ai.ui.GameViewport;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisForMultipleRandomAIs {

    private static final Logger LOG = LogManager.getLogger(TetrisForMultipleRandomAIs.class);

    public static void main(String[] args) {
        int gamesPerRow = 5, gamesPerColumn = 2, totalGames = gamesPerRow * gamesPerColumn;

        LOG.info("Starting {} standalone Tetris game(s) with UI for {} AI(s) with random moves...", () -> totalGames, () -> totalGames);

        GameCompositeWindow gameCompositeWindow = new GameCompositeWindow("Tetris (random moves AI)", gamesPerColumn, gamesPerRow);
        AgentsMaster agentsMaster = new AgentsMaster();

        for (int k = 0; k < totalGames; k++) {
            int gameID = k + 1;

            TetrisGame tetrisGame = new TetrisGame();
            tetrisGame.startGame(Constants.GAME_START_INITIAL_DELAY);

            GameViewport gameViewport = new GameViewport("Tetris for random AI #" + gameID, tetrisGame, false);
            gameCompositeWindow.addViewport(gameViewport);

            RandomMoveAgent randomAgent = new RandomMoveAgent(gameID);
            agentsMaster.addAgent(tetrisGame, randomAgent);
        }

        gameCompositeWindow.display();
        agentsMaster.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
