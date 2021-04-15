package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class ScoreComponent extends JPanel {

    private final JLabel scoreValue;

    public ScoreComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        JLabel scoreLabel = new JLabel("Score: ");
        scoreLabel.setForeground(Color.white);

        scoreValue = new JLabel();
        scoreValue.setForeground(Color.white);

        Font font = new Font(scoreLabel.getFont().getName(), Font.PLAIN, 20);

        scoreLabel.setFont(font);
        scoreValue.setFont(font);

        setScore(0);

        add(scoreLabel);
        add(scoreValue);
    }

    public void setScore(int newValue) {
        scoreValue.setText(String.valueOf(newValue));
    }
}
