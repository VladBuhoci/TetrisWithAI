package edu.vbu.tetris_with_ai.core;

import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.core.shapes.Shapes;
import edu.vbu.tetris_with_ai.ui.GameGrid;
import edu.vbu.tetris_with_ai.ui.GameViewport;
import edu.vbu.tetris_with_ai.utils.Constants;
import edu.vbu.tetris_with_ai.utils.VoidFunctionNoArg;
import edu.vbu.tetris_with_ai.utils.VoidFunctionOneArg;
import edu.vbu.tetris_with_ai.utils.VoidFunctionTwoArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Optional;

/**
 * <pre>
 * Handles the game's logic.
 * A UI wrapper can be used to present the status of a game instance to the user.
 * A wrapper can be used to map user input to game controls.
 * </pre>
 */
public final class TetrisGame {

    private static final Logger LOG = LogManager.getLogger(TetrisGame.class);

    private final int id;

    private long initialDelay;

    private GameGrid gameGrid;
    private GameViewport pairedViewport;

    private VoidFunctionNoArg onGameOverCallback;
    private VoidFunctionOneArg<String> onSpawnPieceCallback;
    private VoidFunctionTwoArgs<Integer, Double> onScoreIncreasedCallback;

    private Shape upcomingPiece;
    private boolean isGameSessionRunning;
    private Thread pieceDescendingThread;
    private double pieceMoveDownTimesPerSecond;
    private double score;
    private int level;
    private int clearedLinesCount;

    public TetrisGame() {
        this(0);
    }

    public TetrisGame(int id) {
        this.id = id;
        this.gameGrid = initGameGrid();
    }

    public void startGame(long initialDelay) {
        startGame(initialDelay, true);
    }

    public void startGame(long initialDelay, boolean startGameLoop) {
        LOG.info("Starting game thread.");

        this.initialDelay = initialDelay;

        // Spawn initial piece.
        spawnNewPiece();

        // Init session vars.
        isGameSessionRunning = true;
        pieceMoveDownTimesPerSecond = 1.0;
        score = 0;
        level = 0;
        clearedLinesCount = 0;

        if (startGameLoop) {
            // Initial delay.
            waitForMillis(initialDelay);

            // Create a thread that brings the current piece down at fixed rates.
            pieceDescendingThread = new Thread(() -> {
                try {
                    while (isGameSessionRunning) {
                        gameLoopSingleCycle();
                        waitForMillis(getWaitTimeForCurrentLevel());
                    }
                } catch (Exception e) {
                    endGame(EndGameReason.ERROR);
                }
            }, "PieceElevatorThread-" + Thread.currentThread().getId());

            pieceDescendingThread.start();
        }
    }

    public enum EndGameReason {
        NORMAL_END,
        ERROR,
        FORCED_BY_TIMEOUT
    }

    public void endGame(EndGameReason reason) {
        if (isGameSessionRunning) {
            LOG.info("Stopping game thread.");

            isGameSessionRunning = false;

            if (pieceDescendingThread != null && pieceDescendingThread.isAlive()) {
                pieceDescendingThread.interrupt();
            }

            if (reason == EndGameReason.ERROR) {
                pairedViewport.setBackground(Color.red);
            } else if (reason == EndGameReason.FORCED_BY_TIMEOUT) {
                pairedViewport.setBackground(Color.orange);
            }

            Optional.ofNullable(onGameOverCallback).ifPresent(VoidFunctionNoArg::call);
        }
    }

    public void markAsTopGame(boolean isTop) {
        if (isTop) {
            pairedViewport.setBackground(Color.green);
        } else {
            pairedViewport.setBackground(Constants.BACKGROUND_COLOUR_LIGHT);
        }
    }

    public void unmark() {
        if (pairedViewport.getBackground() != Color.green) {
            pairedViewport.setBackground(Constants.BACKGROUND_COLOUR_LIGHT);
        }
    }

    public void gameLoopSingleCycle() throws Exception {
        if (gameGrid.isPieceCollidingBottom()) {
            int clearedRows = gameGrid.tryClearCompletedHorizLines();
            if (clearedRows > 0) {
                // If there were any completed (and cleared by now) horizontal lines, raise the score accordingly.
                increaseScoreByCompletedLines(clearedRows);
            }

            try {
                spawnNewPiece();
            } catch (IllegalStateException e) {
                // Failure to apply the new piece's colours in one or more cells means the place is already (partially) occupied by other piece(s).
                // Consider it to be game over.
                endGame(EndGameReason.NORMAL_END);
            }
        } else {
            gameGrid.movePieceDownOneRow();
        }
    }

    public boolean isGameOver() {
        return !isGameSessionRunning;
    }

    public void reset(String newGameLabel) {
        gameGrid = initGameGrid();
        upcomingPiece = null;

        pairedViewport.setTetrisGame(this);
        pairedViewport.resetDisplayedData(newGameLabel);

        unmark();
        startGame(initialDelay, false);
    }

