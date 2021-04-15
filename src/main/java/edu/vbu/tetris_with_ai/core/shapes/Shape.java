package edu.vbu.tetris_with_ai.core.shapes;

import edu.vbu.tetris_with_ai.core.Orientation;
import edu.vbu.tetris_with_ai.core.Position;

import java.awt.*;
import java.util.List;

public abstract class Shape {

    protected String name;
    protected Orientation currentOrientation;

    protected Shape(String name, Orientation initialOrientation) {
        this.name = name;
        this.currentOrientation = initialOrientation;
    }

    public String getName() {
        return name;
    }

    public abstract void rotateLeft();
    public abstract void rotateRight();

    public abstract List<Position> getOccupiedCellPositions();

    public abstract Color getColour();
}
