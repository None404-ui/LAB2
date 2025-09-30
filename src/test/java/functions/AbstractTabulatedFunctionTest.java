package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для абстрактных методов AbstractTabulatedFunction с использованием MockTabulatedFunction
 */
class AbstractTabulatedFunctionTest {

    @Test
    void testInterpolateMethod() {
        MockTabulatedFunction mock = new MockTabulatedFunction(0.0, 0.0, 2.0, 4.0);

        // Тест линейной интерполяции: y = 2x
        // При x = 1.0 должен быть y = 2.0
        double result = mock.interpolate(1.0, 0.0, 2.0, 0.0, 4.0);
        assertEquals(2.0, result, 0.001);
    }

    @Test
    void testInterpolateWithDifferentValues() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 2.0, 3.0, 8.0);

        // Тест интерполяции между точками (1,2) и (3,8)
        // При x = 2.0 должен быть y = 5.0
        double result = mock.interpolate(2.0, 1.0, 3.0, 2.0, 8.0);
        assertEquals(5.0, result, 0.001);
    }

    @Test
    void testApplyMethodWithExactX() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 5.0, 3.0, 15.0);

        // Тест применения к точным значениям x
        assertEquals(5.0, mock.apply(1.0), 0.001);
        assertEquals(15.0, mock.apply(3.0), 0.001);
    }

    @Test
    void testApplyMethodWithInterpolation() {
        MockTabulatedFunction mock = new MockTabulatedFunction(0.0, 0.0, 4.0, 16.0);

        // Тест интерполяции между точками
        // Линейная функция y = 4x, при x = 2.0 должен быть y = 8.0
        double result = mock.apply(2.0);
        assertEquals(8.0, result, 0.001);
    }

    @Test
    void testApplyMethodWithExtrapolationLeft() {
        MockTabulatedFunction mock = new MockTabulatedFunction(2.0, 4.0, 4.0, 16.0);

        // Тест экстраполяции слева
        // Линейная функция y = 6x - 8 (из точек (2,4) и (4,16))
        // При x = 1.0 должен быть y = 6*1 - 8 = -2.0
        double result = mock.apply(1.0);
        assertEquals(-2.0, result, 0.001);
    }

    @Test
    void testApplyMethodWithExtrapolationRight() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 1.0, 2.0, 4.0);

        // Тест экстраполяции справа
        // Линейная функция y = 3x - 2 (из точек (1,1) и (2,4))
        // При x = 3.0 должен быть y = 3*3 - 2 = 7.0
        double result = mock.apply(3.0);
        assertEquals(7.0, result, 0.001);
    }

    @Test
    void testApplyMethodWithSinglePoint() {
        // Тест с одной точкой (хотя обычно минимум 2, но проверим логику)
        MockTabulatedFunction mock = new MockTabulatedFunction(5.0, 10.0, 7.0, 14.0);

        // Для точных значений x должен возвращаться соответствующий y
        assertEquals(10.0, mock.apply(5.0), 0.001); // точное значение
        assertEquals(14.0, mock.apply(7.0), 0.001); // точное значение

        // Для экстраполяции результат может быть любым
        double extrapolated = mock.apply(6.0);
        assertTrue(extrapolated >= 10.0 && extrapolated <= 14.0);
    }

    @Test
    void testBounds() {
        MockTabulatedFunction mock = new MockTabulatedFunction(10.0, 20.0, 30.0, 60.0);

        assertEquals(10.0, mock.leftBound(), 0.001);
        assertEquals(30.0, mock.rightBound(), 0.001);
        assertEquals(2, mock.getCount());
    }

    @Test
    void testIndexOfX() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.5, 3.0, 2.5, 7.0);

        assertEquals(0, mock.indexOfX(1.5));
        assertEquals(1, mock.indexOfX(2.5));
        assertEquals(-1, mock.indexOfX(2.0));
    }

    @Test
    void testIndexOfY() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 2.0, 3.0, 6.0);

        assertEquals(0, mock.indexOfY(2.0));
        assertEquals(1, mock.indexOfY(6.0));
        assertEquals(-1, mock.indexOfY(4.0));
    }

    @Test
    void testGetters() {
        MockTabulatedFunction mock = new MockTabulatedFunction(5.0, 25.0, 7.0, 49.0);

        assertEquals(5.0, mock.getX(0), 0.001);
        assertEquals(7.0, mock.getX(1), 0.001);
        assertEquals(25.0, mock.getY(0), 0.001);
        assertEquals(49.0, mock.getY(1), 0.001);
    }

    @Test
    void testSetYThrowsException() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 1.0, 2.0, 4.0);

        // MockTabulatedFunction не поддерживает изменение y
        assertThrows(UnsupportedOperationException.class, () -> mock.setY(0, 5.0));
    }

    @Test
    void testFloorIndexOfX() {
        MockTabulatedFunction mock = new MockTabulatedFunction(2.0, 8.0, 6.0, 24.0);

        // Тесты для floorIndexOfX
        assertEquals(0, mock.floorIndexOfX(1.0)); // x < x0
        assertEquals(0, mock.floorIndexOfX(3.0)); // x0 <= x < x1
        assertEquals(0, mock.floorIndexOfX(4.0)); // x0 <= x < x1
        assertEquals(1, mock.floorIndexOfX(6.0)); // x == x1
        assertEquals(1, mock.floorIndexOfX(7.0)); // x > x1
    }

    @Test
    void testMockTabulatedFunctionConstructor() {
        // Тест успешного создания
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 2.0, 3.0, 4.0);
        assertEquals(2, mock.getCount());
        assertEquals(1.0, mock.getX(0));
        assertEquals(3.0, mock.getX(1));

        // Тест исключения при некорректных параметрах
        assertThrows(IllegalArgumentException.class, () -> {
            new MockTabulatedFunction(3.0, 4.0, 1.0, 2.0); // x0 >= x1
        });
    }

    @Test
    void testMockTabulatedFunctionGetters() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.5, 10.0, 2.5, 20.0);

        // Тест getX
        assertEquals(1.5, mock.getX(0), 0.001);
        assertEquals(2.5, mock.getX(1), 0.001);

        // Тест getY
        assertEquals(10.0, mock.getY(0), 0.001);
        assertEquals(20.0, mock.getY(1), 0.001);
    }

    @Test
    void testMockTabulatedFunctionGettersOutOfBounds() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 2.0, 3.0, 4.0);

        // Тест исключений при выходе за границы
        assertThrows(IndexOutOfBoundsException.class, () -> mock.getX(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> mock.getX(2));
        assertThrows(IndexOutOfBoundsException.class, () -> mock.getY(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> mock.getY(2));
    }

    @Test
    void testMockTabulatedFunctionSetY() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 2.0, 3.0, 4.0);

        // Тест исключения при попытке изменить значение
        assertThrows(UnsupportedOperationException.class, () -> mock.setY(0, 5.0));
        assertThrows(IndexOutOfBoundsException.class, () -> mock.setY(-1, 5.0));
        assertThrows(IndexOutOfBoundsException.class, () -> mock.setY(2, 5.0));
    }

    @Test
    void testMockTabulatedFunctionIndexOfX() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.5, 10.0, 2.5, 20.0);

        // Тест поиска существующих значений
        assertEquals(0, mock.indexOfX(1.5));
        assertEquals(1, mock.indexOfX(2.5));

        // Тест поиска несуществующих значений
        assertEquals(-1, mock.indexOfX(2.0));
        assertEquals(-1, mock.indexOfX(1.0));
        assertEquals(-1, mock.indexOfX(3.0));
    }

    @Test
    void testMockTabulatedFunctionIndexOfY() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.5, 10.0, 2.5, 20.0);

        // Тест поиска существующих значений
        assertEquals(0, mock.indexOfY(10.0));
        assertEquals(1, mock.indexOfY(20.0));

        // Тест поиска несуществующих значений
        assertEquals(-1, mock.indexOfY(15.0));
        assertEquals(-1, mock.indexOfY(25.0));
    }

    @Test
    void testMockTabulatedFunctionExtrapolateLeft() {
        MockTabulatedFunction mock = new MockTabulatedFunction(2.0, 4.0, 4.0, 16.0);

        // Тест экстраполяции слева
        // Линейная функция y = 6x - 8 (из точек (2,4) и (4,16))
        double result = mock.extrapolateLeft(1.0);
        assertEquals(-2.0, result, 0.001);
    }

    @Test
    void testMockTabulatedFunctionExtrapolateRight() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 1.0, 2.0, 4.0);

        // Тест экстраполяции справа
        // Линейная функция y = 3x - 2 (из точек (1,1) и (2,4))
        double result = mock.extrapolateRight(3.0);
        assertEquals(7.0, result, 0.001);
    }

    @Test
    void testMockTabulatedFunctionInterpolateWithFloorIndex() {
        MockTabulatedFunction mock = new MockTabulatedFunction(0.0, 0.0, 4.0, 16.0);

        // Тест интерполяции с указанием индекса интервала
        // Линейная функция y = 4x, при x = 2.0 должен быть y = 8.0
        double result1 = mock.interpolate(2.0, 0);
        assertEquals(8.0, result1, 0.001);

        double result2 = mock.interpolate(2.0, 1);
        assertEquals(8.0, result2, 0.001);
    }

    @Test
    void testMockTabulatedFunctionComprehensive() {
        MockTabulatedFunction mock = new MockTabulatedFunction(-1.0, 1.0, 1.0, 3.0);

        // Тест всех методов вместе
        assertEquals(2, mock.getCount());

        // Тест граничных значений
        assertEquals(-1.0, mock.getX(0));
        assertEquals(1.0, mock.getX(1));
        assertEquals(1.0, mock.getY(0));
        assertEquals(3.0, mock.getY(1));

        // Тест поиска
        assertEquals(0, mock.indexOfX(-1.0));
        assertEquals(1, mock.indexOfX(1.0));
        assertEquals(0, mock.indexOfY(1.0));
        assertEquals(1, mock.indexOfY(3.0));

        // Тест floorIndexOfX для всех случаев
        assertEquals(0, mock.floorIndexOfX(-2.0)); // x < x0
        assertEquals(0, mock.floorIndexOfX(0.0));  // x0 <= x < x1
        assertEquals(1, mock.floorIndexOfX(1.0));  // x == x1
        assertEquals(1, mock.floorIndexOfX(2.0));  // x > x1

        // Тест интерполяции и экстраполяции
        assertEquals(2.0, mock.apply(0.0), 0.001); // точное значение
        assertEquals(2.5, mock.apply(0.5), 0.001); // интерполяция
        assertEquals(0.0, mock.apply(-2.0), 0.001); // экстраполяция слева
        assertEquals(4.0, mock.apply(2.0), 0.001);  // экстраполяция справа
    }
}
