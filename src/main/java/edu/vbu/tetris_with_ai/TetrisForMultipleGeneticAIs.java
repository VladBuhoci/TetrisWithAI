package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.ai.GeneticAgentsMaster;
import edu.vbu.tetris_with_ai.ai.GeneticAlgoAgent;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.ui.GameCompositeWindow;
import edu.vbu.tetris_with_ai.ui.GameStatsWindow;
import edu.vbu.tetris_with_ai.ui.GameViewport;
import edu.vbu.tetris_with_ai.utils.Constants;
import edu.vbu.tetris_with_ai.utils.TetrisUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisForMultipleGeneticAIs {

    private static final Logger LOG = LogManager.getLogger(TetrisForMultipleGeneticAIs.class);

    public static void main(String[] args) {
        int gamesPerRow = 5, gamesPerColumn = 2, totalGames = gamesPerRow * gamesPerColumn;

        LOG.info("Starting {} standalone Tetris game(s) with UI for {} AI(s) using genetic algorithm...", () -> totalGames, () -> totalGames);

        GameCompositeWindow gameCompositeWindow = new GameCompositeWindow("Tetris (genetic AI)", gamesPerColumn, gamesPerRow);
        GeneticAgentsMaster agentsMaster = new GeneticAgentsMaster();
        GameStatsWindow gameStats = new GameStatsWindow("Stats", agentsMaster);

        for (int k = 0; k < totalGames; k++) {
            int gameID = TetrisUtils.getNextAgentID();

            TetrisGame tetrisGame = new TetrisGame(gameID);
            tetrisGame.startGame(Constants.GAME_START_INITIAL_DELAY, false);

            GameViewport gameViewport = new GameViewport("Tetris for genetic AI #" + gameID, tetrisGame, false);
            gameCompositeWindow.addViewport(gameViewport);

            GeneticAlgoAgent geneticAgent = new GeneticAlgoAgent(gameID);
            agentsMaster.addAgent(tetrisGame, geneticAgent);
        }

        gameCompositeWindow.display();
        gameStats.display();
        agentsMaster.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
