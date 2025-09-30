package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionInsertTest {



    @Test
    void testInsertAtBeginning() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставляем в начало
        function.insert(0.0, 0.0);

        assertEquals(4, function.getCount());
        assertEquals(0.0, function.leftBound(), 0.001);
        assertEquals(0.0, function.getX(0), 0.001);
        assertEquals(0.0, function.getY(0), 0.001);
        assertEquals(1.0, function.getX(1), 0.001);
        assertEquals(1.0, function.getY(1), 0.001);
    }

    @Test
    void testInsertAtEnd() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставляем в конец
        function.insert(3.0, 9.0);

        assertEquals(4, function.getCount());
        assertEquals(3.0, function.rightBound(), 0.001);
        assertEquals(3.0, function.getX(3), 0.001);
        assertEquals(9.0, function.getY(3), 0.001);
        assertEquals(2.0, function.getX(2), 0.001);
        assertEquals(4.0, function.getY(2), 0.001);
    }

    @Test
    void testInsertInMiddle() {
        double[] xValues = {0.0, 2.0, 4.0};
        double[] yValues = {0.0, 4.0, 16.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставляем в середину
        function.insert(1.0, 1.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(1), 0.001);
        assertEquals(1.0, function.getY(1), 0.001);
        assertEquals(0.0, function.getX(0), 0.001);
        assertEquals(2.0, function.getX(2), 0.001);
        assertEquals(4.0, function.getX(3), 0.001);

        // Проверяем порядок
        assertTrue(function.getX(0) < function.getX(1));
        assertTrue(function.getX(1) < function.getX(2));
        assertTrue(function.getX(2) < function.getX(3));
    }

    @Test
    void testInsertReplaceExisting() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Заменяем существующее значение
        function.insert(1.0, 10.0);

        assertEquals(3, function.getCount()); // Количество не должно измениться
        assertEquals(1.0, function.getX(1), 0.001);
        assertEquals(10.0, function.getY(1), 0.001); // Значение должно обновиться
        assertEquals(0.0, function.getY(0), 0.001); // Другие значения не должны измениться
        assertEquals(4.0, function.getY(2), 0.001);
    }

//    @Test
//    void testInsertMultipleValues() {
//        double[] xValues = {1.0, 4.0};
//        double[] yValues = {1.0, 16.0};
//
//        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
//
//        // Вставляем несколько значений
//        function.insert(0.0, 0.0);  // в начало
//        function.insert(2.0, 4.0);  // в середину
//        function.insert(3.0, 9.0);  // в середину
//        function.insert(5.0, 25.0); // в конец
//
//        assertEquals(6, function.getCount());
//
//        // Проверяем порядок и значения
//        assertEquals(0.0, function.getX(0), 0.001);
//        assertEquals(1.0, function.getX(1), 0.001);
//        assertEquals(2.0, function.getX(2), 0.001);
//        assertEquals(3.0, function.getX(3), 0.001);
//        assertEquals(4.0, function.getX(4), 0.001);
//        assertEquals(5.0, function.getX(5), 0.001);
//
//        assertEquals(0.0, function.getY(0), 0.001);
//        assertEquals(1.0, function.getY(1), 0.001);
//        assertEquals(4.0, function.getY(2), 0.001);
//        assertEquals(9.0, function.getY(3), 0.001);
//        assertEquals(16.0, function.getY(4), 0.001);
//        assertEquals(25.0, function.getY(5), 0.001);
//    }


//
    @Test
    void testInsertAndApply() {
        double[] xValues = {0.0, 2.0};
        double[] yValues = {0.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставляем точку в середину
        function.insert(1.0, 1.0);

        // Проверяем, что интерполяция работает корректно
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(1.0, function.apply(1.0), 0.001); // точное значение
        assertEquals(2.5, function.apply(1.5), 0.001); // интерполяция
        assertEquals(4.0, function.apply(2.0), 0.001);
    }
}