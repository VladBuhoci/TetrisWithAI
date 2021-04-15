package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class StatusComponent extends JPanel {

    private final JLabel status;

    public StatusComponent(Color backgroundColour) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        status = new JLabel();
        status.setForeground(Color.white);
        status.setFont(new Font(status.getFont().getName(), Font.PLAIN, 40));

        setStatusGameOver(true);

        add(status);
    }

    public void setStatusGameOver(boolean isGameOver) {
        // empty during game session, "Game Over" when the player lost.
        setStatus(isGameOver ? "Game Over" : "");
    }

    private void setStatus(String newStatus) {
        status.setText(newStatus);
    }
}
