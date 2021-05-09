package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.Action;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class Agent {

    private static final Logger LOG = LogManager.getLogger(Agent.class);

    private final int id;
    private final Queue<Action> actions;

    public Agent(int id) {
        this.id = id;
        this.actions = new ArrayDeque<>(4);
    }

    public Action getNextAction(TetrisGame gameToPlay) {
        if (actions.isEmpty()) {
            determineActions(actions, 4, gameToPlay);
        }

        LOG.debug("Agent [{}] has current action queue: {}", this::getName, () -> actions);

        return actions.poll();
    }

    protected int getId() {
        return id;
    }

    protected abstract void determineActions(Queue<Action> actionQueueToFill, int actionAmount, TetrisGame game);
    protected abstract String getName();
}
