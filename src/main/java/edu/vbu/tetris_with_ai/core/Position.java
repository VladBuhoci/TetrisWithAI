package edu.vbu.tetris_with_ai.core;

public class Position {

    private final int posX;
    private final int podY;

    public Position(int posX, int podY) {
        this.posX = posX;
        this.podY = podY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPodY() {
        return podY;
    }
}
