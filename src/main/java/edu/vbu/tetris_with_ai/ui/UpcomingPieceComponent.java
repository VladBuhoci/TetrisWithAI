package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class UpcomingPieceComponent extends JPanel {

    private final JLabel upcomingPieceName;

    public UpcomingPieceComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        upcomingPieceName = new JLabel();
        upcomingPieceName.setForeground(Color.white);
        setUpcomingPieceName(null);

        JLabel nextPieceLabel = new JLabel("Next piece: ");
        nextPieceLabel.setForeground(Color.white);

        add(nextPieceLabel);
        add(upcomingPieceName);
    }

    public void setUpcomingPieceName(String pieceName) {
        upcomingPieceName.setText(pieceName);
    }
}
