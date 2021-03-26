package edu.vbu.tetris_with_ai.ui;

import edu.vbu.tetris_with_ai.core.Shape;

import javax.swing.*;
import java.awt.*;

public class GameGrid extends JPanel {

    private final JPanel[][] gridCells;

    private Shape currFallPiece;
    private int fallingPieceRowIndex, fallingPieceColumnIndex;

    public GameGrid(int cellCountOnX, int cellCountOnY, boolean isDoubleBuffered) {
        super(new GridLayout(cellCountOnY, cellCountOnX, 1, 1), isDoubleBuffered);

        setBackground(Color.black);

        this.gridCells = new JPanel[cellCountOnY][cellCountOnX];

        resetFallingPiece(null);

        for (int i = 0; i < cellCountOnY; i++) {
            for (int j = 0; j < cellCountOnX; j++) {
                JPanel cell = new JPanel();
                cell.setBackground(Color.darkGray);
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
        if (currFallPiece != null /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(Color.darkGray);
            fallingPieceRowIndex++;
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceLeftOneColumn() {
        if (currFallPiece != null /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(Color.darkGray);
            fallingPieceColumnIndex--;
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    public void movePieceRightOneColumn() {
        if (currFallPiece != null /*TODO: and other -physical- conditions*/) {
            determineCellColoursForFallingPiece(Color.darkGray);
            fallingPieceColumnIndex++;
            determineCellColoursForFallingPiece(currFallPiece.getColour());
        }
    }

    private void resetFallingPiece(Shape fallingPiece) {
        this.currFallPiece = fallingPiece;
        this.fallingPieceRowIndex = 0;
        this.fallingPieceColumnIndex = gridCells[0].length / 2 - 1;
    }

    private void determineCellColoursForFallingPiece(Color newColour) {
        currFallPiece.getOccupiedCellPositions().forEach(pos -> gridCells[fallingPieceRowIndex + pos.getPosX()][fallingPieceColumnIndex + pos.getPodY()].setBackground(newColour));
    }
}
