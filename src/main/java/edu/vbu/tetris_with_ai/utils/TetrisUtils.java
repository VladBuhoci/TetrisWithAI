package edu.vbu.tetris_with_ai.utils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TetrisUtils {

    private static final AtomicInteger aiAgentIdCounter = new AtomicInteger();

    private TetrisUtils() {
        // Nothing
    }

    public static int getNextAgentID() {
        return aiAgentIdCounter.incrementAndGet();
    }
}
