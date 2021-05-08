package edu.vbu.tetris_with_ai.utils;

/**
 * Functional interface that requires one parameter and which does not return a value (void).
 */
@FunctionalInterface
public interface VoidFunctionOneArg<ARGT> {

    void call(ARGT arg);
}
