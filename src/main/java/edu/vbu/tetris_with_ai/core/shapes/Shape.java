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

    public int getHorizontalLength() {
        List<Position> cellPositions = getOccupiedCellPositions();

        int columnMin = Integer.MAX_VALUE;
        int columnMax = Integer.MIN_VALUE;

        for (Position cellPosition : cellPositions) {
            if (columnMin > cellPosition.getPosY()) {
                columnMin = cellPosition.getPosY();
            }

            if (columnMax < cellPosition.getPosY()) {
                columnMax = cellPosition.getPosY();
            }
        }

        return columnMax - columnMin + 1;
    }

    public Shape duplicate() {
        Shape clone = Shapes.cloneShape(this);
        clone.currentOrientation = this.currentOrientation;

        return clone;
    }

    public int getHorizontalEmptyCellsOffset() {
        List<Position> cellPositions = getOccupiedCellPositions();
        int columnMin = Integer.MAX_VALUE;

        for (Position cellPosition : cellPositions) {
            if (columnMin > cellPosition.getPosY()) {
                columnMin = cellPosition.getPosY();
            }
        }

        return columnMin;
    }

    public abstract void rotateLeft();
    public abstract void rotateRight();

    public abstract List<Position> getOccupiedCellPositions();

    public abstract Color getColour();
}
