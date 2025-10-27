package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import concurrent.SynchronizedTabulatedFunction;
import java.util.Iterator;



class SynchronizedTabulatedFunctionTest {

    @Test
    void testBasicOperations() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(3, syncFunc.getCount());
        assertEquals(1.0, syncFunc.getX(0));
        assertEquals(10.0, syncFunc.getY(0));
        assertEquals(1.0, syncFunc.leftBound());
        assertEquals(3.0, syncFunc.rightBound());
    }

    @Test
    void testSetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        syncFunc.setY(1, 25.0);
        assertEquals(25.0, syncFunc.getY(1));
    }

    @Test
    void testApply() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(15.0, syncFunc.apply(1.5), 0.001); // интерполяция
        assertEquals(10.0, syncFunc.apply(1.0));
    }

    @Test
    void testIndexOf() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(1, syncFunc.indexOfX(2.0));
        assertEquals(0, syncFunc.indexOfY(10.0));
        assertEquals(-1, syncFunc.indexOfX(5.0));
    }

    @Test
    void testIterator() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Тестируем итератор
        Iterator<Point> iterator = syncFunc.iterator();

        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x);
        assertEquals(10.0, point1.y);

        assertTrue(iterator.hasNext());
        Point point2 = iterator.next();
        assertEquals(2.0, point2.x);
        assertEquals(20.0, point2.y);

        assertTrue(iterator.hasNext());
        Point point3 = iterator.next();
        assertEquals(3.0, point3.x);
        assertEquals(30.0, point3.y);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithModification() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Получаем итератор
        Iterator<Point> iterator = syncFunc.iterator();

        // Меняем данные в функции
        syncFunc.setY(1, 25.0);

        // Итератор должен работать с исходными данными (копией)
        // и не бросать ConcurrentModificationException
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(3, count);
    }
}