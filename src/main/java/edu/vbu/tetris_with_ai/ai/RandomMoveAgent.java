package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.Action;
import edu.vbu.tetris_with_ai.core.TetrisGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class RandomMoveAgent extends Agent {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public RandomMoveAgent(int id) {
        super(id);
    }

    @Override
    protected void determineActions(Queue<Action> actionQueueToFill, int actionAmount, TetrisGame game) {
        final Action[] actions = Action.values();
        final int uniqueActionCount = actions.length;

        getRandomIntsInRange(0, uniqueActionCount).forEach(actionIndex -> actionQueueToFill.offer(actions[actionIndex]));
    }

    @Override
    protected String getName() {
        return "random-AI-" + getId();
    }

    private List<Integer> getRandomIntsInRange(int startInclusive, int endExclusive) {
        List<Integer> integers = new ArrayList<>(endExclusive - startInclusive);

        for (int k = startInclusive; k < endExclusive; k++) {
            integers.add(RANDOM.nextInt(endExclusive) + startInclusive);
        }

        return integers;
    }
}
