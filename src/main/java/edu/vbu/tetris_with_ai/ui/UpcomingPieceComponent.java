package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class UpcomingPieceComponent extends JPanel {

    private final JLabel upcomingPieceName;

    public UpcomingPieceComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        JLabel nextPieceLabel = new JLabel("Next: ");
        nextPieceLabel.setForeground(Color.white);

        upcomingPieceName = new JLabel();
        upcomingPieceName.setForeground(Color.white);

        Font font = new Font(nextPieceLabel.getFont().getName(), Font.PLAIN, 18);

        nextPieceLabel.setFont(font);
        upcomingPieceName.setFont(font);

        setUpcomingPieceName(null);

        add(nextPieceLabel);
        add(upcomingPieceName);
    }

    public void setUpcomingPieceName(String pieceName) {
        upcomingPieceName.setText(pieceName);
    }
}
