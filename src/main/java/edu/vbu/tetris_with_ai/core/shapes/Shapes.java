package edu.vbu.tetris_with_ai.core.shapes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public abstract class Shapes {

    private static final Logger LOG = LogManager.getLogger(Shapes.class);
    private static final Class<? extends Shape>[] ALL_SHAPES = new Class[] { LForm.class, LFormMirror.class, Line.class, Square.class, TForm.class, Zigzag.class, ZigzagMirror.class };
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final Null nullPiece = new Null();

    private Shapes() {
        // nothing
    }

    public static Shape getRandomShape() {
        int randIndex = RANDOM.nextInt(ALL_SHAPES.length);
        Class<? extends Shape> chosenType = ALL_SHAPES[randIndex];

        try {
            return chosenType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("Failed to obtain a new instance of type {} due to error: {}", chosenType::getName, () -> e);
        }

        return null;
    }

    public static Shape cloneShape(Shape original) {
        Class<? extends Shape> shapeType = original.getClass();

        try {
            return shapeType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("Failed to clone a shape of type {} due to error: {}", shapeType::getName, () -> e);
        }

        return null;
    }

    public static Null getNullPiece() {
        return nullPiece;
    }
}
