package edu.vbu.tetris_with_ai.utils;

public abstract class MathUtils {

    private MathUtils() {
        // Nothing
    }

    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) {
            return min;
        } else if (val.compareTo(max) > 0) {
            return max;
        } else {
            return val;
        }
    }
}
