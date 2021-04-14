package edu.vbu.tetris_with_ai.ui;

import com.sun.istack.internal.Nullable;
import edu.vbu.tetris_with_ai.core.Position;
import edu.vbu.tetris_with_ai.core.shapes.Shape;
import edu.vbu.tetris_with_ai.utils.VoidFunctionNoArg;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
//                cell.setBackground(Color.getHSBColor(i + j, i - j + 0.1f, i + j + 0.7f));

                gridCells[i][j] = cell;
                add(cell);
            }
        }
    }

    public void setCurrentFallingPiece(Shape fallingPiece) {
        if (this.currFallPiece != fallingPiece) {
            resetFallingPiece(fallingPiece);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceDownOneRow() {
        if (currFallPiece != null && !isPieceTouchingBottom() /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceVerticallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceLeftOneColumn() {
        if (currFallPiece != null && !isPieceTouchingLeftWall() /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(-1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceRightOneColumn() {
        if (currFallPiece != null && !isPieceTouchingRightWall() /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);
            movePieceHorizontallyInternal(+1);
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void rotatePieceLeftOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateLeft());
    }

    public void rotatePieceRightOnce() {
        rotatePieceOnceInternal(() -> currFallPiece.rotateRight());
    }

    private void resetFallingPiece(@Nullable Shape newFallingPiece) {
        this.currFallPiece = newFallingPiece;
        this.fallingPieceRowIndex = 0;  // new piece starts at the top of the grid.
        this.fallingPieceColumnIndex = gridCells[0].length / 2 - 1; // new piece starts in the middle of the horizontal line.
    }

    private void determineCellColoursForFallingPiece(Color newColour) {
        currFallPiece.getOccupiedCellPositions().forEach(pos -> gridCells[fallingPieceRowIndex + pos.getPosX()][fallingPieceColumnIndex + pos.getPosY()].setBackground(newColour));
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Collisions:
    ////////////////////////////////////////////////////////////////////////////////

    private static enum CollisionLocation {
        NONE,
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    private boolean isPieceTouchingBottom() {
        assertCurrentPieceNotNull();

        List<Position> occupiedCellPositions = currFallPiece.getOccupiedCellPositions();

        for (Position pos : occupiedCellPositions) {
            if (fallingPieceRowIndex + pos.getPosX() == gridCells.length - 1) {
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

    // TODO: W.I.P.
    private boolean isPieceOverlappingWithOtherPiece() {
        assertCurrentPieceNotNull();

        return false;
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

    private void rotatePieceOnceInternal(VoidFunctionNoArg rotationImplementation) {
        if (currFallPiece != null /*TODO: and other -physical- conditions (??)*/) {
            determineCellColoursForFallingPiece(EMPTY_CELL_COLOUR);

            if (true) { // TODO: other conditions?
                // Test collision with left, right and bottom sides after applying the rotation.
                // If colliding with a wall or an object, forcefully translate the current piece in the opposite direction.

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

                // TODO: another check for collisions with other pieces.
            }

            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    private void assertCurrentPieceNotNull() {
        if (currFallPiece == null) {
            throw new IllegalStateException("Cannot invoke method if there is no current piece in the grid");
        }
    }
}
