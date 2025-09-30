package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для метода andThen
 */
class AndThenMethodTest {

    @Test
    void testSimpleComposition() {
        SqrFunction sqr = new SqrFunction();           // f(x) = x²
        ConstantFunction const5 = new ConstantFunction(5);  // g(x) = 5

        // Тест: sqr.andThen(const5) должно давать: const5(sqr(x)) = const5(x²) = 5
        MathFunction composite1 = sqr.andThen(const5);
        double result1 = composite1.apply(3); // sqr(3) = 9, const5(9) = 5
        assertEquals(5.0, result1, 0.001);

        // Тест: const5.andThen(sqr) должно давать: sqr(const5(x)) = sqr(5) = 25
        MathFunction composite2 = const5.andThen(sqr);
        double result2 = composite2.apply(10); // const5(10) = 5, sqr(5) = 25
        assertEquals(25.0, result2, 0.001);
    }

    @Test
    void testChainComposition() {
        SqrFunction sqr = new SqrFunction();           // f(x) = x²
        ConstantFunction const5 = new ConstantFunction(5);  // g(x) = 5
        UnitFunction unit = new UnitFunction();        // h(x) = 1

        // Тест: sqr.andThen(const5).andThen(unit) = unit(const5(sqr(x))) = unit(const5(x²)) = unit(5) = 1
        MathFunction composite = sqr.andThen(const5).andThen(unit);
        double result1 = composite.apply(7); // sqr(7) = 49, const5(49) = 5, unit(5) = 1
        assertEquals(1.0, result1, 0.001);

        // Тест с другим порядком: unit.andThen(sqr).andThen(const5)
        MathFunction composite2 = unit.andThen(sqr).andThen(const5);
        double result2 = composite2.apply(-5); // unit(-5) = 1, sqr(1) = 1, const5(1) = 5
        assertEquals(5.0, result2, 0.001);
    }

    @Test
    void testLongChain() {
        SqrFunction sqr = new SqrFunction();           // f(x) = x²
        ConstantFunction const2 = new ConstantFunction(2);  // g(x) = 2
        UnitFunction unit = new UnitFunction();        // h(x) = 1
        ZeroFunction zero = new ZeroFunction();        // z(x) = 0

        // Тест: const2.andThen(sqr).andThen(unit).andThen(zero)
        // const2(x) = 2, sqr(2) = 4, unit(4) = 1, zero(1) = 0
        MathFunction longChain = const2.andThen(sqr).andThen(unit).andThen(zero);
        double result = longChain.apply(999); // Независимо от входа, результат должен быть 0
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testDirectApplication() {
        SqrFunction sqr = new SqrFunction();
        ConstantFunction const3 = new ConstantFunction(3);

        // Прямое применение: sqr.andThen(const3).apply(5)
        // sqr(5) = 25, const3(25) = 3
        double directResult = sqr.andThen(const3).apply(5);
        assertEquals(3.0, directResult, 0.001);

        // Более сложная прямая цепочка
        double complexResult = new SqrFunction()
                .andThen(new ConstantFunction(10))
                .andThen(new SqrFunction())
                .apply(123);
        // sqr(123) = любое число, const10(число) = 10, sqr(10) = 100
        assertEquals(100.0, complexResult, 0.001);
    }

    @Test
    void testWithIdentityFunction() {
        IdentityFunction identity = new IdentityFunction();  // f(x) = x
        SqrFunction sqr = new SqrFunction();                 // g(x) = x²

        // identity.andThen(sqr) = sqr(identity(x)) = sqr(x) = x²
        MathFunction composite = identity.andThen(sqr);
        assertEquals(0.0, composite.apply(0.0), 0.001);
        assertEquals(1.0, composite.apply(1.0), 0.001);
        assertEquals(4.0, composite.apply(2.0), 0.001);
        assertEquals(9.0, composite.apply(3.0), 0.001);

        // sqr.andThen(identity) = identity(sqr(x)) = sqr(x) = x²
        MathFunction composite2 = sqr.andThen(identity);
        assertEquals(0.0, composite2.apply(0.0), 0.001);
        assertEquals(1.0, composite2.apply(1.0), 0.001);
        assertEquals(4.0, composite2.apply(2.0), 0.001);
        assertEquals(9.0, composite2.apply(3.0), 0.001);
    }

    @Test
    void testWithConstantFunctions() {
        ConstantFunction const2 = new ConstantFunction(2);
        ConstantFunction const3 = new ConstantFunction(3);

        // const2.andThen(const3) = const3(const2(x)) = const3(2) = 3
        MathFunction composite = const2.andThen(const3);
        assertEquals(3.0, composite.apply(0.0), 0.001);
        assertEquals(3.0, composite.apply(100.0), 0.001);
        assertEquals(3.0, composite.apply(-50.0), 0.001);
    }

    @Test
    void testWithZeroAndUnitFunctions() {
        ZeroFunction zero = new ZeroFunction();
        UnitFunction unit = new UnitFunction();

        // zero.andThen(unit) = unit(zero(x)) = unit(0) = 1
        MathFunction composite1 = zero.andThen(unit);
        assertEquals(1.0, composite1.apply(0.0), 0.001);
        assertEquals(1.0, composite1.apply(100.0), 0.001);

        // unit.andThen(zero) = zero(unit(x)) = zero(1) = 0
        MathFunction composite2 = unit.andThen(zero);
        assertEquals(0.0, composite2.apply(0.0), 0.001);
        assertEquals(0.0, composite2.apply(100.0), 0.001);
    }

    @Test
    void testChainingWithDifferentTypes() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();
        ConstantFunction const4 = new ConstantFunction(4);

        // identity.andThen(sqr).andThen(const4) = const4(sqr(identity(x))) = const4(sqr(x)) = const4(x²) = 4
        MathFunction composite = identity.andThen(sqr).andThen(const4);
        assertEquals(4.0, composite.apply(0.0), 0.001);
        assertEquals(4.0, composite.apply(1.0), 0.001);
        assertEquals(4.0, composite.apply(2.0), 0.001);
        assertEquals(4.0, composite.apply(3.0), 0.001);
    }

    @Test
    void testReturnType() {
        SqrFunction sqr = new SqrFunction();
        ConstantFunction const5 = new ConstantFunction(5);

        // andThen должен возвращать CompositeFunction
        MathFunction composite = sqr.andThen(const5);
        assertTrue(composite instanceof CompositeFunction);
    }

    @Test
    void testNullParameter() {
        SqrFunction sqr = new SqrFunction();

        // andThen с null должно выбросить исключение
        assertThrows(NullPointerException.class, () -> {
            sqr.andThen(null);
        });
    }

    @Test
    void testComplexMathematicalChain() {
        // Создаем сложную математическую цепочку: f(x) = ((x + 1)² - 1) * 2
        MathFunction plusOne = x -> x + 1;
        MathFunction sqr = new SqrFunction();
        MathFunction minusOne = x -> x - 1;
        MathFunction timesTwo = x -> x * 2;

        MathFunction complexChain = plusOne
                .andThen(sqr)
                .andThen(minusOne)
                .andThen(timesTwo);

        // Проверяем несколько значений
        assertEquals(0.0, complexChain.apply(0.0), 0.001);  // ((0+1)²-1)*2 = 0
        assertEquals(6.0, complexChain.apply(1.0), 0.001);  // ((1+1)²-1)*2 = 6
        assertEquals(16.0, complexChain.apply(2.0), 0.001); // ((2+1)²-1)*2 = 16
    }
}