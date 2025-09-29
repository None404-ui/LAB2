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

        // Проверяем значения в различных точках
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(0.5, function.apply(0.25), 0.001);
        assertEquals(1.0, function.apply(0.5), 0.001);
        assertEquals(0.5, function.apply(0.75), 0.001);
        assertEquals(0.0, function.apply(1.0), 0.001);
    }

    @Test
    void testQuadraticBasisFunction() {
        // Тестируем квадратичный B-сплайн (degree = 2)
        double[] controlPoints = {0.0, 0.0, 1.0, 0.0, 0.0};
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Проверяем симметричность и основные значения
        assertEquals(0.0, function.apply(0.0), 0.001);
        assertEquals(0.125, function.apply(0.25), 0.001);
        assertEquals(0.5, function.apply(0.5), 0.001);
        assertEquals(0.125, function.apply(0.75), 0.001);
        assertEquals(0.0, function.apply(1.0), 0.001);
    }

    @Test
    void testCustomKnotVector() {
        // Тестируем с пользовательским узловым вектором
        double[] controlPoints = {1.0, 2.0, 3.0};
        double[] knots = {0.0, 0.0, 0.0, 1.0, 1.0, 1.0}; // Для degree = 2
        int degree = 2;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        // Проверяем граничные значения
        assertEquals(1.0, function.apply(0.0), 0.001);
        assertEquals(3.0, function.apply(1.0), 0.001);
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
        double[] knots = {0.0, 0.0, 1.0, 1.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, knots, degree);

        assertArrayEquals(controlPoints, function.getControlPoints(), 0.001);
        assertArrayEquals(knots, function.getKnots(), 0.001);
        assertEquals(degree, function.getDegree());
    }

    @Test
    void testOutsideKnotRange() {
        // Тестируем значения вне диапазона узлов
        double[] controlPoints = {0.0, 1.0, 0.0};
        int degree = 1;

        DeBoorFunction function = new DeBoorFunction(controlPoints, degree);

        // Вне диапазона должно возвращать 0
        assertEquals(0.0, function.apply(-1.0), 0.001);
        assertEquals(0.0, function.apply(2.0), 0.001);
    }
}