package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class ScoreComponent extends JPanel {

    private final JLabel scoreValue;

    public ScoreComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        scoreValue = new JLabel();
        scoreValue.setForeground(Color.white);
        setScore(0);

        JLabel scoreLabel = new JLabel("Score: ");
        scoreLabel.setForeground(Color.white);

        add(scoreLabel);
        add(scoreValue);
    }

    public void setScore(int newValue) {
        scoreValue.setText(String.valueOf(newValue));
    }
}
