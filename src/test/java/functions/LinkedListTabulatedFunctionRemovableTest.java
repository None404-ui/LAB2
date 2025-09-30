package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для метода remove в LinkedListTabulatedFunction
 */
class LinkedListTabulatedFunctionRemovableTest {

    @Test
    void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем элемент с индексом 2 (значение 3.0)
        function.remove(2);

        // Проверяем, что количество элементов уменьшилось
        assertEquals(4, function.getCount());

        // Проверяем, что элементы сдвинулись
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getX(2), 0.001);
        assertEquals(5.0, function.getX(3), 0.001);

        assertEquals(10.0, function.getY(0), 0.001);
        assertEquals(20.0, function.getY(1), 0.001);
        assertEquals(40.0, function.getY(2), 0.001);
        assertEquals(50.0, function.getY(3), 0.001);
    }

    @Test
    void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем первый элемент
        function.remove(0);

        assertEquals(4, function.getCount());
        assertEquals(2.0, function.getX(0), 0.001);
        assertEquals(3.0, function.getX(1), 0.001);
        assertEquals(4.0, function.getX(2), 0.001);
        assertEquals(5.0, function.getX(3), 0.001);
    }

    @Test
    void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем последний элемент
        function.remove(4);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(3.0, function.getX(2), 0.001);
        assertEquals(4.0, function.getX(3), 0.001);
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Попытка удалить с недопустимым индексом
        assertThrows(IndexOutOfBoundsException.class, () -> function.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> function.remove(10));
    }

    @Test
    void testRemoveFromFunctionWithTwoElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Попытка удалить элемент из функции с 2 элементами должна вызвать исключение
        assertThrows(IllegalStateException.class, () -> function.remove(0));
        assertThrows(IllegalStateException.class, () -> function.remove(1));
    }

    @Test
    void testRemoveAllElements() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем все элементы кроме двух
        function.remove(4); // удаляем 5.0
        function.remove(3); // удаляем 4.0
        function.remove(2); // удаляем 3.0

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(1), 0.001);
        assertEquals(10.0, function.getY(0), 0.001);
        assertEquals(20.0, function.getY(1), 0.001);

        // Попытка удалить еще один элемент должна вызвать исключение
        assertThrows(IllegalStateException.class, () -> function.remove(0));
    }

    @Test
    void testRemoveAndApply() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем элемент с индексом 2 (x=2.0, y=4.0)
        function.remove(2);

        // Проверяем, что функция все еще работает корректно
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(1.0, function.apply(1.0), 0.001);
        assertEquals(9.0, function.apply(3.0), 0.001);
        assertEquals(16.0, function.apply(4.0), 0.001);

        // Проверяем интерполяцию
        double interpolated = function.apply(2.5);
        assertTrue(interpolated > 1.0 && interpolated < 9.0);
    }
}

