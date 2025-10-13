package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

/**
 * Тесты для дифференциального оператора
 */
class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithLinearFunction() {
        // Тестируем линейную функцию f(x) = 2x + 1
        // Производная должна быть константой f'(x) = 2

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0}; // 2x + 1

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.derive(function);

        // Проверяем, что производная примерно равна 2 везде
        assertEquals(4, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 0.001); // (3-1)/(1-0) = 2
        assertEquals(2.0, derivative.getY(1), 0.001); // (5-3)/(2-1) = 2
        assertEquals(2.0, derivative.getY(2), 0.001); // (7-5)/(3-2) = 2
        assertEquals(2.0, derivative.getY(3), 0.001); // последняя равна предпоследней
    }

    @Test
    void testDeriveWithQuadraticFunction() {
        // Тестируем квадратичную функцию f(x) = x²
        // Производная должна быть линейной f'(x) = 2x

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0}; // x²

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.derive(function);

        // Проверяем численные производные
        assertEquals(4, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 0.001); // (1-0)/(1-0) = 1
        assertEquals(3.0, derivative.getY(1), 0.001); // (4-1)/(2-1) = 3
        assertEquals(5.0, derivative.getY(2), 0.001); // (9-4)/(3-2) = 5
        assertEquals(5.0, derivative.getY(3), 0.001); // последняя равна предпоследней
    }

    @Test
    void testDeriveWithDifferentFactories() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тестируем с Array фабрикой
        TabulatedDifferentialOperator arrayOperator =
                new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        TabulatedFunction arrayDerivative = arrayOperator.derive(function);
        assertTrue(arrayDerivative instanceof ArrayTabulatedFunction);

        // Тестируем с LinkedList фабрикой
        TabulatedDifferentialOperator linkedOperator =
                new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction linkedDerivative = linkedOperator.derive(function);
        assertTrue(linkedDerivative instanceof LinkedListTabulatedFunction);

        // Проверяем, что производные одинаковы по значениям
        assertEquals(arrayDerivative.getCount(), linkedDerivative.getCount());
        for (int i = 0; i < arrayDerivative.getCount(); i++) {
            assertEquals(arrayDerivative.getX(i), linkedDerivative.getX(i), 0.001);
            assertEquals(arrayDerivative.getY(i), linkedDerivative.getY(i), 0.001);
        }
    }

    @Test
    void testFactoryGetterAndSetter() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // Проверяем фабрику по умолчанию
        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory);

        // Меняем фабрику
        LinkedListTabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        operator.setFactory(newFactory);

        // Проверяем, что фабрика изменилась
        assertSame(newFactory, operator.getFactory());
    }

    @Test
    void testDeriveWithMinimumPoints() {
        // Тестируем с минимальным количеством точек (2)
        double[] xValues = {0.0, 1.0};
        double[] yValues = {0.0, 1.0};

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(2, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 0.001); // (1-0)/(1-0) = 1
        assertEquals(1.0, derivative.getY(1), 0.001); // последняя равна предпоследней
    }
}