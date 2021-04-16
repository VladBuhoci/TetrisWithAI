package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class ScoreComponent extends JPanel {

    private final JLabel levelValue;
    private final JLabel scoreValue;

    public ScoreComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        JLabel levelLabel = new JLabel("Level: ");
        levelLabel.setForeground(Color.white);

        JLabel scoreLabel = new JLabel("Score: ");
        scoreLabel.setForeground(Color.white);

        levelValue = new JLabel();
        levelValue.setForeground(Color.white);

        scoreValue = new JLabel();
        scoreValue.setForeground(Color.white);

        Font font = new Font(scoreLabel.getFont().getName(), Font.PLAIN, 20);

        levelLabel.setFont(font);
        levelValue.setFont(font);

        scoreLabel.setFont(font);
        scoreValue.setFont(font);

        setLevelAndScore(0, 0);

        JPanel verticalPanel = new JPanel();
        verticalPanel.setBackground(backgroundColour);

        BoxLayout verticalLayout = new BoxLayout(verticalPanel, BoxLayout.Y_AXIS);

        verticalPanel.setLayout(verticalLayout);

        JPanel levelHorizPanel = new JPanel(new GridBagLayout());
        levelHorizPanel.setBackground(backgroundColour);

        JPanel scoreHorizPanel = new JPanel(new GridBagLayout());
        scoreHorizPanel.setBackground(backgroundColour);

        levelHorizPanel.add(levelLabel);
        levelHorizPanel.add(levelValue);

        scoreHorizPanel.add(scoreLabel);
        scoreHorizPanel.add(scoreValue);

        verticalPanel.add(levelHorizPanel);
        verticalPanel.add(scoreHorizPanel);

        add(verticalPanel);
    }

    public void setLevelAndScore(int level, int score) {
        levelValue.setText(String.valueOf(level));
        scoreValue.setText(String.valueOf(score));
    }
}
