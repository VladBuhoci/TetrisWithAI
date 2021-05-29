import edu.vbu.tetris_with_ai.core.shapes.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ShapeTest {

    @Test
    public void testLineShapeGetHorizontalLength() {
        Shape line = new Line();

        Assert.assertEquals("The length is not right", 4, line.getHorizontalLength());

        line.rotateLeft();

        Assert.assertEquals("The length is not right", 1, line.getHorizontalLength());

        line.rotateRight();
        line.rotateRight();

        Assert.assertEquals("The length is not right", 1, line.getHorizontalLength());

        line.rotateRight();

        Assert.assertEquals("The length is not right", 4, line.getHorizontalLength());
    }

    @Test
    public void testTFormShapeGetHorizontalLength() {
        Shape tForm = new TForm();

        Assert.assertEquals("The length is not right", 3, tForm.getHorizontalLength());

        tForm.rotateLeft();

        Assert.assertEquals("The length is not right", 2, tForm.getHorizontalLength());

        tForm.rotateRight();
        tForm.rotateRight();

        Assert.assertEquals("The length is not right", 2, tForm.getHorizontalLength());

        tForm.rotateRight();

        Assert.assertEquals("The length is not right", 3, tForm.getHorizontalLength());
    }

    @Test
    public void testZigzagShapeGetHorizontalLength() {
        Shape zigzag = new Zigzag();

        Assert.assertEquals("The length is not right", 3, zigzag.getHorizontalLength());

        zigzag.rotateLeft();

        Assert.assertEquals("The length is not right", 2, zigzag.getHorizontalLength());

        zigzag.rotateRight();
        zigzag.rotateRight();

        Assert.assertEquals("The length is not right", 2, zigzag.getHorizontalLength());

        zigzag.rotateRight();

        Assert.assertEquals("The length is not right", 3, zigzag.getHorizontalLength());
    }

    @Test
    public void testLFormShapeGetHorizontalLength() {
        Shape lForm = new LForm();

        Assert.assertEquals("The length is not right", 3, lForm.getHorizontalLength());

        lForm.rotateLeft();

        Assert.assertEquals("The length is not right", 2, lForm.getHorizontalLength());

        lForm.rotateRight();
        lForm.rotateRight();

        Assert.assertEquals("The length is not right", 2, lForm.getHorizontalLength());

        lForm.rotateRight();

        Assert.assertEquals("The length is not right", 3, lForm.getHorizontalLength());
    }
}
