package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для SqrFunction
 */
class SqrFunctionTest {

    @Test
    void testBasicSquaring() {
        SqrFunction sqr = new SqrFunction();

        // Тест с положительными числами
        assertEquals(0.0, sqr.apply(0.0), 0.001);
        assertEquals(1.0, sqr.apply(1.0), 0.001);
        assertEquals(4.0, sqr.apply(2.0), 0.001);
        assertEquals(9.0, sqr.apply(3.0), 0.001);
        assertEquals(16.0, sqr.apply(4.0), 0.001);
        assertEquals(25.0, sqr.apply(5.0), 0.001);
    }

    @Test
    void testNegativeNumbers() {
        SqrFunction sqr = new SqrFunction();

        // Тест с отрицательными числами
        assertEquals(1.0, sqr.apply(-1.0), 0.001);
        assertEquals(4.0, sqr.apply(-2.0), 0.001);
        assertEquals(9.0, sqr.apply(-3.0), 0.001);
        assertEquals(16.0, sqr.apply(-4.0), 0.001);
        assertEquals(25.0, sqr.apply(-5.0), 0.001);
    }

    @Test
    void testDecimalNumbers() {
        SqrFunction sqr = new SqrFunction();

        // Тест с десятичными числами
        assertEquals(0.25, sqr.apply(0.5), 0.001);
        assertEquals(0.25, sqr.apply(-0.5), 0.001);
        assertEquals(2.25, sqr.apply(1.5), 0.001);
        assertEquals(2.25, sqr.apply(-1.5), 0.001);
        assertEquals(6.25, sqr.apply(2.5), 0.001);
        assertEquals(6.25, sqr.apply(-2.5), 0.001);
    }

    @Test
    void testLargeNumbers() {
        SqrFunction sqr = new SqrFunction();

        // Тест с большими числами
        assertEquals(100.0, sqr.apply(10.0), 0.001);
        assertEquals(100.0, sqr.apply(-10.0), 0.001);
        assertEquals(10000.0, sqr.apply(100.0), 0.001);
        assertEquals(10000.0, sqr.apply(-100.0), 0.001);
    }

    @Test
    void testSmallNumbers() {
        SqrFunction sqr = new SqrFunction();

        // Тест с очень маленькими числами
        assertEquals(0.01, sqr.apply(0.1), 0.001);
        assertEquals(0.01, sqr.apply(-0.1), 0.001);
        assertEquals(0.0001, sqr.apply(0.01), 0.001);
        assertEquals(0.0001, sqr.apply(-0.01), 0.001);
    }

    @Test
    void testSpecialValues() {
        SqrFunction sqr = new SqrFunction();

        // Тест с NaN
        assertTrue(Double.isNaN(sqr.apply(Double.NaN)));

        // Тест с бесконечностью
        assertEquals(Double.POSITIVE_INFINITY, sqr.apply(Double.POSITIVE_INFINITY));
        assertEquals(Double.POSITIVE_INFINITY, sqr.apply(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testMathFunctionInterface() {
        SqrFunction sqr = new SqrFunction();

        // Проверяем, что SqrFunction реализует MathFunction
        assertTrue(sqr instanceof MathFunction);

        // Тест через интерфейс
        MathFunction func = sqr;
        assertEquals(9.0, func.apply(3.0), 0.001);
        assertEquals(16.0, func.apply(4.0), 0.001);
    }

    @Test
    void testPrecision() {
        SqrFunction sqr = new SqrFunction();

        // Тест точности вычислений
        double result = sqr.apply(Math.PI);
        double expected = Math.PI * Math.PI;
        assertEquals(expected, result, 1e-10);

        double result2 = sqr.apply(Math.E);
        double expected2 = Math.E * Math.E;
        assertEquals(expected2, result2, 1e-10);
    }
}