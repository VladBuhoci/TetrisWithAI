package edu.vbu.tetris_with_ai.core;

import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.core.shapes.Shapes;
import edu.vbu.tetris_with_ai.ui.GameGrid;
import edu.vbu.tetris_with_ai.utils.Constants;
import edu.vbu.tetris_with_ai.utils.VoidFunctionNoArg;
import edu.vbu.tetris_with_ai.utils.VoidFunctionOneArg;
import edu.vbu.tetris_with_ai.utils.VoidFunctionTwoArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final GameGrid gameGrid;

    private VoidFunctionNoArg onGameOverCallback;
    private VoidFunctionOneArg<String> onSpawnPieceCallback;
    private VoidFunctionTwoArgs<Integer, Integer> onScoreIncreasedCallback;

    private Shape upcomingPiece;
    private boolean isGameSessionRunning;
    private Thread pieceDescendingThread;
    private double pieceMoveDownTimesPerSecond;
    private int score;
    private int level;
    private int clearedLinesCount;

    public TetrisGame() {
        this.gameGrid = new GameGrid(Constants.CELL_COUNT_HORIZONTALLY, Constants.CELL_COUNT_VERTICALLY, Constants.IS_DOUBLE_BUFFERED);
    }

    public void startGame(long initialDelay) {
        LOG.info("Starting game thread.");

        // Spawn initial piece.
        spawnNewPiece();

        // Init session vars.
        isGameSessionRunning = true;
        pieceMoveDownTimesPerSecond = 1.0;
        score = 0;
        level = 0;
        clearedLinesCount = 0;

        // Initial delay.
        waitForMillis(initialDelay);

        // Create a thread that brings the current piece down at fixed rates.
        pieceDescendingThread = new Thread(() -> {
            while (isGameSessionRunning) {
                if (gameGrid.isPieceCollidingBottom()) {
                    int clearedRows = gameGrid.tryClearCompletedHorizLines();
                    if (clearedRows > 0) {
                        // If there were any completed (and cleared by now) horizontal lines, raise the score accordingly.
                        increaseScore(clearedRows);
                    }

                    try {
                        spawnNewPiece();
                    } catch (IllegalStateException e) {
                        // Failure to apply the new piece's colours in one or more cells means the place is already (partially) occupied by other piece(s).
                        // Consider it to be game over.
                        endGame();
                    }
                } else {
                    gameGrid.movePieceDownOneRow();
                }

                waitForMillis(getWaitTimeForCurrentLevel());
            }
        }, "PieceElevatorThread-" + Thread.currentThread().getId());

        pieceDescendingThread.start();
    }

    public void endGame() {
        if (isGameSessionRunning) {
            LOG.info("Stopping game thread.");

            isGameSessionRunning = false;

            if (pieceDescendingThread != null && pieceDescendingThread.isAlive()) {
                pieceDescendingThread.interrupt();
            }

            Optional.ofNullable(onGameOverCallback).ifPresent(VoidFunctionNoArg::call);
        }
    }

    public boolean isGameOver() {
        return !isGameSessionRunning;
    }

    public void setOnGameOverCallback(VoidFunctionNoArg onGameOverCallback) {
        this.onGameOverCallback = onGameOverCallback;
    }

    public void setOnSpawnPieceCallback(VoidFunctionOneArg<String> onSpawnPieceCallback) {
        this.onSpawnPieceCallback = onSpawnPieceCallback;
    }

    public void setOnScoreIncreasedCallback(VoidFunctionTwoArgs<Integer, Integer> onScoreIncreasedCallback) {
        this.onScoreIncreasedCallback = onScoreIncreasedCallback;
    }

    public GameGrid getGameGrid() {
        return gameGrid;
    }

    public void performAction(Action action) {
        LOG.debug("Performing the following action upon current Tetris game: {}", () -> action);

        switch (action) {
            case MOVE_DOWN_ONCE:    movePieceDownOneRow();      break;
            case MOVE_LEFT_ONCE:    movePieceLeftOneColumn();   break;
            case MOVE_RIGHT_ONCE:   movePieceRightOneColumn();  break;
            case ROTATE_LEFT_ONCE:  rotatePieceLeftOnce();      break;
            case ROTATE_RIGHT_ONCE: rotatePieceRightOnce();     break;
//            case DROP: /* TODO: dropping is not implemented yet. */ break;

            default:
                break;
        }
    }

    // Begin delegates.

    public void movePieceDownOneRow() {
        gameGrid.movePieceDownOneRow();
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

    // ~ end of delegates.

    private void spawnNewPiece() {
        gameGrid.setCurrentFallingPiece(getUpcomingPiece());

        Optional.ofNullable(onSpawnPieceCallback).ifPresent(callback -> callback.call(upcomingPiece.getName()));
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

    private void increaseScore(int clearedLines) {
        int deltaScore = 0;

        clearedLinesCount += clearedLines;
        level = clearedLinesCount / Constants.LINES_REQUIRED_FOR_LEVEL_UP;

        switch (clearedLines) {
            case 1:
                deltaScore = 40 * (level + 1);
                break;
            case 2:
                deltaScore = 100 * (level + 1);
                break;
            case 3:
                deltaScore = 300 * (level + 1);
                break;
            case 4:
                deltaScore = 1200 * (level + 1);
                break;
        }

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
