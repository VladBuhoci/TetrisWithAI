package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.Action;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.utils.Constants;
import edu.vbu.tetris_with_ai.utils.VoidFunctionOneArg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AgentsMaster {

    private static final Logger LOG = LogManager.getLogger(AgentsMaster.class);

    private final Map<TetrisGame, Agent> gamesAndAgents;
    private final List<Thread> agentThreads;
    private final ConcurrentMap<Thread, AtomicBoolean> agentsAndRunFlags;

    private AtomicBoolean isRunning;
    private Thread masterThread;
    private Thread gameTimeoutThread;

    private VoidFunctionOneArg<Map<TetrisGame, Agent>> allGamesOverCallback;

    public AgentsMaster() {
        gamesAndAgents = new HashMap<>(10);
        agentThreads = new ArrayList<>(10);
        agentsAndRunFlags = new ConcurrentHashMap<>(10);
        isRunning = new AtomicBoolean(false);
    }

    /**
     * @param tetrisGame
     * @param newAgent   new agent to add to the collection.
     * @throws IllegalStateException if this master instance is already running (and some games are not over yet).
     */
    public void addAgent(TetrisGame tetrisGame, Agent newAgent) throws IllegalStateException {
        if (isRunning.get()) {
            throw new IllegalStateException("Cannot add an agent if the master is already running");
        }

        gamesAndAgents.put(tetrisGame, newAgent);
    }

    /**
     * @throws IllegalStateException if this master instance is already running (and some games are not over yet).
     */
    public void start() throws IllegalStateException {
        if (isRunning.get()) {
            throw new IllegalStateException("Cannot start the master if it is already running");
        }

        isRunning.compareAndSet(false, true);

        masterThread = new Thread(() -> gamesAndAgents.forEach((game, agent) -> {
            Thread agentThread = new Thread(() -> {
                while (agentsAndRunFlags.get(Thread.currentThread()) != null && agentsAndRunFlags.get(Thread.currentThread()).get()) {// (isRunning.get()) {
                    if (!game.isGameOver()) {
                        try {
                            Action nextAction = agent.getNextAction(game);
                            game.performAction(nextAction);
                        } catch (Exception e) {
                            LOG.error("An error occurred while performing an agent's action", e);
                            game.endGame(TetrisGame.EndGameReason.ERROR);
                        }

                        try {
                            game.gameLoopSingleCycle();
                        } catch (Exception e) {
                            LOG.error("An error occurred during an agent game's loop cycle", e);
                            game.endGame(TetrisGame.EndGameReason.ERROR);
                        }
                    } else {
                        LOG.info("Agent [{}] has finished its game", agent::getName);
                        stopSpecificAgent(Thread.currentThread());
                    }

                    if (checkAreAllGamesOver()) {
                        break;
                    }

                    waitForMillis(Constants.AI_WAIT_TIME_MILLIS_BEFORE_NEXT_MOVE);
                }
            }, "Game" + game.getId() + "Agent" + agent.getId() + "Thread");

            agentThreads.add(agentThread);
            agentsAndRunFlags.put(agentThread, new AtomicBoolean(true));
            agentThread.start();
        }));

        masterThread.start();

        gameTimeoutThread = new Thread(() -> {
            waitForMillis(TimeUnit.SECONDS.toMillis(Constants.AI_GAME_TIME_LIMIT_SECONDS));
            forceAllGamesOver();
            checkAreAllGamesOver();
        });

        gameTimeoutThread.start();
    }

    /**
     * @throws IllegalStateException if this master instance is already stopped.
     */
    public void stop() throws IllegalStateException {
        if (!isRunning.get()) {
            throw new IllegalStateException("Cannot stop the master if it is already stopped");
        }

        LOG.info("Stopping agents master thread.");

        if (isRunning.compareAndSet(true, false)) {
            agentThreads.forEach(Thread::interrupt);
            agentThreads.clear();

            agentsAndRunFlags.forEach((thread, runFlag) -> stopSpecificAgent(thread));
            agentsAndRunFlags.clear();

            masterThread.interrupt();
            gameTimeoutThread.interrupt();
        }
    }

    protected void setAllGamesOverCallback(VoidFunctionOneArg<Map<TetrisGame, Agent>> allGamesOverCallback) {
        this.allGamesOverCallback = allGamesOverCallback;
    }

    protected void resetGame(TetrisGame game, String newGameLabel) {
        game.reset(newGameLabel);
    }

    protected void forceAllGamesOver() {
        gamesAndAgents.keySet().stream().filter(game -> !game.isGameOver()).forEach(game -> game.endGame(TetrisGame.EndGameReason.FORCED_BY_TIMEOUT));
    }

    private void stopSpecificAgent(Thread agentThread) {
        agentsAndRunFlags.get(agentThread).compareAndSet(true, false);
    }

    private synchronized boolean checkAreAllGamesOver() {
        if (areAllGamesOver()) {
            if (isRunning.get()) {
                stop();

                if (allGamesOverCallback != null) {
                    allGamesOverCallback.call(gamesAndAgents);
                }
            }

            return true;
        }

        return false;
    }

    private boolean areAllGamesOver() {
        return gamesAndAgents.isEmpty() || gamesAndAgents.keySet().stream().allMatch(TetrisGame::isGameOver);
    }

    protected void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (e.getMessage().equalsIgnoreCase("sleep interrupted")) {
                // Do nothing, thread was interrupted on purpose (agents master stopped by user).
            } else {
                LOG.warn("An error occurred while trying to wait: {}", () -> e);
            }
        }
    }

    public Map<TetrisGame, Agent> getGamesAndAgents() {
        return gamesAndAgents;
    }
}
