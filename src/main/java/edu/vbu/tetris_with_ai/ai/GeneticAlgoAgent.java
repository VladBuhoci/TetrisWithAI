package edu.vbu.tetris_with_ai.ai;

import edu.vbu.tetris_with_ai.core.Action;
import edu.vbu.tetris_with_ai.core.TetrisGame;
import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.ui.GameGrid;
import edu.vbu.tetris_with_ai.utils.Constants;
import edu.vbu.tetris_with_ai.utils.MathUtils;
import edu.vbu.tetris_with_ai.utils.TetrisUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.Arrays;
import java.util.Queue;

/**
 * <pre>
 * Helpful article: <a href=https://codemyroad.wordpress.com/2013/04/14/tetris-ai-the-near-perfect-player/>click here</a>
 * </pre>
 */
public class GeneticAlgoAgent extends Agent {

    private static final Logger LOG = LogManager.getLogger(GeneticAlgoAgent.class);

    private double weightForHeight;
    private double weightForHoles;
    private double weightForBumpiness;
    private double weightForLineClear;

    public GeneticAlgoAgent(long id) {
        this(id, true);
    }

    private GeneticAlgoAgent(long id, boolean logGeneratedWeights) {
        super(id);

//        weightForHeight = getRandomWeight();
//        weightForHoles = getRandomWeight();
//        weightForBumpiness = getRandomWeight();
//        weightForLineClear = getRandomWeight();

        weightForHeight    = -0.10531331367607177;
        weightForHoles     = -0.16308920919262127;
        weightForBumpiness = +0.46243821049196820;
        weightForLineClear = -0.07588886641364434;

        if (logGeneratedWeights) {
            logWeights();
        }
    }

    @Override
    protected void determineActions(Queue<Action> actionQueueToFill, int actionAmount, TetrisGame game) {
        // Fill action queue based on the agent's prediction.

        double bestFitness = Double.MIN_VALUE;
        int bestRotationCount = -1;             // determines how many rotations (clockwise) need to be applied to the piece.
        int bestPositionOnRow = -1;             // determines how many moves to the left or to the right need to be applied to the piece.

        GameGrid gameGrid = game.getGameGrid();

        Shape pieceClone = gameGrid.getCurrFallPiece().duplicate();
        int initialPositionOnRow = gameGrid.getFallingPieceColumnIndex();

        // Iterate through all rotations (0th to 3rd, since 4th is equivalent to no rotation).
        for (int rotationIter = 0; rotationIter < 4; rotationIter++) {
            // Now iterate through all possible positions on the current row, using the current rotation.
            int possibleSlotsToOccupyOnRow = gameGrid.getColumnCount() - pieceClone.getHorizontalLength() + 1;
            int emptyCellsInPieceSchemaOffset = pieceClone.getHorizontalEmptyCellsOffset();

            for (int positionX = 0; positionX < possibleSlotsToOccupyOnRow; positionX++) {
                GameGrid futureGrid = gameGrid.getFutureGridWithCurrentPieceInFinalPosition(pieceClone, positionX - emptyCellsInPieceSchemaOffset);
                double fitness = getFitness(futureGrid);

//                game.setGameGrid(futureGrid);

                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestRotationCount = rotationIter;
                    bestPositionOnRow = positionX;
                }
            }

            pieceClone.rotateRight();
        }

//        game.setGameGrid(gameGrid);

        // Determine the actions based on the above stats.

        for (int k = 0; k < bestRotationCount; k++) {
            actionQueueToFill.add(Action.ROTATE_RIGHT_ONCE);
        }

        int deltaMovesOnRow = Math.abs(initialPositionOnRow - bestPositionOnRow);
        Action moveAction = initialPositionOnRow < bestPositionOnRow ? Action.MOVE_RIGHT_ONCE : Action.MOVE_LEFT_ONCE;

        for (int k = 0; k < deltaMovesOnRow; k++) {
            actionQueueToFill.add(moveAction);
        }

