package functions;

import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import operations.TabulatedFunctionOperationService;


class TabulatedFunctionOperationServiceTest {

    @Test
    void testMultiply() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] yValues2 = {5.0, 6.0, 7.0};

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues1, yValues2);

        TabulatedFunction result = service.multiply(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(10.0, result.getY(0), 0.001); // 2.0 * 5.0 = 10.0
        assertEquals(18.0, result.getY(1), 0.001); // 3.0 * 6.0 = 18.0
        assertEquals(28.0, result.getY(2), 0.001); // 4.0 * 7.0 = 28.0
    }

    @Test
    void testDivide() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        double[] yValues2 = {2.0, 4.0, 5.0};

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues, yValues2);

        TabulatedFunction result = service.divide(func1, func2);

        assertEquals(3, result.getCount());
        assertEquals(5.0, result.getY(0), 0.001);  // 10.0 / 2.0 = 5.0
        assertEquals(5.0, result.getY(1), 0.001);  // 20.0 / 4.0 = 5.0
        assertEquals(6.0, result.getY(2), 0.001);  // 30.0 / 5.0 = 6.0
    }

    @Test
    void testDivideByZero() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        double[] yValues2 = {2.0, 0.0, 5.0}; // ноль во второй точке

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues, yValues2);

        assertThrows(ArithmeticException.class, () -> {
            service.divide(func1, func2);
        });
    }

    @Test
    void testMultiplyWithDifferentFactories() {
        // Тест с Array фабрикой
        TabulatedFunctionOperationService arrayService =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        // Тест с LinkedList фабрикой
        TabulatedFunctionOperationService linkedService =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        double[] xValues = {1.0, 2.0};
        double[] yValues1 = {3.0, 4.0};
        double[] yValues2 = {5.0, 6.0};

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues, yValues2);

        TabulatedFunction arrayResult = arrayService.multiply(func1, func2);
        TabulatedFunction linkedResult = linkedService.multiply(func1, func2);

        // Проверяем, что результаты одинаковы по значениям
        assertEquals(arrayResult.getCount(), linkedResult.getCount());
        for (int i = 0; i < arrayResult.getCount(); i++) {
            assertEquals(arrayResult.getX(i), linkedResult.getX(i), 0.001);
            assertEquals(arrayResult.getY(i), linkedResult.getY(i), 0.001);
        }

        // Проверяем типы результатов
        assertTrue(arrayResult instanceof ArrayTabulatedFunction);
        assertTrue(linkedResult instanceof LinkedListTabulatedFunction);
    }

    @Test
    void testMixedTypesOperations() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        double[] yValues2 = {1.0, 2.0, 3.0};

        // Смешиваем типы функций
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues2);

        // Умножение: Array * LinkedList
        TabulatedFunction multiplyResult = service.multiply(arrayFunc, linkedFunc);
        assertEquals(2.0, multiplyResult.getY(0), 0.001); // 2.0 * 1.0 = 2.0
        assertEquals(6.0, multiplyResult.getY(1), 0.001); // 3.0 * 2.0 = 6.0
        assertEquals(12.0, multiplyResult.getY(2), 0.001); // 4.0 * 3.0 = 12.0

        // Деление: Array / LinkedList
        TabulatedFunction divideResult = service.divide(arrayFunc, linkedFunc);
        assertEquals(2.0, divideResult.getY(0), 0.001); // 2.0 / 1.0 = 2.0
        assertEquals(1.5, divideResult.getY(1), 0.001); // 3.0 / 2.0 = 1.5
        assertEquals(1.333, divideResult.getY(2), 0.001); // 4.0 / 3.0 ≈ 1.333
    }
}