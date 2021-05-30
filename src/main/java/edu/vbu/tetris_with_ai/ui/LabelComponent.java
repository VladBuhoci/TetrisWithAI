package edu.vbu.tetris_with_ai.ui;

import javax.swing.*;
import java.awt.*;

public class LabelComponent extends JPanel {

    private final JLabel label;

    public LabelComponent(Color backgroundColour, String label) {
        setBackground(backgroundColour);
        setLayout(new GridBagLayout());

        this.label = new JLabel(label);
        this.label.setForeground(Color.lightGray);
        this.label.setFont(new Font(this.label.getFont().getName(), Font.PLAIN, 14));

        add(this.label);
    }

    public void setLabel(String newLabel) {
        label.setText(newLabel);
    }
}
