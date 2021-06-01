package edu.vbu.tetris_with_ai.utils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TetrisUtils {

    private static final AtomicInteger aiAgentIdCounter = new AtomicInteger();
    private static final long timeAtAppStart = System.nanoTime();

    private TetrisUtils() {
        // Nothing
    }

    public static int getNextAgentID() {
        return aiAgentIdCounter.incrementAndGet();
    }

    /**
     * Returns elapsed time, in nanoseconds.
     */
    public static long getTimePassedSinceAppStart() {
        return System.nanoTime() - timeAtAppStart;
    }
}
