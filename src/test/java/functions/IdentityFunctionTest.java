package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса IdentityFunction
 */
class IdentityFunctionTest {

    @Test
    void testApply() {
        IdentityFunction identity = new IdentityFunction();
        
        // Тест с положительными числами
        assertEquals(0.0, identity.apply(0.0), 0.001);
        assertEquals(1.0, identity.apply(1.0), 0.001);
        assertEquals(5.5, identity.apply(5.5), 0.001);
        assertEquals(100.0, identity.apply(100.0), 0.001);
        
        // Тест с отрицательными числами
        assertEquals(-1.0, identity.apply(-1.0), 0.001);
        assertEquals(-5.5, identity.apply(-5.5), 0.001);
        assertEquals(-100.0, identity.apply(-100.0), 0.001);
        
        // Тест с дробными числами
        assertEquals(0.001, identity.apply(0.001), 0.0001);
        assertEquals(-0.001, identity.apply(-0.001), 0.0001);
        assertEquals(Math.PI, identity.apply(Math.PI), 0.001);
        assertEquals(Math.E, identity.apply(Math.E), 0.001);
    }

    @Test
    void testTolerance() {
        IdentityFunction identity = new IdentityFunction();
        
        // Тест с очень маленькими числами
        double smallValue = 1e-10;
        assertEquals(smallValue, identity.apply(smallValue), 1e-15);
        
        // Тест с очень большими числами
        double largeValue = 1e10;
        assertEquals(largeValue, identity.apply(largeValue), 1e5);
    }

    @Test
    void testSpecialValues() {
        IdentityFunction identity = new IdentityFunction();
        
        // Тест с NaN
        assertTrue(Double.isNaN(identity.apply(Double.NaN)));
        
        // Тест с положительной бесконечностью
        assertEquals(Double.POSITIVE_INFINITY, identity.apply(Double.POSITIVE_INFINITY));
        
        // Тест с отрицательной бесконечностью
        assertEquals(Double.NEGATIVE_INFINITY, identity.apply(Double.NEGATIVE_INFINITY));
    }
}

