package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.Action;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class AgentsMaster {

    private static final Logger LOG = LogManager.getLogger(AgentsMaster.class);

    private final Map<TetrisGame, Agent> gamesAndAgents;

    private boolean isRunning;
    private Thread masterThread;

    public AgentsMaster() {
        gamesAndAgents = new HashMap<>(10);
        isRunning = false;
    }

    /**
     * @param tetrisGame
     * @param newAgent   new agent to add to the collection.
     * @throws IllegalStateException if this master instance is already running.
     */
    public void addAgent(TetrisGame tetrisGame, Agent newAgent) throws IllegalStateException {
        if (isRunning) {
            throw new IllegalStateException("Cannot add an agent if the master is already running");
        }

        gamesAndAgents.put(tetrisGame, newAgent);
    }

    /**
     * @throws IllegalStateException if this master instance is already running.
     */
    public void start() throws IllegalStateException {
        if (isRunning) {
            throw new IllegalStateException("Cannot start the master if it is already running");
        }

        isRunning = true;

        masterThread = new Thread(() -> gamesAndAgents.forEach((game, agent) -> new Thread(() -> {
            while (isRunning) {
                if (!game.isGameOver()) {
                    Action nextAction = agent.getNextAction(game);
                    game.performAction(nextAction);
                } else {
                    LOG.info("Agent [{}] has finished its game, removing it from master's pool..", agent::getName);
                    gamesAndAgents.remove(game);
                    break;
                }

                try {
                    game.gameLoopSingleCycle();
                } catch (Exception e) {
                    LOG.error("An error occurred during an agent game's loop cycle", e);
                    game.endGame(true);
                    gamesAndAgents.remove(game);
                    break;
                }

                waitForMillis(Constants.AI_WAIT_TIME_MILLIS_BEFORE_NEXT_MOVE);
            }
        }).start()));

        masterThread.start();
    }

    /**
     * @throws IllegalStateException if this master instance is already stopped.
     */
    public void stop() throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Cannot stop the master if it is already stopped");
        }

        LOG.info("Stopping agents master thread.");

        isRunning = false;

        if (masterThread != null && masterThread.isAlive()) {
            masterThread.interrupt();
        }
    }

    private void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (e.getMessage().equalsIgnoreCase("sleep interrupted")) {
                // Do nothing, thread was interrupted on purpose (agents master stopped by user).
            } else {
                LOG.warn("An error occurred while trying to wait before having the agents execute a move again: {}", () -> e);
            }
        }
    }
}
