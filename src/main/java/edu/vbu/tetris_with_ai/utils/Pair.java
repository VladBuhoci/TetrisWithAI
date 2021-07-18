package edu.vbu.tetris_with_ai.utils;

public final class Pair<LEFT_TYPE, RIGHT_TYPE> {

    private final LEFT_TYPE leftValue;
    private final RIGHT_TYPE rightValue;

    public Pair(LEFT_TYPE leftValue, RIGHT_TYPE rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public LEFT_TYPE getLeftValue() {
        return leftValue;
    }

    public RIGHT_TYPE getRightValue() {
        return rightValue;
    }
}
