package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        CompositeFunction1 composite = new CompositeFunction1(plusOne, timesTwo);

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
        CompositeFunction1 composite = new CompositeFunction1(identity, timesThree);

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
        CompositeFunction1 firstLevel = new CompositeFunction1(plusOne, timesTwo);

        // Затем создаем k(g(f(x))) = ((x + 1) * 2) - 5
        CompositeFunction1 secondLevel = new CompositeFunction1(firstLevel, minusFive);

        assertEquals(-1.0, secondLevel.apply(1.0), 0.001);  // ((1 + 1) * 2) - 5 = -1
        assertEquals(1.0, secondLevel.apply(2.0), 0.001);   // ((2 + 1) * 2) - 5 = 1
    }

    @Test
    void testSelfComposition() {
        // Композиция функции с самой собой: h(x) = f(f(x))
        MathFunction plusOne = x -> x + 1;

        // f(f(x)) = (x + 1) + 1 = x + 2
        CompositeFunction1 selfComposite = new CompositeFunction1(plusOne, plusOne);

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
        CompositeFunction1 composite1 = new CompositeFunction1(plusTwo, square);

        // Создаем еще одну композитную функцию k(x) = h(h(x))
        CompositeFunction1 composite2 = new CompositeFunction1(composite1, composite1);

        // k(1) = h(h(1)) = h((1 + 2)²) = h(9) = (9 + 2)² = 121
        assertEquals(121.0, composite2.apply(1.0), 0.001);
    }

    @Test
    void testNullConstructorParameters() {
        MathFunction validFunction = new IdentityFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new CompositeFunction1(null, validFunction);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new CompositeFunction1(validFunction, null);
        });
    }

    @Test
    void testGetters() {
        IdentityFunction first = new IdentityFunction();

        // f(x) = x * 4
        MathFunction second = x -> x * 4;

        CompositeFunction1 composite = new CompositeFunction1(first, second);

        assertSame(first, composite.getFirstFunction());
        assertEquals(8.0, composite.getSecondFunction().apply(2.0), 0.001);
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