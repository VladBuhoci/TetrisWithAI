package edu.vbu.tetris_with_ai.core;

import java.awt.*;
import java.util.List;

public interface Shape {

    void rotate();

    List<Position> getOccupiedCellPositions();

    Color getColour();
}
