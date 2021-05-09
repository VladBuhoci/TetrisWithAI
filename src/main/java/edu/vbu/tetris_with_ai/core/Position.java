package edu.vbu.tetris_with_ai.core;

public final class Position {

    private final int posX;
    private final int posY;

    public Position(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
