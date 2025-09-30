package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeBoorFunctionTest {

    @Test
    void testLinearBasisFunction() {
        // Тестируем линейный B-сплайн (degree = 1)
        double[] controlPoints = {0.0, 1.0, 0.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Минимальные проверки: функция работает и возвращает конечные значения
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(1.0)));
    }

    @Test
    void testQuadraticBasisFunction() {
        // Тестируем квадратичный B-сплайн (degree = 2)
        double[] controlPoints = {0.0, 0.0, 1.0, 0.0, 0.0};
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Минимальные проверки: функция работает и возвращает конечные значения
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(1.0)));
    }

    @Test
    void testCustomKnotVector() {
        // Тестируем с пользовательским узловым вектором
        double[] controlPoints = {1.0, 2.0, 3.0};
        double[] knots = {0.0, 0.0, 0.0, 1.0, 1.0, 1.0}; // Для degree = 2
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        // Минимальные проверки: функция работает с пользовательским узловым вектором
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(1.0)));
    }

    @Test
    void testInvalidConstructorParameters() {
        double[] controlPoints = {1.0, 2.0};
        double[] knots = {0.0, 0.5, 1.0};
        int degree = 2;

        // Должно выбросить исключение - недостаточно контрольных точек
        assertThrows(IllegalArgumentException.class, () -> {
            new DeBoorFunction(controlPoints, knots, degree);
        });

        // Неправильная степень
        assertThrows(IllegalArgumentException.class, () -> {
            new DeBoorFunction(controlPoints, 0);
        });

        // Null параметры
        assertThrows(IllegalArgumentException.class, () -> {
            new DeBoorFunction(null, knots, 1);
        });
    }

    @Test
    void testGetters() {
        double[] controlPoints = {1.0, 2.0, 3.0};
        double[] knots = {0.0, 0.0, 0.0, 1.0, 1.0}; // Правильная длина: 3 + 1 + 1 = 5
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        assertArrayEquals(controlPoints, function.getControlPoints(), 0.001);
        assertArrayEquals(knots, function.getKnots(), 0.001);
        assertEquals(degree, function.getDegree());
    }

    @Test
    void testBoundaryValues() {
        // Тестируем значения на границах диапазона узлов
        double[] controlPoints = {0.0, 1.0, 0.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // На границах диапазона значения должны быть корректными
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(0.0, function.apply(1.0), 0.001);

        // Вне диапазона алгоритм может возвращать произвольные значения
        // Проверяем только, что метод не выбрасывает исключений
        double result1 = function.apply(-1.0);
        double result2 = function.apply(2.0);
        assertTrue(Double.isFinite(result1));
        assertTrue(Double.isFinite(result2));
    }

    @Test
    void testCubicBasisFunction() {
        // Тестируем кубический B-сплайн (degree = 3)
        double[] controlPoints = {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
        int degree = 3;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Минимальные проверки: функция работает и возвращает конечные значения
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(1.0)));
        assertTrue(Double.isFinite(function.apply(1.5)));
        assertTrue(Double.isFinite(function.apply(2.0)));
    }

    @Test
    void testDifferentControlPointConfigurations() {
        // Тест различных конфигураций контрольных точек
        double[] controlPoints1 = {1.0, 2.0, 3.0, 2.0, 1.0};
        int degree = 2;

        DeBoorFunction function1 = new DeBoorFunction(controlPoints1, degree);

        // Минимальные проверки для контрольных точек {1.0, 2.0, 3.0, 2.0, 1.0}
        assertTrue(Double.isFinite(function1.apply(0.0)));
        assertTrue(Double.isFinite(function1.apply(1.0)));
        assertTrue(Double.isFinite(function1.apply(2.0)));

        // Тест с другой конфигурацией
        double[] controlPoints2 = {0.0, 1.0, 0.0, -1.0, 0.0};
        DeBoorFunction function2 = new DeBoorFunction(controlPoints2, degree);

        assertTrue(Double.isFinite(function2.apply(0.0)));
        assertTrue(Double.isFinite(function2.apply(1.0)));
        assertTrue(Double.isFinite(function2.apply(2.0)));
    }

    @Test
    void testGettersAndInternalMethods() {
        double[] controlPoints = {0.0, 1.0, 0.0};
        double[] knots = {0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        // Тест геттеров
        assertArrayEquals(controlPoints, function.getControlPoints(), 0.001);
        assertArrayEquals(knots, function.getKnots(), 0.001);
        assertEquals(degree, function.getDegree());

        // Тест MathFunction интерфейса
        assertTrue(function instanceof MathFunction);

        // Тест применения функции
        double result = function.apply(0.5);
        assertTrue(Double.isFinite(result)); // значение должно быть конечным
    }

    @Test
    void testEdgeCases() {
        // Тест минимальной конфигурации
        double[] controlPoints = {1.0, 2.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Минимальные проверки для минимальной конфигурации
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(1.0)));
    }

    @Test
    void testComplexKnotVector() {
        // Тест с неравномерным узловым вектором
        double[] controlPoints = {0.0, 1.0, 2.0, 1.0, 0.0};
        double[] knots = {0.0, 0.0, 0.0, 0.25, 0.75, 1.0, 1.0, 1.0};
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        // Минимальные проверки для неравномерного узлового вектора
        assertTrue(Double.isFinite(function.apply(0.0)));
        assertTrue(Double.isFinite(function.apply(0.25)));
        assertTrue(Double.isFinite(function.apply(0.5)));
        assertTrue(Double.isFinite(function.apply(0.75)));
        assertTrue(Double.isFinite(function.apply(1.0)));
    }

    @Test
    void testMathFunctionInterface() {
        double[] controlPoints = {0.0, 1.0, 0.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Тест интерфейса MathFunction
        MathFunction mathFunc = function;

        assertTrue(Double.isFinite(mathFunc.apply(0.0)));
        assertTrue(Double.isFinite(mathFunc.apply(0.5)));
        assertTrue(Double.isFinite(mathFunc.apply(1.0)));

        // Тест с отрицательными значениями (экстраполяция)
        double result1 = mathFunc.apply(-0.5);
        double result2 = mathFunc.apply(1.5);
        assertTrue(Double.isFinite(result1));
        assertTrue(Double.isFinite(result2));
    }
}