        actionQueueToFill.add(Action.DROP);
    }

    @Override
    protected String getName() {
        return "genetic-AI-" + getId();
    }

    private double getFitness(GameGrid gameGrid) {
        double score = 0.0;
        int clearedLinesLastCycle = getCurrentCompleteRowIndices(gameGrid);
        int[] gameGridColumnHeights = getGameGridColumnHeights(gameGrid);

        score += weightForHeight * Arrays.stream(gameGridColumnHeights).sum();
        score += weightForHoles * getGameGridHoleCount(gameGrid);
        score += weightForBumpiness * getGameGridHorizontalBumpiness(gameGridColumnHeights);
        score += weightForLineClear * clearedLinesLastCycle;

        return score;
    }

    /**
     * "Mix" this agent with another one to produce a new "child" agent.
     *
     * @param otherParent the other "parent" agent.
     * @return the "child" agent.
     */
    public GeneticAlgoAgent crossOver(GeneticAlgoAgent otherParent, TetrisGame agent1Game, TetrisGame agent2Game) {
        GeneticAlgoAgent child = new GeneticAlgoAgent(TetrisUtils.getNextAgentID(), false);

        // Choose the weights for the child, from this agent or the other one, randomly.
//        child.weightForHeight = RANDOM.nextBoolean() ? this.weightForHeight : otherParent.weightForHeight;
//        child.weightForHoles = RANDOM.nextBoolean() ? this.weightForHoles : otherParent.weightForHoles;
//        child.weightForBumpiness = RANDOM.nextBoolean() ? this.weightForBumpiness : otherParent.weightForBumpiness;
//        child.weightForLineClear = RANDOM.nextBoolean() ? this.weightForLineClear : otherParent.weightForLineClear;

//        double scoreRatio = (double) agent1Game.getScore() / agent2Game.getScore();

        {
//            double weightRatio = child.weightForHeight / otherParent.weightForHeight;

            // 4 : 1 --> 3 & 1

            child.weightForHeight = (this.weightForHeight + otherParent.weightForHeight) / 2.0;
        }
        {
            child.weightForHoles = (this.weightForHoles + otherParent.weightForHoles) / 2.0;
        }
        {
            child.weightForBumpiness = (this.weightForBumpiness + otherParent.weightForBumpiness) / 2.0;
        }
        {
            child.weightForLineClear = (this.weightForLineClear + otherParent.weightForLineClear) / 2.0;
        }

        // Mutate the weights of the child, randomly.
        if (RANDOM.nextDouble() < Constants.AI_GENES_MUTATION_RATE) {
            double factor = RANDOM.nextBoolean() ? 1 : -1;
            child.weightForHeight += getRandomWeight() / 2.0 * factor;
        }
        if (RANDOM.nextDouble() < Constants.AI_GENES_MUTATION_RATE) {
            double factor = RANDOM.nextBoolean() ? 1 : -1;
            child.weightForHoles += getRandomWeight() / 2.0 * factor;
        }
        if (RANDOM.nextDouble() < Constants.AI_GENES_MUTATION_RATE) {
            double factor = RANDOM.nextBoolean() ? 1 : -1;
            child.weightForBumpiness += getRandomWeight() / 2.0 * factor;
        }
        if (RANDOM.nextDouble() < Constants.AI_GENES_MUTATION_RATE) {
            double factor = RANDOM.nextBoolean() ? 1 : -1;
            child.weightForLineClear += getRandomWeight() / 2.0 * factor;
        }

        child.clampWeights();
        child.logWeights();

        return child;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Utils:
    ////////////////////////////////////////////////////////////////////////////////

    private int getCurrentCompleteRowIndices(GameGrid gameGrid) {
        return gameGrid.getCurrentCompleteRowIndices().size();
    }

    private int[] getGameGridColumnHeights(GameGrid gameGrid) {
        int[] heights = new int[gameGrid.getColumnCount()];

        for (int columnIndex = 0; columnIndex < gameGrid.getColumnCount(); columnIndex++) {
            for (int rowIndex = 0; rowIndex < gameGrid.getRowCount(); rowIndex++) {
                if (gameGrid.getPanelAtPosition(rowIndex, columnIndex).getBackground() == Constants.EMPTY_CELL_COLOUR) {
                    heights[columnIndex] -= 1;
                } else {
                    break;
                }
            }

            heights[columnIndex] += gameGrid.getRowCount();
        }

        return heights;
    }

    private int getGameGridHoleCount(GameGrid gameGrid) {
        int count = 0;

        for (int columnIndex = 0; columnIndex < gameGrid.getColumnCount(); columnIndex++) {
            boolean foundTopMostPiece = false;

            for (int rowIndex = 0; rowIndex < gameGrid.getRowCount(); rowIndex++) {
                JPanel pieceAtPosition = gameGrid.getPanelAtPosition(rowIndex, columnIndex);

                if (pieceAtPosition.getBackground() != Constants.EMPTY_CELL_COLOUR) {
                    if (!foundTopMostPiece) {
                        foundTopMostPiece = true;
                    }
                } else {
                    if (foundTopMostPiece) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public int getGameGridHorizontalBumpiness(int[] gridColumnHeights) {
        if (gridColumnHeights.length == 0) {
            return 0;
        } else if (gridColumnHeights.length == 1) {
            return gridColumnHeights[0];
        }

        int bumpiness = 0;

        for (int i = 1; i < gridColumnHeights.length; i++) {
            bumpiness += Math.abs(gridColumnHeights[i - 1] - gridColumnHeights[i]);
        }

        return bumpiness;
    }

    private double getRandomWeight() {
        return RANDOM.nextDouble() * 2.0f - 1.0f;    // map [0, 1] to [-1, 1]
    }

    private void clampWeights() {
        weightForHeight = MathUtils.clamp(weightForHeight, -1.0, 1.0);
        weightForHoles = MathUtils.clamp(weightForHoles, -1.0, 1.0);
        weightForBumpiness = MathUtils.clamp(weightForBumpiness, -1.0, 1.0);
        weightForLineClear = MathUtils.clamp(weightForLineClear, -1.0, 1.0);
    }

    private void logWeights() {
        LOG.debug("Weights of agent [id = {}] for: height = {} | holes = {} | bumpiness = {} | lines = {}"
                , this::getId, () -> weightForHeight, () -> weightForHoles, () -> weightForBumpiness, () -> weightForLineClear);
    }

    public double getWeightForHeight() {
        return weightForHeight;
    }

    public double getWeightForHoles() {
        return weightForHoles;
    }

    public double getWeightForBumpiness() {
        return weightForBumpiness;
    }

    public double getWeightForLineClear() {
        return weightForLineClear;
    }
}
