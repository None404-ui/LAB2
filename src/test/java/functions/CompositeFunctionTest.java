package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//Тесты для композитной функции
class CompositeFunctionTest {

    @Test
    void testSimpleComposition() {
        // f(x) = x + 1
        MathFunction plusOne = new MathFunction() {
            @Override
            public double apply(double x) {
                return x + 1;
            }
        };

        // g(x) = x * 2
        MathFunction timesTwo = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * 2;
            }
        };

        // h(x) = g(f(x)) = (x + 1) * 2
        CompositeFunction composite = new CompositeFunction(plusOne, timesTwo);

        assertEquals(4.0, composite.apply(1.0), 0.001);  // (1 + 1) * 2 = 4
        assertEquals(6.0, composite.apply(2.0), 0.001);  // (2 + 1) * 2 = 6
        assertEquals(0.0, composite.apply(-1.0), 0.001); // (-1 + 1) * 2 = 0
    }

    @Test
    void testWithIdentityFunction() {
        IdentityFunction identity = new IdentityFunction();  // f(x) = x

        // g(x) = x * 3
        MathFunction timesThree = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * 3;
            }
        };

        // h(x) = g(f(x)) = (x) * 3 = 3x
        CompositeFunction composite = new CompositeFunction(identity, timesThree);

        assertEquals(0.0, composite.apply(0.0), 0.001);   // 3*0 = 0
        assertEquals(3.0, composite.apply(1.0), 0.001);   // 3*1 = 3
        assertEquals(6.0, composite.apply(2.0), 0.001);   // 3*2 = 6
    }

    @Test
    void testNestedComposition() {
        // f(x) = x + 1
        MathFunction plusOne = x -> x + 1;

        // g(x) = x * 2
        MathFunction timesTwo = x -> x * 2;

        // k(x) = x - 5
        MathFunction minusFive = x -> x - 5;

        // Сначала создаем g(f(x)) = (x + 1) * 2
        CompositeFunction firstLevel = new CompositeFunction(plusOne, timesTwo);

        // Затем создаем k(g(f(x))) = ((x + 1) * 2) - 5
        CompositeFunction secondLevel = new CompositeFunction(firstLevel, minusFive);

        assertEquals(-1.0, secondLevel.apply(1.0), 0.001);  // ((1 + 1) * 2) - 5 = -1
        assertEquals(1.0, secondLevel.apply(2.0), 0.001);   // ((2 + 1) * 2) - 5 = 1
    }

    @Test
    void testSelfComposition() {
        // Композиция функции с самой собой: h(x) = f(f(x))
        MathFunction plusOne = x -> x + 1;

        // f(f(x)) = (x + 1) + 1 = x + 2
        CompositeFunction selfComposite = new CompositeFunction(plusOne, plusOne);

        assertEquals(3.0, selfComposite.apply(1.0), 0.001);  // 1 + 2 = 3
        assertEquals(5.0, selfComposite.apply(3.0), 0.001);  // 3 + 2 = 5
    }

    @Test
    void testComplexNestedComposition() {
        // Сложная вложенная композиция

        // f(x) = x + 2
        MathFunction plusTwo = x -> x + 2;

        // g(x) = x * x (квадрат)
        MathFunction square = x -> x * x;

        // Создаем композитную функцию h(x) = g(f(x)) = (x + 2)²
        CompositeFunction composite1 = new CompositeFunction(plusTwo, square);

        // Создаем еще одну композитную функцию k(x) = h(h(x))
        CompositeFunction composite2 = new CompositeFunction(composite1, composite1);

        // k(1) = h(h(1)) = h((1 + 2)²) = h(9) = (9 + 2)² = 121
        assertEquals(121.0, composite2.apply(1.0), 0.001);
    }

    @Test
    void testNullConstructorParameters() {
        MathFunction validFunction = new IdentityFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new CompositeFunction(null, validFunction);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new CompositeFunction(validFunction, null);
        });
    }

    @Test
    void testGetters() {
        IdentityFunction first = new IdentityFunction();

        // f(x) = x * 4
        MathFunction second = x -> x * 4;

        CompositeFunction composite = new CompositeFunction(first, second);

        assertSame(first, composite.getFirstFunction());
        assertEquals(8.0, composite.getSecondFunction().apply(2.0), 0.001);
    }

    @Test
    void testGettersWithDifferentFunctionTypes() {
        // Тест геттеров с различными типами функций
        SqrFunction sqr = new SqrFunction();
        ConstantFunction constant = new ConstantFunction(5.0);
        ZeroFunction zero = new ZeroFunction();

        // Тест CompositeFunction(SqrFunction, ConstantFunction)
        CompositeFunction composite1 = new CompositeFunction(sqr, constant);
        assertTrue(composite1.getFirstFunction() instanceof SqrFunction);
        assertTrue(composite1.getSecondFunction() instanceof ConstantFunction);

        // Тест CompositeFunction(ConstantFunction, ZeroFunction)
        CompositeFunction composite2 = new CompositeFunction(constant, zero);
        assertTrue(composite2.getFirstFunction() instanceof ConstantFunction);
        assertTrue(composite2.getSecondFunction() instanceof ZeroFunction);

        // Тест применения функций через геттеры
        assertEquals(25.0, composite1.getFirstFunction().apply(5.0), 0.001); // sqr(5) = 25
        assertEquals(5.0, composite1.getSecondFunction().apply(25.0), 0.001);  // constant(25) = 5
        assertEquals(0.0, composite2.getSecondFunction().apply(10.0), 0.001);  // zero(10) = 0
    }

    @Test
    void testGettersWithComplexNestedFunctions() {
        // Тест геттеров с вложенными композитными функциями
        SqrFunction sqr = new SqrFunction();
        IdentityFunction identity = new IdentityFunction();

        // Создаем сложную функцию: (x)^2, затем применяем тождественную функцию
        CompositeFunction innerComposite = new CompositeFunction(identity, sqr);
        CompositeFunction outerComposite = new CompositeFunction(innerComposite, identity);

        // Проверяем, что геттеры возвращают правильные функции
        MathFunction firstFunction = outerComposite.getFirstFunction();
        MathFunction secondFunction = outerComposite.getSecondFunction();

        assertTrue(firstFunction instanceof CompositeFunction);
        assertTrue(secondFunction instanceof IdentityFunction);

        // Проверяем, что функции работают корректно
        assertEquals(16.0, firstFunction.apply(4.0), 0.001); // (4)^2 = 16
        assertEquals(16.0, secondFunction.apply(16.0), 0.001); // identity(16) = 16

        // Проверяем полную цепочку
        assertEquals(16.0, outerComposite.apply(4.0), 0.001); // identity(sqr(identity(4))) = identity(sqr(4)) = 16
    }

    @Test
    void testWithMultipleOperations() {
        // f(x) = x + 10
        MathFunction plusTen = x -> x + 10;

        // g(x) = x / 2
        MathFunction divideByTwo = x -> x / 2;

        // k(x) = x - 5
        MathFunction minusFive = x -> x - 5;

        // h(x) = k(g(f(x))) = ((x + 10) / 2) - 5
        CompositeFunction composite = new CompositeFunction(
                new CompositeFunction(plusTen, divideByTwo),
                minusFive
        );

        assertEquals(0.0, composite.apply(0.0), 0.001);   // ((0 + 10)/2) - 5 = 0
        assertEquals(2.5, composite.apply(5.0), 0.001);   // ((5 + 10)/2) - 5 = 2.5
    }
}