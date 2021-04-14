package edu.vbu.tetris_with_ai.core.shapes;

import edu.vbu.tetris_with_ai.core.Orientation;
import edu.vbu.tetris_with_ai.core.Position;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Shape structure (area of 2x2, all cells are always filled):
 * <pre>
 *     +---+---+
 *     | X | X |
 *     +---+---+
 *     | X | X |
 *     +---+---+
 * </pre>
 */
public class Square extends Shape {

    private static final List<Position> occupiedCellPositions = Arrays.asList(
            new Position(0, 0), new Position(0, 1),     // first row
            new Position(1, 0), new Position(1, 1)      // second row
    );

    public Square() {
        super(Orientation.UP);
    }

    @Override
    public void rotateLeft() {
        // Do nothing: the square needs no rotation.
    }

    @Override
    public void rotateRight() {
        // Do nothing: the square needs no rotation.
    }

    @Override
    public List<Position> getOccupiedCellPositions() {
        return occupiedCellPositions;
    }

    @Override
    public Color getColour() {
        return Color.yellow;
    }
}
