package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки исключений
 */
public class ExceptionsTest {

    @Test
    public void testCheckLengthIsTheSame_DifferentLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0};
        
        assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    public void testCheckLengthIsTheSame_SameLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        
        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    public void testCheckSorted_NotSorted() {
        double[] xValues = {1.0, 3.0, 2.0};
        
        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
    }

    @Test
    public void testCheckSorted_EqualElements() {
        double[] xValues = {1.0, 2.0, 2.0, 3.0};
        
        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
    }

    @Test
    public void testCheckSorted_Sorted() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        
        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
    }

    @Test
    public void testArrayTabulatedFunction_DifferentLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0};
        
        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testArrayTabulatedFunction_NotSorted() {
        double[] xValues = {1.0, 3.0, 2.0};
        double[] yValues = {1.0, 9.0, 4.0};
        
        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testLinkedListTabulatedFunction_DifferentLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0};
        
        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testLinkedListTabulatedFunction_NotSorted() {
        double[] xValues = {1.0, 3.0, 2.0};
        double[] yValues = {1.0, 9.0, 4.0};
        
        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    public void testInterpolationException_ArrayTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        // Тестируем, что интерполяция работает для значений внутри интервала
        assertDoesNotThrow(() -> {
            function.apply(1.5);
            function.apply(2.5);
        });
    }

    @Test
    public void testInterpolationException_LinkedListTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        
        // Тестируем, что интерполяция работает для значений внутри интервала
        assertDoesNotThrow(() -> {
            function.apply(1.5);
            function.apply(2.5);
        });
    }

    @Test
    public void testDifferentLengthOfArraysException_WithMessage() {
        DifferentLengthOfArraysException exception = new DifferentLengthOfArraysException("Test message");
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testDifferentLengthOfArraysException_NoMessage() {
        DifferentLengthOfArraysException exception = new DifferentLengthOfArraysException();
        assertNull(exception.getMessage());
    }

    @Test
    public void testArrayIsNotSortedException_WithMessage() {
        ArrayIsNotSortedException exception = new ArrayIsNotSortedException("Test message");
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testArrayIsNotSortedException_NoMessage() {
        ArrayIsNotSortedException exception = new ArrayIsNotSortedException();
        assertNull(exception.getMessage());
    }

    @Test
    public void testInterpolationException_WithMessage() {
        InterpolationException exception = new InterpolationException("Test message");
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testInterpolationException_NoMessage() {
        InterpolationException exception = new InterpolationException();
        assertNull(exception.getMessage());
    }
}

