package edu.vbu.tetris_with_ai.core.shapes;

import edu.vbu.tetris_with_ai.core.Orientation;
import edu.vbu.tetris_with_ai.core.Position;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Utility class, has no gameplay purpose.
 */
public class Null extends Shape {

    public Null() {
        super("Null", Orientation.UP);
    }

    @Override
    public void rotateLeft() {
        // Do nothing: the Null needs no rotation.
    }

    @Override
    public void rotateRight() {
        // Do nothing: the Null needs no rotation.
    }

    @Override
    public List<Position> getOccupiedCellPositions() {
        return Collections.emptyList();
    }

    @Override
    public Color getColour() {
        return Color.BLACK;
    }
}
