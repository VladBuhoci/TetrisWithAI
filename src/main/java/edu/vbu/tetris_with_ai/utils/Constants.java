package edu.vbu.tetris_with_ai.utils;

import java.awt.*;

public abstract class Constants {

    public Constants() {
        // Nothing, class is final too.
    }

    //======================================================================
    //      Core game settings:
    //======================================================================

    public static final int CELL_COUNT_HORIZONTALLY = 10;
    public static final int CELL_COUNT_VERTICALLY = 20;

    public static final int LINES_REQUIRED_FOR_LEVEL_UP = 10;

    public static final long GAME_START_INITIAL_DELAY = 300L;

    //======================================================================
    //      UI settings:
    //======================================================================

    public static final String WINDOW_ICON_PATH = "tetris.png";

    public static final boolean IS_DOUBLE_BUFFERED = true;

    public static final int CELL_SIZE = 1;     // horizontally, as well as vertically, measured in pixels.

    // Always keep the background and empty cells' colour values different!
    public static final Color BACKGROUND_COLOUR = Color.black;
    public static final Color EMPTY_CELL_COLOUR = Color.darkGray;
    public static final Color BACKGROUND_COLOUR_LIGHT = Color.lightGray;

    public static final int  WINDOW_WIDTH = 220 + CELL_COUNT_HORIZONTALLY * CELL_SIZE * 10;
    public static final int WINDOW_HEIGHT = 100 +   CELL_COUNT_VERTICALLY * CELL_SIZE * 10;

    // How much of the total window height should be allocated for each empty area (up and bottom).
    public static final double TOP_FILLER_SPACE_HEIGHT_PERCENTAGE = 0.1;
    public static final double BOTTOM_FILLER_SPACE_HEIGHT_PERCENTAGE = 0.1;

    // How much of the total window width should be allocated for each side area (west/stats panel and east/future pieces panel).
    public static final double WEST_SPACE_WIDTH_PERCENTAGE = 0.2;
    public static final double EAST_SPACE_WIDTH_PERCENTAGE = 0.2;

    //======================================================================
    //      AI settings:
    //======================================================================

    public static final long AI_WAIT_TIME_MILLIS_BEFORE_NEXT_MOVE = 10L;

    // The higher the value (percentage), the higher the chance for a gene of a child agent to mutate.
    public static final float AI_GENES_MUTATION_RATE = 10.0f;

    // Games are forcefully ended after the timeout.
    public static final int AI_GAME_TIME_LIMIT_SECONDS = 10;
}
