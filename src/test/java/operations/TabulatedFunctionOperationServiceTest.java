package operations;

import exceptions.InconsistentFunctionsException;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
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

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 0.001);
            assertEquals(yValues[i], points[i].y, 0.001);
        }
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

    @Test
    public void testAdd_ArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 4.0, 6.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues, yValues2);
        
        TabulatedFunction result = service.add(func1, func2);
        
        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0), 0.001);
        assertEquals(6.0, result.getY(1), 0.001);
        assertEquals(9.0, result.getY(2), 0.001);
        assertTrue(result instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testAdd_LinkedListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());
        
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 4.0, 6.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues, yValues2);
        
        TabulatedFunction result = service.add(func1, func2);
        
        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0), 0.001);
        assertEquals(6.0, result.getY(1), 0.001);
        assertEquals(9.0, result.getY(2), 0.001);
        assertTrue(result instanceof LinkedListTabulatedFunction);
    }

    @Test
    public void testSubtract_ArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {5.0, 10.0, 15.0};
        double[] yValues2 = {2.0, 3.0, 5.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues, yValues2);
        
        TabulatedFunction result = service.subtract(func1, func2);
        
        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0), 0.001);
        assertEquals(7.0, result.getY(1), 0.001);
        assertEquals(10.0, result.getY(2), 0.001);
    }

    @Test
    public void testAdd_DifferentTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        double[] yValues2 = {4.0, 5.0, 6.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues, yValues2);
        
        TabulatedFunction result = service.add(func1, func2);
        
        assertEquals(5.0, result.getY(0), 0.001);
        assertEquals(7.0, result.getY(1), 0.001);
        assertEquals(9.0, result.getY(2), 0.001);
        assertTrue(result instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testAdd_InconsistentFunctions_DifferentCount() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        double[] xValues2 = {1.0, 2.0};
        double[] yValues2 = {1.0, 2.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);
        
        assertThrows(InconsistentFunctionsException.class, () -> {
            service.add(func1, func2);
        });
    }

    @Test
    public void testAdd_InconsistentFunctions_DifferentX() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        double[] xValues2 = {1.0, 2.5, 3.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);
        
        assertThrows(InconsistentFunctionsException.class, () -> {
            service.add(func1, func2);
        });
    }

    @Test
    public void testGetterSetter() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        assertTrue(service.getFactory() instanceof ArrayTabulatedFunctionFactory);
        
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        assertTrue(service.getFactory() instanceof LinkedListTabulatedFunctionFactory);
    }
}

