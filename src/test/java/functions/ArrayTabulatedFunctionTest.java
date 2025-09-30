package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ArrayTabulatedFunction
 */
class ArrayTabulatedFunctionTest {

    @Test
    void testConstructorWithArrays() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(4.0, function.getX(3), 0.001);
        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(16.0, function.getY(3), 0.001);
    }

    @Test
    void testConstructorWithFunction() {
        SqrFunction sqrFunction = new SqrFunction();
        ArrayTabulatedFunction tabulatedFunction = new ArrayTabulatedFunction(sqrFunction, 0.0, 4.0, 5);

        assertEquals(5, tabulatedFunction.getCount());
        assertEquals(0.0, tabulatedFunction.getX(0), 0.001);
        assertEquals(4.0, tabulatedFunction.getX(4), 0.001);
        assertEquals(0.0, tabulatedFunction.getY(0), 0.001);
        assertEquals(4.0, tabulatedFunction.getY(2), 0.001);
        assertEquals(16.0, tabulatedFunction.getY(4), 0.001);
    }

    @Test
    void testInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест интерполяции между точками
        double result = function.apply(2.5);
        double expected = 6.5; // Линейная интерполяция между 4.0 и 9.0
        assertEquals(expected, result, 0.001);
    }

    @Test
    void testExtrapolation() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест экстраполяции слева
        double leftResult = function.apply(0.5);
        assertTrue(leftResult < 1.0 && leftResult > -1.0);

        // Тест экстраполяции справа
        double rightResult = function.apply(5.0);
        assertTrue(rightResult > 16.0);
    }

    @Test
    void testApplyMethod() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

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
    void testGettersAndSetters() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

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
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(5.0));
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(1.0));
        assertEquals(2, function.indexOfY(9.0));
        assertEquals(-1, function.indexOfY(5.0));
    }

    @Test
    void testBounds() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.leftBound(), 0.001);
        assertEquals(4.0, function.rightBound(), 0.001);
    }

    @Test
    void testInsertMethod() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем в середину
        function.insert(2.0, 4.0);
        assertEquals(4, function.getCount());
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getY(1), 0.001);

        // Вставляем в начало
        function.insert(0.0, 0.0);
        assertEquals(5, function.getCount());
        assertEquals(0.0, function.getX(0), 0.001);
        assertEquals(0.0, function.getY(0), 0.001);

        // Вставляем в конец
        function.insert(6.0, 36.0);
        assertEquals(6, function.getCount());
        assertEquals(6.0, function.getX(5), 0.001);
        assertEquals(36.0, function.getY(5), 0.001);
    }

    @Test
    void testInsertExistingX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем существующий x
        function.insert(2.0, 5.0);
        assertEquals(3, function.getCount()); // количество не изменилось
        assertEquals(5.0, function.getY(1), 0.001); // y изменился
    }

    @Test
    void testConstructorValidation() {
        // Тест с разными длинами массивов
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{1.0});
        });

        // Тест с недостаточным количеством точек
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(new double[]{1.0}, new double[]{1.0});
        });

        // Тест с неупорядоченными x
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(new double[]{2.0, 1.0, 3.0}, new double[]{4.0, 1.0, 9.0});
        });
    }

    @Test
    void testIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IndexOutOfBoundsException.class, () -> function.getX(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getX(3));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(3));
        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(-1, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(3, 0.0));
    }

    @Test
    void testInsertWithExistingX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем значение для существующего x
        function.insert(2.0, 16.0);

        // Количество точек не должно измениться
        assertEquals(3, function.getCount());
        // Значение y для x=2.0 должно обновиться
        assertEquals(16.0, function.getY(1), 0.001);
        // Другие значения не должны измениться
        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(9.0, function.getY(2), 0.001);
    }

    @Test
    void testInsertBeforeFirstElement() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем элемент перед первым
        function.insert(1.0, 1.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getY(1), 0.001);
    }

    @Test
    void testInsertAfterLastElement() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем элемент после последнего
        function.insert(4.0, 16.0);

        assertEquals(4, function.getCount());
        assertEquals(4.0, function.getX(3), 0.001);
        assertEquals(16.0, function.getY(3), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);
        assertEquals(9.0, function.getY(2), 0.001);
    }

    @Test
    void testInsertBetweenElements() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем элемент между 1.0 и 3.0
        function.insert(2.0, 4.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);
        assertEquals(5.0, function.getX(3), 0.001);

        assertEquals(1.0, function.getY(0), 0.001);
        assertEquals(4.0, function.getY(1), 0.001);
        assertEquals(9.0, function.getY(2), 0.001);
        assertEquals(25.0, function.getY(3), 0.001);
    }

    @Test
    void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем первый элемент
        function.remove(0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0), 0.001);
        assertEquals(3.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getX(2), 0.001);
    }

    @Test
    void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем последний элемент
        function.remove(3);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);
    }

    @Test
    void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем элемент из середины
        function.remove(2);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getX(2), 0.001);
        assertEquals(5.0, function.getX(3), 0.001);
    }

    @Test
    void testRemoveWithInterpolationAfterRemoval() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем элемент с индексом 1 (x=1.0, y=1.0)
        function.remove(1);

        // Проверяем, что функция все еще работает корректно
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(4.0, function.apply(2.0), 0.001);
        assertEquals(9.0, function.apply(3.0), 0.001);

        // Проверяем интерполяцию
        double interpolated = function.apply(1.5);
        assertTrue(interpolated > 0.0 && interpolated < 9.0);
    }

    @Test
    void testInsertAndRemoveMultipleTimes() {
        double[] xValues = {1.0, 3.0};
        double[] yValues = {1.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставляем несколько элементов
        function.insert(2.0, 4.0);
        function.insert(0.0, 0.0);
        function.insert(4.0, 16.0);

        assertEquals(5, function.getCount());

        // Удаляем элементы
        function.remove(4); // удаляем 4.0
        function.remove(0); // удаляем 0.0

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);
    }
}