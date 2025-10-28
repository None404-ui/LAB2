package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import concurrent.SynchronizedTabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import functions.Point;
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

    @Test
    void testDoSynchronouslyWithIntegerResult() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        Integer result = syncFunc.doSynchronously(new SynchronizedTabulatedFunction.Operation<Integer>() {
            @Override
            public Integer apply(SynchronizedTabulatedFunction function) {
                return function.getCount();
            }
        });

        assertEquals(3, result);
    }

    @Test
    void testDoSynchronouslyWithDoubleResult() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        Double result = syncFunc.doSynchronously(function -> {
            function.setY(0, 15.0);
            return function.getY(0);
        });

        assertEquals(15.0, result);
    }

    @Test
    void testDoSynchronouslyWithVoidResult() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        Void result = syncFunc.doSynchronously(function -> {
            function.setY(1, 25.0);
            function.setY(2, 35.0);
            return null;
        });

        assertNull(result);
        assertEquals(25.0, syncFunc.getY(1));
        assertEquals(35.0, syncFunc.getY(2));
    }

    @Test
    void testDoSynchronouslyWithStringResult() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        String result = syncFunc.doSynchronously(function -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < function.getCount(); i++) {
                sb.append("(").append(function.getX(i)).append(",").append(function.getY(i)).append(") ");
            }
            return sb.toString().trim();
        });

        assertEquals("(1.0,10.0) (2.0,20.0) (3.0,30.0)", result);
    }

    @Test
    void testDoSynchronouslyMultipleOperations() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Выполняем несколько операций атомарно
        Double sum = syncFunc.doSynchronously(function -> {
            double total = 0.0;
            for (int i = 0; i < function.getCount(); i++) {
                total += function.getY(i);
                function.setY(i, function.getY(i) * 2); // удваиваем каждое значение
            }
            return total;
        });

        assertEquals(60.0, sum); // 10 + 20 + 30
        assertEquals(20.0, syncFunc.getY(0)); // 10 * 2
        assertEquals(40.0, syncFunc.getY(1)); // 20 * 2
        assertEquals(60.0, syncFunc.getY(2)); // 30 * 2
    }
}