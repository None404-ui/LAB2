package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для константных функций
 */
class ConstantFunctionsTest {

    @Test
    void testConstantFunction() {
        // Тест с положительной константой
        ConstantFunction func1 = new ConstantFunction(5.7);
        assertEquals(5.7, func1.apply(10), 0.001);
        assertEquals(5.7, func1.apply(-3.2), 0.001);
        assertEquals(5.7, func1.apply(0), 0.001);
        assertEquals(5.7, func1.getConstant(), 0.001);

        // Тест с отрицательной константой
        ConstantFunction func2 = new ConstantFunction(-2.5);
        assertEquals(-2.5, func2.apply(100), 0.001);
        assertEquals(-2.5, func2.apply(-100), 0.001);
        assertEquals(-2.5, func2.getConstant(), 0.001);

        // Тест с нулевой константой
        ConstantFunction func3 = new ConstantFunction(0);
        assertEquals(0, func3.apply(123.45), 0.001);
        assertEquals(0, func3.getConstant(), 0.001);
    }

    @Test
    void testZeroFunction() {
        ZeroFunction zeroFunc = new ZeroFunction();

        // Тест с различными значениями x
        assertEquals(0, zeroFunc.apply(5), 0.001);
        assertEquals(0, zeroFunc.apply(-10.5), 0.001);
        assertEquals(0, zeroFunc.apply(0), 0.001);
        assertEquals(0, zeroFunc.apply(999.999), 0.001);

        // Тест геттера
        assertEquals(0, zeroFunc.getConstant(), 0.001);
    }

    @Test
    void testUnitFunction() {
        UnitFunction unitFunc = new UnitFunction();

        // Тест с различными значениями x
        assertEquals(1, unitFunc.apply(5), 0.001);
        assertEquals(1, unitFunc.apply(-10.5), 0.001);
        assertEquals(1, unitFunc.apply(0), 0.001);
        assertEquals(1, unitFunc.apply(999.999), 0.001);

        // Тест геттера
        assertEquals(1, unitFunc.getConstant(), 0.001);
    }

    @Test
    void testInheritance() {
        // Проверяем, что ZeroFunction и UnitFunction наследуются от ConstantFunction
        ZeroFunction zero = new ZeroFunction();
        UnitFunction unit = new UnitFunction();

        assertTrue(zero instanceof ConstantFunction);
        assertTrue(unit instanceof ConstantFunction);
        assertTrue(zero instanceof MathFunction);
        assertTrue(unit instanceof MathFunction);
    }

    @Test
    void testSpecialValues() {
        ConstantFunction func = new ConstantFunction(42.0);

        // Тест с NaN - константная функция должна возвращать константу
        assertEquals(42.0, func.apply(Double.NaN), 0.001);

        // Тест с бесконечностью
        assertEquals(42.0, func.apply(Double.POSITIVE_INFINITY), 0.001);
        assertEquals(42.0, func.apply(Double.NEGATIVE_INFINITY), 0.001);
    }

    @Test
    void testConstantFunctionWithDifferentValues() {
        // Тест константных функций с различными значениями
        ConstantFunction zeroFunc = new ConstantFunction(0.0);
        ConstantFunction oneFunc = new ConstantFunction(1.0);
        ConstantFunction piFunc = new ConstantFunction(Math.PI);
        ConstantFunction negativeFunc = new ConstantFunction(-5.5);

        // Тест применения функций
        assertEquals(0.0, zeroFunc.apply(0.0), 0.001);
        assertEquals(0.0, zeroFunc.apply(100.0), 0.001);
        assertEquals(0.0, zeroFunc.apply(-50.0), 0.001);

        assertEquals(1.0, oneFunc.apply(0.0), 0.001);
        assertEquals(1.0, oneFunc.apply(100.0), 0.001);
        assertEquals(1.0, oneFunc.apply(-50.0), 0.001);

        assertEquals(Math.PI, piFunc.apply(0.0), 0.001);
        assertEquals(Math.PI, piFunc.apply(100.0), 0.001);
        assertEquals(Math.PI, piFunc.apply(-50.0), 0.001);

        assertEquals(-5.5, negativeFunc.apply(0.0), 0.001);
        assertEquals(-5.5, negativeFunc.apply(100.0), 0.001);
        assertEquals(-5.5, negativeFunc.apply(-50.0), 0.001);

        // Тест геттеров
        assertEquals(0.0, zeroFunc.getConstant(), 0.001);
        assertEquals(1.0, oneFunc.getConstant(), 0.001);
        assertEquals(Math.PI, piFunc.getConstant(), 0.001);
        assertEquals(-5.5, negativeFunc.getConstant(), 0.001);
    }

    @Test
    void testZeroAndUnitFunctions() {
        ZeroFunction zeroFunc = new ZeroFunction();
        UnitFunction unitFunc = new UnitFunction();

        // Тест ZeroFunction
        assertEquals(0.0, zeroFunc.apply(0.0), 0.001);
        assertEquals(0.0, zeroFunc.apply(100.0), 0.001);
        assertEquals(0.0, zeroFunc.apply(-50.0), 0.001);
        assertEquals(0.0, zeroFunc.getConstant(), 0.001);

        // Тест UnitFunction
        assertEquals(1.0, unitFunc.apply(0.0), 0.001);
        assertEquals(1.0, unitFunc.apply(100.0), 0.001);
        assertEquals(1.0, unitFunc.apply(-50.0), 0.001);
        assertEquals(1.0, unitFunc.getConstant(), 0.001);

        // Тест наследования
        assertTrue(zeroFunc instanceof ConstantFunction);
        assertTrue(unitFunc instanceof ConstantFunction);
        assertTrue(zeroFunc instanceof MathFunction);
        assertTrue(unitFunc instanceof MathFunction);
    }

    @Test
    void testConstantFunctionEdgeCases() {
        // Тест с очень большими значениями
        ConstantFunction largeFunc = new ConstantFunction(1e10);
        assertEquals(1e10, largeFunc.apply(0.0), 0.001);
        assertEquals(1e10, largeFunc.apply(1e10), 0.001);

        // Тест с очень маленькими значениями
        ConstantFunction smallFunc = new ConstantFunction(1e-10);
        assertEquals(1e-10, smallFunc.apply(0.0), 0.001);
        assertEquals(1e-10, smallFunc.apply(1e-10), 0.001);

        // Тест с нулем
        ConstantFunction zeroFunc = new ConstantFunction(0.0);
        assertEquals(0.0, zeroFunc.apply(0.0), 0.001);
        assertEquals(0.0, zeroFunc.apply(Double.MAX_VALUE), 0.001);
        assertEquals(0.0, zeroFunc.apply(Double.MIN_VALUE), 0.001);
    }
}