package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для комбинаций табулированных функций
 */
class TabulatedFunctionCombinationsTest {

    @Test
    void testArrayTabulatedFunctionWithIdentityFunction() {
        IdentityFunction identity = new IdentityFunction();
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(identity, 0.0, 4.0, 5);

        assertEquals(0.0, tabulated.apply(0.0), 0.001);
        assertEquals(1.0, tabulated.apply(1.0), 0.001);
        assertEquals(2.0, tabulated.apply(2.0), 0.001);
        assertEquals(3.0, tabulated.apply(3.0), 0.001);
        assertEquals(4.0, tabulated.apply(4.0), 0.001);
        assertEquals(1.5, tabulated.apply(1.5), 0.001);
    }

    @Test
    void testArrayTabulatedFunctionWithSqrFunction() {
        SqrFunction sqr = new SqrFunction();
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(sqr, 0.0, 4.0, 5);

        assertEquals(0.0, tabulated.apply(0.0), 0.001);
        assertEquals(1.0, tabulated.apply(1.0), 0.001);
        assertEquals(4.0, tabulated.apply(2.0), 0.001);
        assertEquals(9.0, tabulated.apply(3.0), 0.001);
        assertEquals(16.0, tabulated.apply(4.0), 0.001);
        assertEquals(2.5, tabulated.apply(1.5), 0.001);
    }

    @Test
    void testLinkedListTabulatedFunctionWithIdentityFunction() {
        IdentityFunction identity = new IdentityFunction();
        LinkedListTabulatedFunction tabulated = new LinkedListTabulatedFunction(identity, 0.0, 4.0, 5);

        assertEquals(0.0, tabulated.apply(0.0), 0.001);
        assertEquals(1.0, tabulated.apply(1.0), 0.001);
        assertEquals(2.0, tabulated.apply(2.0), 0.001);
        assertEquals(3.0, tabulated.apply(3.0), 0.001);
        assertEquals(4.0, tabulated.apply(4.0), 0.001);
        assertEquals(1.5, tabulated.apply(1.5), 0.001);
    }

    @Test
    void testCompositeFunctionWithTabulatedFunctions() {
        IdentityFunction identity = new IdentityFunction();
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(identity, 0.0, 4.0, 5);
        SqrFunction sqr = new SqrFunction();

        CompositeFunction composite = new CompositeFunction(tabulated, sqr);

        assertEquals(0.0, composite.apply(0.0), 0.001);
        assertEquals(1.0, composite.apply(1.0), 0.001);
        assertEquals(4.0, composite.apply(2.0), 0.001);
        assertEquals(9.0, composite.apply(3.0), 0.001);
        assertEquals(16.0, composite.apply(4.0), 0.001);
    }

    @Test
    void testTabulatedFunctionWithAndThenMethod() {
        IdentityFunction identity = new IdentityFunction();
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(identity, 0.0, 4.0, 5);
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = tabulated.andThen(sqr);

        assertEquals(0.0, composite.apply(0.0), 0.001);
        assertEquals(1.0, composite.apply(1.0), 0.001);
        assertEquals(4.0, composite.apply(2.0), 0.001);
        assertEquals(9.0, composite.apply(3.0), 0.001);
        assertEquals(16.0, composite.apply(4.0), 0.001);
    }
}