    public void setOnGameOverCallback(VoidFunctionNoArg onGameOverCallback) {
        this.onGameOverCallback = onGameOverCallback;
    }

    public void setOnSpawnPieceCallback(VoidFunctionOneArg<String> onSpawnPieceCallback) {
        this.onSpawnPieceCallback = onSpawnPieceCallback;
    }

    public void setOnScoreIncreasedCallback(VoidFunctionTwoArgs<Integer, Double> onScoreIncreasedCallback) {
        this.onScoreIncreasedCallback = onScoreIncreasedCallback;
    }

    public GameGrid getGameGrid() {
        return gameGrid;
    }

    public double getScore() {
        return score;
    }

    public void setGameGrid(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public int getId() {
        return id;
    }

    public void setPairedViewport(GameViewport gameViewport) {
        this.pairedViewport = gameViewport;
    }

    public void performAction(Action action) {
        if (action == null) {
            return;
        }

        LOG.debug("Performing the following action upon current Tetris game and shape [{}] : {}", gameGrid::getCurrFallPiece, () -> action);

        switch (action) {
            case MOVE_DOWN_ONCE:
                movePieceDownOneRow();
                break;
            case MOVE_LEFT_ONCE:
                movePieceLeftOneColumn();
                break;
            case MOVE_RIGHT_ONCE:
                movePieceRightOneColumn();
                break;
            case ROTATE_LEFT_ONCE:
                rotatePieceLeftOnce();
                break;
            case ROTATE_RIGHT_ONCE:
                rotatePieceRightOnce();
                break;
            case DROP:
                instantDropPiece();
                break;

            default:
                break;
        }
    }

    // Begin delegates.

    public void movePieceDownOneRow() {
        boolean movedOneRow = gameGrid.movePieceDownOneRow();

        increaseScore(movedOneRow ? Constants.SCORE_PER_PIECE_DOWN_MOVE : 0.0);
    }

    public void movePieceLeftOneColumn() {
        gameGrid.movePieceLeftOneColumn();
    }

    public void movePieceRightOneColumn() {
        gameGrid.movePieceRightOneColumn();
    }

    public void rotatePieceLeftOnce() {
        gameGrid.rotatePieceLeftOnce();
    }

    public void rotatePieceRightOnce() {
        gameGrid.rotatePieceRightOnce();
    }

    public void instantDropPiece() {
        int moveCount = gameGrid.instantDropPiece();

        increaseScore(moveCount * Constants.SCORE_PER_PIECE_DOWN_MOVE);
    }

    // ~ end of delegates.

    private GameGrid initGameGrid() {
        return new GameGrid(Constants.CELL_COUNT_HORIZONTALLY, Constants.CELL_COUNT_VERTICALLY, Constants.IS_DOUBLE_BUFFERED);
    }

    private void spawnNewPiece() {
        Shape upcomingPiece = getUpcomingPiece();

        LOG.debug("Spawning next shape: {}", () -> upcomingPiece);

        gameGrid.setCurrentFallingPiece(upcomingPiece);

        Optional.ofNullable(onSpawnPieceCallback).ifPresent(callback -> callback.call(this.upcomingPiece.getName()));
    }

    private Shape getUpcomingPiece() {
        Shape currentUpcomingPiece = upcomingPiece != null ? upcomingPiece : determineNewUpcomingPiece();
        upcomingPiece = determineNewUpcomingPiece();

        return currentUpcomingPiece;
    }

    private Shape determineNewUpcomingPiece() {
        Shape chosenShape = Shapes.getRandomShape();

        LOG.debug("Chosen new random upcoming shape: {}", () -> chosenShape);

        return chosenShape;
    }

    private void increaseScore(double deltaScore) {
        increaseScore0(deltaScore);
    }

    private void increaseScoreByCompletedLines(int clearedLines) {
        double deltaScore = 0.0;

        clearedLinesCount += clearedLines;
        level = clearedLinesCount / Constants.LINES_REQUIRED_FOR_LEVEL_UP;

        switch (clearedLines) {
            case 1:
                deltaScore = 40.0 * (level + 1);
                break;
            case 2:
                deltaScore = 100.0 * (level + 1);
                break;
            case 3:
                deltaScore = 300.0 * (level + 1);
                break;
            case 4:
                deltaScore = 1_200.0 * (level + 1);
                break;
        }

        increaseScore0(deltaScore);
    }

    /**
     * Don't call this directly.
     *
     * @param deltaScore points to add to the game's score.
     */
    private void increaseScore0(double deltaScore) {
        score += deltaScore;

        Optional.ofNullable(onScoreIncreasedCallback).ifPresent(callback -> callback.call(level, score));
    }

    private void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (e.getMessage().equalsIgnoreCase("sleep interrupted")) {
                // Do nothing, thread was interrupted on purpose (game stopped by user).
            } else {
                LOG.warn("An error occurred while trying to wait before moving the piece down again: {}", () -> e);
            }
        }
    }

    private long getWaitTimeForCurrentLevel() {
        return (long) (1.0 / pieceMoveDownTimesPerSecond * 1000.0);
    }
}
