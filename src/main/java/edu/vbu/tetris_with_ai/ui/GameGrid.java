package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.Position;
import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.core.shapes.Shapes;
import edu.vbu.tetris_with_ai.utils.MathUtils;
import edu.vbu.tetris_with_ai.utils.VoidFunctionNoArg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

import static edu.vbu.tetris_with_ai.utils.Constants.BACKGROUND_COLOUR;
import static edu.vbu.tetris_with_ai.utils.Constants.EMPTY_CELL_COLOUR;

/**
 * <pre>
 * Contains tetris grid content.
 * Handles piece translation, rotation and collision detection.
 * </pre>
 */
public class GameGrid extends JPanel {

    private static final Logger LOG = LogManager.getLogger(GameGrid.class);

    private final JPanel[][] gridCells;
    private final int rowCount;
    private final int columnCount;

//    private final Semaphore semaphore = new Semaphore(1);

    private Shape currFallPiece;
    private int fallingPieceRowIndex, fallingPieceColumnIndex;

    int clearedLinesSinceLastCycle;

    public GameGrid(int cellCountOnX, int cellCountOnY, boolean isDoubleBuffered) {
        super(new GridLayout(cellCountOnY, cellCountOnX, 1, 1), isDoubleBuffered);

        setBackground(BACKGROUND_COLOUR);

        this.gridCells = new JPanel[cellCountOnY][cellCountOnX];
        this.rowCount = cellCountOnY;
        this.columnCount = cellCountOnX;

        resetFallingPiece(null);

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                JPanel cell = new JPanel();
                cell.setBackground(EMPTY_CELL_COLOUR);
                cell.setDoubleBuffered(true);

                gridCells[i][j] = cell;
                add(cell);
            }
        }
    }

    private GameGrid(JPanel[][] gridCells, int cellCountOnX, int cellCountOnY, boolean isDoubleBuffered) {
        super(new GridLayout(cellCountOnY, cellCountOnX, 1, 1), isDoubleBuffered);

        setBackground(BACKGROUND_COLOUR);

        this.gridCells = gridCells;
        this.rowCount = cellCountOnY;
        this.columnCount = cellCountOnX;

        resetFallingPiece(null);

//        for (int i = 0; i < rowCount; i++) {
//            for (int j = 0; j < columnCount; j++) {
//                JPanel cell = new JPanel();
//                cell.setBackground(EMPTY_CELL_COLOUR);
//                cell.setDoubleBuffered(true);
//
//                gridCells[i][j] = cell;
//                add(cell);
//            }
//        }
    }

    /**
     * Spawns the given piece at the top of the grid.
     *
     * @param fallingPiece the new piece to insert into the game grid.
     * @throws IllegalStateException when the place is occupied by other piece(s).
     */
    public void setCurrentFallingPiece(Shape fallingPiece) throws IllegalStateException {
        if (currFallPiece != fallingPiece) {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            LOG.trace("Updating current piece from {} to {}", () -> currFallPiece, () -> fallingPiece);

            if (currFallPiece != null) {
                // Darken the now-abandoned piece (to easily distinguish between static pieces and the falling one + collision detection is partially based on colours!!).
                determineCellColoursForFallingPieceForced(currFallPiece.getColour().darker());
            }

            resetFallingPiece(fallingPiece);
            determineCellColoursForFallingPiece(currFallPiece.getColour());

//            semaphore.release();
        }
    }

    public boolean movePieceDownOneRow() {
        if (currFallPiece != null && !isPieceCollidingBottom()) {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            LOG.trace("Moving current piece [{}] down one row", () -> currFallPiece);

            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceVerticallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());

//            semaphore.release();

            return true;
        }

        return false;
    }

    public void movePieceLeftOneColumn() {
        if (currFallPiece != null && !isPieceTouchingLeftWall() && !isPieceTouchingOtherPieceLeft()) {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            LOG.trace("Moving current piece [{}] left one column", () -> currFallPiece);

            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(-1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());

//            semaphore.release();
        }
    }

    public void movePieceRightOneColumn() {
        if (currFallPiece != null && !isPieceTouchingRightWall() && !isPieceTouchingOtherPieceRight()) {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            LOG.trace("Moving current piece [{}] right one column", () -> currFallPiece);

            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());

//            semaphore.release();
        }
    }

    public void rotatePieceLeftOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateLeft(), () -> currFallPiece.rotateRight());
    }

    public void rotatePieceRightOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateRight(), () -> currFallPiece.rotateLeft());
    }

    public int instantDropPiece() {
        int moveCount = 0;

        while (movePieceDownOneRow()) {
            moveCount++;
        }

        return moveCount;
    }

    public boolean isPieceCollidingBottom() {
        return isPieceTouchingFloor() || isPieceTouchingOtherPieceDown();
    }

    public Shape getCurrFallPiece() {
        return currFallPiece;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getFallingPieceColumnIndex() {
        return fallingPieceColumnIndex;
    }

    public Set<Integer> getCurrentCompleteRowIndices() {
        Set<Integer> completedLineIndices = new HashSet<>(4);

        for (int i = 0; i < rowCount; i++) {
            int emptyCellsOnRow = 0;

            for (int j = 0; j < columnCount; j++) {
                JPanel cell = gridCells[i][j];

                if (cell.getBackground() != EMPTY_CELL_COLOUR) {
                    emptyCellsOnRow++;
                }
            }

            if (emptyCellsOnRow == columnCount) {
                completedLineIndices.add(i);
            }
        }

        return completedLineIndices;
    }

    public JPanel getPanelAtPosition(int row, int column) {
        return gridCells[row][column];
    }

    /**
     * If the player has filled any number of horizontal lines, they will be cleared.
     *
     * @return the number of cleared rows.
     */
    public int tryClearCompletedHorizLines() {
        int completedLines = 0;

        // Clear the cells, if any rows are found to be completed.
        for (Integer completeRowIndex : getCurrentCompleteRowIndices()) {
            Arrays.stream(gridCells[completeRowIndex]).forEach(cell -> cell.setBackground(BACKGROUND_COLOUR));
            completedLines++;
        }

        if (completedLines == 0) {
            return 0;
        }

        // Force the current piece to get blended with the other still pieces, so it's guaranteed that it can also be erased if sitting on a completed line.
        setCurrentFallingPiece(Shapes.getNullPiece());

        // Fill the now-empty cells with the cells above.
        int lookupDelta = 1;    // how many rows to look above to pull cell row down to current index (to avoid dragging down black rows).
        boolean isAtLeastOneEmptyRowFilled = false;

        for (int i = rowCount - 1; i > 0; i--) {
            if (gridCells[i][0].getBackground() != BACKGROUND_COLOUR && !isAtLeastOneEmptyRowFilled) {
                continue;
            }

            if (!isAtLeastOneEmptyRowFilled) {
                int k = i - 1;
                while (gridCells[k][0].getBackground() == BACKGROUND_COLOUR) {
                    lookupDelta++;
                    k--;
                }
            }

            for (int j = 0; j < columnCount; j++) {
                Color colourFromAbove = i - lookupDelta >= 0 ? gridCells[i - lookupDelta][j].getBackground() : EMPTY_CELL_COLOUR;

                gridCells[i][j].setBackground(colourFromAbove);
                isAtLeastOneEmptyRowFilled = true;
            }
        }

        clearedLinesSinceLastCycle = completedLines;

        return completedLines;
    }

    public GameGrid getFutureGridWithCurrentPieceInFinalPosition(Shape pieceToUse, int positionOnRow) {
        GameGrid gridClone = duplicateCurrentGameGrid(pieceToUse, positionOnRow);
        gridClone.instantDropPiece();

        return gridClone;
    }

    private GameGrid duplicateCurrentGameGrid(Shape pieceToUse, int positionOnRow) {
        JPanel[][] gridClone = new JPanel[this.gridCells.length][this.gridCells[0].length];

        for (int i = 0; i < this.gridCells.length; i++) {
            for (int j = 0; j < this.gridCells[i].length; j++) {
                JPanel originalCell = this.gridCells[i][j];

                gridClone[i][j] = new JPanel(originalCell.getLayout(), originalCell.isDoubleBuffered());
                gridClone[i][j].setBackground(originalCell.getBackground());
            }
        }

        GameGrid clone = new GameGrid(gridClone, this.columnCount, this.rowCount, this.isDoubleBuffered());
        clone.currFallPiece = this.currFallPiece;
        clone.determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR, true);     // erase the initial falling piece in the cloned grid
        clone.currFallPiece = pieceToUse;
        clone.fallingPieceRowIndex = this.fallingPieceRowIndex;
        clone.fallingPieceColumnIndex = positionOnRow;

        return clone;
    }

    private void resetFallingPiece(Shape newFallingPiece) {
        this.currFallPiece = newFallingPiece;
        this.fallingPieceRowIndex = 0;  // new piece starts at the top of the grid.
        this.fallingPieceColumnIndex = gridCells[0].length / 2 - 1; // new piece starts in the middle of the horizontal line.
    }

    private void determineCellColoursForFallingPiece(Color newColour) throws IllegalStateException {
        determineCellColoursForFallingPiece(newColour, false);
    }

    private void determineCellColoursForFallingPieceForced(Color newColour) throws IllegalStateException {
        determineCellColoursForFallingPiece(newColour, true);
    }

    private void determineCellColoursForFallingPiece(Color newColour, boolean forceRecolouring) throws IllegalStateException {
        Map<JPanel, Color> previousCellColours = new HashMap<>(4);

        currFallPiece.getOccupiedCellPositions().forEach(pos -> {
            int rowIndex = fallingPieceRowIndex + pos.getPosX();
            int columnIndex = fallingPieceColumnIndex + pos.getPosY();

            // Clamp indices
            rowIndex = MathUtils.clamp(rowIndex, 0, rowCount - 1);
            columnIndex = MathUtils.clamp(columnIndex, 0, columnCount - 1);

            JPanel cell = gridCells[rowIndex][columnIndex];

            if (cell.getBackground() == BACKGROUND_COLOUR) {
                return;
            }

            previousCellColours.put(cell, cell.getBackground());

            if (!forceRecolouring && cell.getBackground() != EMPTY_CELL_COLOUR && newColour != EMPTY_CELL_COLOUR && cell.getBackground() != newColour) {
                previousCellColours.keySet().forEach(prevCellState -> prevCellState.setBackground(previousCellColours.get(prevCellState)));
                throw new IllegalStateException("Tried to recolour an already coloured cell (" + cell.getBackground() + ") at position [" + rowIndex + ", " + columnIndex + "] (current piece: " + currFallPiece + ", colour: " + newColour + ")");
            }

            cell.setBackground(newColour);
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Collisions:
    ////////////////////////////////////////////////////////////////////////////////

    private enum CollisionLocation {
        NONE,
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    private boolean isPieceTouchingFloor() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            if (fallingPieceRowIndex + pos.getPosX() == gridCells.length - 1) {
                return true;
            }
        }

        return false;
    }

    private boolean isPieceTouchingOtherPieceDown() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            int currentPiecePosX = fallingPieceRowIndex + pos.getPosX();
            int currentPiecePosY = fallingPieceColumnIndex + pos.getPosY();

            // Clamp indices
            currentPiecePosX = MathUtils.clamp(currentPiecePosX, 0, rowCount - 1);
            currentPiecePosY = MathUtils.clamp(currentPiecePosY, 0, columnCount - 1);

            if (currentPiecePosX < gridCells.length - 1
                    && gridCells[currentPiecePosX + 1][currentPiecePosY].getBackground() != EMPTY_CELL_COLOUR
                    && gridCells[currentPiecePosX + 1][currentPiecePosY].getBackground() != currFallPiece.getColour()) {
                return true;
            }
        }

        return false;
    }

    private boolean isPieceTouchingOtherPieceLeft() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            int currentPiecePosX = fallingPieceRowIndex + pos.getPosX();
            int currentPiecePosY = fallingPieceColumnIndex + pos.getPosY();

            if (currentPiecePosY > 0
                    && gridCells[currentPiecePosX][currentPiecePosY - 1].getBackground() != EMPTY_CELL_COLOUR
                    && gridCells[currentPiecePosX][currentPiecePosY - 1].getBackground() != currFallPiece.getColour()) {
                return true;
            }
        }

        return false;
    }

    private boolean isPieceTouchingLeftWall() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            if (fallingPieceColumnIndex + pos.getPosY() == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean isPieceTouchingRightWall() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            int currentPiecePosX = fallingPieceRowIndex + pos.getPosX();
            int currentPiecePosY = fallingPieceColumnIndex + pos.getPosY();

            if (currentPiecePosY < gridCells[0].length - 1
                    && gridCells[currentPiecePosX][currentPiecePosY + 1].getBackground() != EMPTY_CELL_COLOUR
                    && gridCells[currentPiecePosX][currentPiecePosY + 1].getBackground() != currFallPiece.getColour()) {
                return true;
            }
        }

        return false;
    }

    private boolean isPieceTouchingOtherPieceRight() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            if (fallingPieceColumnIndex + pos.getPosY() == gridCells[0].length - 1) {
                return true;
            }
        }

        return false;
    }

    private CollisionLocation isPiecePartiallyOutsideGrid() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            if (fallingPieceColumnIndex + pos.getPosY() < 0) {
                return CollisionLocation.LEFT;
            }
            if (fallingPieceColumnIndex + pos.getPosY() > gridCells[0].length - 1) {
                return CollisionLocation.RIGHT;
            }
            if (fallingPieceRowIndex + pos.getPosX() < 0) {
                return CollisionLocation.TOP;
            }
            if (fallingPieceRowIndex + pos.getPosX() > gridCells.length - 1) {
                return CollisionLocation.BOTTOM;
            }
        }

        return CollisionLocation.NONE;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Utils:
    ////////////////////////////////////////////////////////////////////////////////

    private void movePieceVerticallyInternal(int delta) {
        fallingPieceRowIndex += delta;
    }

    private void movePieceHorizontallyInternal(int delta) {
        fallingPieceColumnIndex += delta;
    }

    private void rotatePieceOnceInternal(VoidFunctionNoArg rotationImplementation, VoidFunctionNoArg rollbackImplementation) {
        if (currFallPiece != null) {
//            try {
//                semaphore.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);

            // Test collision with left, right and bottom sides after applying the rotation.
            // If colliding with a wall, forcefully translate the current piece in the opposite direction.

            LOG.trace("Applying rotation to current piece [{}]", () -> currFallPiece);

            rotationImplementation.call();

            boolean isPieceOutside = true;

            do {
                switch (isPiecePartiallyOutsideGrid()) {
                    case TOP:
                        movePieceVerticallyInternal(+1);
                        break;

                    case LEFT:
                        movePieceHorizontallyInternal(+1);
                        break;

                    case RIGHT:
                        movePieceHorizontallyInternal(-1);
                        break;

                    case BOTTOM:
                        movePieceVerticallyInternal(-1);
                        break;

                    case NONE:
                    default:
                        isPieceOutside = false;
                        break;
                }
            } while (isPieceOutside);

//            if (isPieceOverlappingWithOtherPiece()) {
//                rollbackImplementation.call();
//            }

            try {
                determineCellColoursForFallingPiece(currFallPiece.getColour());
            } catch (IllegalStateException e) {
                LOG.debug("Rolling back last rotation for piece [{}] ...", () -> currFallPiece);

                rollbackImplementation.call();

                try {
                    determineCellColoursForFallingPiece(currFallPiece.getColour());
                } catch (IllegalStateException e1) {
                    LOG.warn("Ignoring an exception caused by failed colour shifting for piece {}: {}", () -> currFallPiece, () -> e1);
                }
            } finally {
//                semaphore.release();
            }
        }
    }

    private void assertCurrentPieceNotNull() {
        if (currFallPiece == null) {
            throw new IllegalStateException("Cannot invoke method if there is no current piece in the grid");
        }
    }
}
