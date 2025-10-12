package operations;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для TabulatedFunctionOperationService
 */
public class TabulatedFunctionOperationServiceTest {

    @Test
    public void testAsPoints_ArrayTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        
        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 0.001);
            assertEquals(yValues[i], points[i].y, 0.001);
        }
    }

    @Test
    public void testAsPoints_LinkedListTabulatedFunction() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 8.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        
        // LinkedListTabulatedFunction пока выбрасывает UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            TabulatedFunctionOperationService.asPoints(function);
        });
    }

    @Test
    public void testAsPoints_TwoPoints() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        
        assertEquals(2, points.length);
        assertEquals(1.0, points[0].x, 0.001);
        assertEquals(10.0, points[0].y, 0.001);
        assertEquals(2.0, points[1].x, 0.001);
        assertEquals(20.0, points[1].y, 0.001);
    }

    @Test
    public void testAsPoints_CheckPointsAreDistinct() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {5.0, 10.0, 15.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        
        // Проверяем, что каждый объект Point создан отдельно
        assertNotNull(points[0]);
        assertNotNull(points[1]);
        assertNotNull(points[2]);
        assertNotSame(points[0], points[1]);
        assertNotSame(points[1], points[2]);
    }
}

