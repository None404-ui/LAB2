package functions;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для метода iterator()
 */
public class IteratorTest {

    @Test
    public void testArrayTabulatedFunction_IteratorWithForEach() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        int i = 0;
        for (Point point : function) {
            assertEquals(xValues[i], point.x, 0.001);
            assertEquals(yValues[i], point.y, 0.001);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testArrayTabulatedFunction_IteratorWithWhile() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        Iterator<Point> iterator = function.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[i], point.x, 0.001);
            assertEquals(yValues[i], point.y, 0.001);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testArrayTabulatedFunction_IteratorNoSuchElementException() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        Iterator<Point> iterator = function.iterator();
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> {
            iterator.next();
        });
    }

    @Test
    public void testLinkedListTabulatedFunction_IteratorThrowsException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        
        assertThrows(UnsupportedOperationException.class, () -> {
            function.iterator();
        });
    }
}

