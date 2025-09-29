package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunction1Test {

    @Test
    void testConstructorFromArrays() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(0.0, function.leftBound(), 0.001);
        assertEquals(3.0, function.rightBound(), 0.001);
    }

    @Test
    void testConstructorFromFunction() {
        IdentityFunction identity = new IdentityFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(identity, 0, 4, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.leftBound(), 0.001);
        assertEquals(4.0, function.rightBound(), 0.001);
    }

    @Test
    void testGetXGetY() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.getX(0), 0.001);
        assertEquals(1.0, function.getX(1), 0.001);
        assertEquals(2.0, function.getX(2), 0.001);

        assertEquals(0.0, function.getY(0), 0.001);
        assertEquals(1.0, function.getY(1), 0.001);
        assertEquals(4.0, function.getY(2), 0.001);
    }

    @Test
    void testSetY() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.setY(1, 10.0);
        assertEquals(10.0, function.getY(1), 0.001);
    }

    @Test
    void testIndexOfX() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(0.0));
        assertEquals(2, function.indexOfX(2.0));
        assertEquals(-1, function.indexOfX(5.0)); // не существует
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(0.0));
        assertEquals(2, function.indexOfY(4.0));
        assertEquals(-1, function.indexOfY(5.0)); // не существует
    }

    @Test
    void testApplyInterpolation() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Интерполяция между 0 и 1
        assertEquals(0.5, function.apply(0.5), 0.001);

        // Интерполяция между 1 и 2
        assertEquals(2.5, function.apply(1.5), 0.001);
    }

    @Test
    void testApplyExtrapolation() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Экстраполяция слева
        assertEquals(-1.0, function.apply(-1.0), 0.001);

        // Экстраполяция справа
        assertEquals(7.0, function.apply(3.0), 0.001);
    }

    @Test
    void testSinglePoint() {
        double[] xValues = {2.0};
        double[] yValues = {4.0};

        // Должен выбросить исключение - нужно минимум 2 точки
        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testInvalidArrays() {
        double[] xValues = {0.0, 1.0};
        double[] yValues = {0.0, 1.0, 2.0}; // разная длина

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }
}