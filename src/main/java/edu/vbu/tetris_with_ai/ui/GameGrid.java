package edu.vbu.tetris_with_ai.ui;

import com.sun.istack.internal.Nullable;
import edu.vbu.tetris_with_ai.core.Position;
import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.utils.VoidFunctionNoArg;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGrid extends JPanel {

    private static final Color BACKGROUND_COLOUR = Color.black;
    private static final Color EMPTY_CELL_COLOUR = Color.darkGray;

    private final JPanel[][] gridCells;

    private Shape currFallPiece;
    private int fallingPieceRowIndex, fallingPieceColumnIndex;

    public GameGrid(int cellCountOnX, int cellCountOnY, boolean isDoubleBuffered) {
        super(new GridLayout(cellCountOnY, cellCountOnX, 1, 1), isDoubleBuffered);

        setBackground(BACKGROUND_COLOUR);

        this.gridCells = new JPanel[cellCountOnY][cellCountOnX];

        resetFallingPiece(null);

        for (int i = 0; i < cellCountOnY; i++) {
            for (int j = 0; j < cellCountOnX; j++) {
                JPanel cell = new JPanel();
                cell.setBackground(EMPTY_CELL_COLOUR);

                gridCells[i][j] = cell;
                add(cell);
            }
        }
    }

    /**
     * Spawns the given piece at the top of the grid.
     *
     * @param fallingPiece the new piece to insert into the game grid.
     * @throws IllegalStateException when the place is occupied by other piece(s).
     */
    public void setCurrentFallingPiece(Shape fallingPiece) throws IllegalStateException {
        if (currFallPiece != fallingPiece) {
            if (currFallPiece != null) {
                // Darken the now-abandoned piece (to easily distinguish between static pieces and the falling one + collision detection is partially based on colours!!).
                determineCellColoursForFallingPieceForced(currFallPiece.getColour().darker());
            }

            resetFallingPiece(fallingPiece);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceDownOneRow() {
        if (currFallPiece != null && !isPieceCollidingBottom()) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceVerticallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceLeftOneColumn() {
        if (currFallPiece != null && !isPieceTouchingLeftWall() && !isPieceTouchingOtherPieceLeft()) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(-1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceRightOneColumn() {
        if (currFallPiece != null && !isPieceTouchingRightWall() && !isPieceTouchingOtherPieceRight()) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void rotatePieceLeftOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateLeft(), () -> currFallPiece.rotateRight());
    }

    public void rotatePieceRightOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateRight(), () -> currFallPiece.rotateLeft());
    }

    public boolean isPieceCollidingBottom() {
        return isPieceTouchingFloor() || isPieceTouchingOtherPieceDown();
    }

    private void resetFallingPiece(@Nullable Shape newFallingPiece) {
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
            JPanel cell = gridCells[fallingPieceRowIndex + pos.getPosX()][fallingPieceColumnIndex + pos.getPosY()];
            previousCellColours.put(cell, cell.getBackground());

            if (!forceRecolouring && cell.getBackground() != EMPTY_CELL_COLOUR && newColour != EMPTY_CELL_COLOUR) {
                previousCellColours.keySet().forEach(prevCellState -> prevCellState.setBackground(previousCellColours.get(prevCellState)));
                throw new IllegalStateException("Trying to recolour an already coloured cell");
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

//    private boolean isPieceOverlappingWithOtherPiece() {
//        assertCurrentPieceNotNull();
//
//        ERROR
//
//        return false;
//    }

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
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);

            // Test collision with left, right and bottom sides after applying the rotation.
            // If colliding with a wall, forcefully translate the current piece in the opposite direction.

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
                rollbackImplementation.call();
                determineCellColoursForFallingPiece(currFallPiece.getColour());
            }
        }
    }

    private void assertCurrentPieceNotNull() {
        if (currFallPiece == null) {
            throw new IllegalStateException("Cannot invoke method if there is no current piece in the grid");
        }
    }
}
