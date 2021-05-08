package edu.vbu.tetris_with_ai.utils;

/**
 * Functional interface that requires two parameters and which does not return a value (void).
 */
@FunctionalInterface
public interface VoidFunctionTwoArgs<ARG1T, ARG2T> {

    void call(ARG1T arg1, ARG2T arg2);
}
