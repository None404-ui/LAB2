package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для фабрик табулированных функций
 */
public class TabulatedFunctionFactoryTest {

    @Test
    public void testArrayTabulatedFunctionFactory() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        
        TabulatedFunction function = factory.create(xValues, yValues);
        
        // Проверяем, что созданный объект имеет правильный тип
        assertTrue(function instanceof ArrayTabulatedFunction);
        
        // Проверяем корректность данных
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 0.001);
        assertEquals(1.0, function.getY(0), 0.001);
    }

    @Test
    public void testLinkedListTabulatedFunctionFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        
        TabulatedFunction function = factory.create(xValues, yValues);
        
        // Проверяем, что созданный объект имеет правильный тип
        assertTrue(function instanceof LinkedListTabulatedFunction);
        
        // Проверяем корректность данных
        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0), 0.001);
        assertEquals(0.0, function.getY(0), 0.001);
    }
}

