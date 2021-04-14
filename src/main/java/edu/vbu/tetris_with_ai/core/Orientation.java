package edu.vbu.tetris_with_ai.core;

public enum Orientation {

    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);

    private final int value;

    Orientation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Orientation previous() {
        int val = value == values()[0].getValue() ? values()[values().length - 1].getValue() : (value - 1);
        return getOrientationWithValue(val);
    }

    public Orientation next() {
        int val = value == values()[values().length - 1].getValue() ? values()[0].getValue() : (value + 1);
        return getOrientationWithValue(val);
    }

    private Orientation getOrientationWithValue(int val) {
        for (Orientation ori : values()) {
            if (ori.getValue() == val) {
                return ori;
            }
        }

        return null;
    }
}
