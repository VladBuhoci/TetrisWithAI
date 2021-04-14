package edu.vbu.tetris_with_ai.core.shapes;

import edu.vbu.tetris_with_ai.core.Orientation;
import edu.vbu.tetris_with_ai.core.Position;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shape structure (area of 3x3, has 4 different orientations):
 * <pre>
 *     +---+---+---+    +---+---+---+    +---+---+---+    +---+---+---+
 *     | X | X |   |    |   |   | X |    |   |   |   |    |   | X |   |
 *     +---+---+---+    +---+---+---+    +---+---+---+    +---+---+---+
 *     |   | X | X |    |   | X | X |    | X | X |   |    | X | X |   |
 *     +---+---+---+    +---+---+---+    +---+---+---+    +---+---+---+
 *     |   |   |   |    |   | X |   |    |   | X | X |    | X |   |   |
 *     +---+---+---+    +---+---+---+    +---+---+---+    +---+---+---+
 * </pre>
 */
public class Zigzag extends Shape {

    private static final List<Position> occupiedCellPositionsUpOrient = Arrays.asList(
            new Position(0, 0), new Position(0, 1),  // first row
            new Position(1, 1), new Position(1, 2)   // second row
                                                                            // third row
    );
    private static final List<Position> occupiedCellPositionsRightOrient = Arrays.asList(
            new Position(0, 2),                                  // first row
            new Position(1, 1), new Position(1, 2),  // second row
            new Position(2, 1)                                   // third row
    );
    private static final List<Position> occupiedCellPositionsDownOrient = Arrays.asList(
                                                                            // first row
            new Position(1, 0), new Position(1, 1), // second row
            new Position(2, 1), new Position(2, 2)  // third row
    );
    private static final List<Position> occupiedCellPositionsLeftOrient = Arrays.asList(
            new Position(0, 1),                                  // first row
            new Position(1, 0), new Position(1, 1),  // second row
            new Position(2, 0)                                   // third row
    );

    private final Map<Orientation, List<Position>> occupiedCellPositions = new HashMap<>(4);

    public Zigzag(Orientation initialOrientation) {
        super(initialOrientation);

        occupiedCellPositions.put(Orientation.UP, occupiedCellPositionsUpOrient);
        occupiedCellPositions.put(Orientation.RIGHT, occupiedCellPositionsRightOrient);
        occupiedCellPositions.put(Orientation.DOWN, occupiedCellPositionsDownOrient);
        occupiedCellPositions.put(Orientation.LEFT, occupiedCellPositionsLeftOrient);
    }

    @Override
    public void rotateLeft() {
        currentOrientation = currentOrientation.previous();
    }

    @Override
    public void rotateRight() {
        currentOrientation = currentOrientation.next();
    }

    @Override
    public List<Position> getOccupiedCellPositions() {
        return occupiedCellPositions.get(currentOrientation);
    }

    @Override
    public Color getColour() {
        return Color.red;
    }
}
