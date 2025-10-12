package functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для метода iterator()
 */
public class IteratorTest {

    @Test
    public void testArrayTabulatedFunction_IteratorThrowsException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        assertThrows(UnsupportedOperationException.class, () -> {
            function.iterator();
        });
    }

    @Test
    public void testLinkedListTabulatedFunction_IteratorThrowsException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        
        assertThrows(UnsupportedOperationException.class, () -> {
            function.iterator();
        });
    }
}

