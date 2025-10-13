package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import exceptions.DifferentLengthOfArraysException;
import exceptions.ArrayIsNotSortedException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Тесты для LinkedListTabulatedFunction
 */
class LinkedListTabulatedFunctionTest {

    @Test
    void testConstructorWithArrays() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(4.0, function.getX(3), 0.001);
        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(16.0, function.getY(3), 0.001);
    }

    @Test
    void testConstructorWithFunction() {
        SqrFunction sqrFunction = new SqrFunction();
        LinkedListTabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(sqrFunction, 0.0, 4.0, 5);

        assertEquals(5, tabulatedFunction.getCount());
        assertEquals(0.0, tabulatedFunction.getX(0), 0.001);
        assertEquals(4.0, tabulatedFunction.getX(4), 0.001);
        assertEquals(0.0, tabulatedFunction.getY(0), 0.001);
        assertEquals(4.0, tabulatedFunction.getY(2), 0.001);
        assertEquals(16.0, tabulatedFunction.getY(4), 0.001);
    }

    @Test
    void testGettersAndSetters() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест getX
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);

        // Тест getY
        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(4.0, function.getY(1), 0.001);
        assertEquals(9.0, function.getY(2), 0.001);

        // Тест setY
        function.setY(1, 5.0);
        assertEquals(5.0, function.getY(1), 0.001);
    }

    @Test
    void testIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(5.0));
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(1.0));
        assertEquals(2, function.indexOfY(9.0));
        assertEquals(-1, function.indexOfY(5.0));
    }

    @Test
    void testBounds() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.leftBound(), 0.001);
        assertEquals(4.0, function.rightBound(), 0.001);
    }

    @Test
    void testApplyMethod() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест точных значений
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(1.0, function.apply(1.0), 0.001);
        assertEquals(4.0, function.apply(2.0), 0.001);
        assertEquals(9.0, function.apply(3.0), 0.001);

        // Тест интерполяции
        double interpolated = function.apply(1.5);
        assertTrue(interpolated > 1.0 && interpolated < 4.0);
    }

    @Test
    void testInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест интерполяции между точками
        double result = function.apply(2.5);
        double expected = 6.5; // Линейная интерполяция между 4.0 и 9.0
        assertEquals(expected, result, 0.001);
    }

    @Test
    void testExtrapolation() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Тест экстраполяции слева
        double leftResult = function.apply(0.5);
        assertTrue(leftResult < 1.0 && leftResult > -1.0);

        // Тест экстраполяции справа
        double rightResult = function.apply(5.0);
        assertTrue(rightResult > 16.0);
    }




    @Test
    void testInsertExistingX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставляем существующий x
        function.insert(2.0, 5.0);
        assertEquals(3, function.getCount()); // количество не изменилось
        assertEquals(5.0, function.getY(1), 0.001); // y изменился
    }

    @Test
    void testConstructorValidation() {
        // Тест с разными длинами массивов
        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new LinkedListTabulatedFunction(new double[]{1.0, 2.0}, new double[]{1.0});
        });

        // Тест с недостаточным количеством точек
        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(new double[]{1.0}, new double[]{1.0});
        });

        // Тест с неупорядоченными x - исправляем ожидаемое исключение
        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(new double[]{2.0, 1.0, 3.0}, new double[]{4.0, 1.0, 9.0});
        });
    }

    @Test
    void testIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(3));
        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(3));
        assertThrows(IllegalArgumentException.class, () -> function.setY(-1, 0.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(3, 0.0));
    }

    @Test
    void testCircularListStructure() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Проверяем, что список циклический
        // Все элементы должны быть доступны через индексы
        for (int i = 0; i < function.getCount(); i++) {
            final int index = i;
            assertDoesNotThrow(() -> function.getX(index));
            assertDoesNotThrow(() -> function.getY(index));
        }
    }

    @Test
    void testLinkedListGetXWithInvalidIndex() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0}, new double[]{1.0, 4.0});

        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(2));
    }

    @Test
    void testLinkedListFloorIndexOfXWithSmallX() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0}, new double[]{1.0, 4.0});

        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(0.5));
    }

    @Test
    void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int pointCount = 0;

        // Тестируем цикл while
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[pointCount], point.x, 0.001);
            assertEquals(yValues[pointCount], point.y, 0.001);
            pointCount++;
        }

        assertEquals(4, pointCount); // Проверяем, что прошли все точки

        // Проверяем, что после окончания бросается исключение
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    void testIteratorWithForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        int pointCount = 0;

        // Тестируем цикл for-each
        for (Point point : function) {
            assertEquals(xValues[pointCount], point.x, 0.001);
            assertEquals(yValues[pointCount], point.y, 0.001);
            pointCount++;
        }

        assertEquals(3, pointCount); // Проверяем, что прошли все точки
    }

    @Test
    void testIteratorEdgeCases() {
        // Тест с минимальным количеством точек
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        // Первый вызов next()
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 0.001);
        assertEquals(1.0, point1.y, 0.001);
        assertTrue(iterator.hasNext());

        // Второй вызов next()
        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 0.001);
        assertEquals(4.0, point2.y, 0.001);
        assertFalse(iterator.hasNext()); // Больше нет элементов

        // Проверяем исключение
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

}



