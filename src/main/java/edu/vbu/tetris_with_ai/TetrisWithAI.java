package edu.vbu.tetris_with_ai;

import edu.vbu.tetris_with_ai.ui.GameWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TetrisWithAI {

    private static final Logger LOG = LogManager.getLogger(TetrisWithAI.class);

    public static void main(String[] args) {
        LOG.info("Starting...");

        GameWindow gameWindow = new GameWindow();
        gameWindow.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Exiting...")));
    }
}
