package operations;

import functions.MathFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для дифференциальных операторов
 */
public class SteppingDifferentialOperatorTest {

    @Test
    public void testLeftSteppingDifferentialOperator() {
        // Для функции f(x) = x^2 производная f'(x) = 2x
        // Левая разностная производная: (f(x) - f(x-h)) / h ≈ 2x - h (при малом h)
        MathFunction sqr = new SqrFunction();
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1.0);
        
        MathFunction derivative = operator.derive(sqr);
        
        // При x = 3, h = 1: (9 - 4) / 1 = 5
        // Точная производная 2*3 = 6, разностная 2*3 - 1 = 5
        assertEquals(5.0, derivative.apply(3.0), 0.001);
        
        // При x = 5, h = 1: (25 - 16) / 1 = 9
        // Точная производная 2*5 = 10, разностная 2*5 - 1 = 9
        assertEquals(9.0, derivative.apply(5.0), 0.001);
    }

    @Test
    public void testRightSteppingDifferentialOperator() {
        // Для функции f(x) = x^2 производная f'(x) = 2x
        // Правая разностная производная: (f(x+h) - f(x)) / h ≈ 2x + h (при малом h)
        MathFunction sqr = new SqrFunction();
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(1.0);
        
        MathFunction derivative = operator.derive(sqr);
        
        // При x = 3, h = 1: (16 - 9) / 1 = 7
        // Точная производная 2*3 = 6, разностная 2*3 + 1 = 7
        assertEquals(7.0, derivative.apply(3.0), 0.001);
        
        // При x = 5, h = 1: (36 - 25) / 1 = 11
        // Точная производная 2*5 = 10, разностная 2*5 + 1 = 11
        assertEquals(11.0, derivative.apply(5.0), 0.001);
    }

    @Test
    public void testMiddleSteppingDifferentialOperator() {
        // Для функции f(x) = x^2 производная f'(x) = 2x
        // Средняя разностная производная: (f(x+h) - f(x-h)) / (2h) ≈ 2x (точная при квадратичной функции!)
        MathFunction sqr = new SqrFunction();
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(1.0);
        
        MathFunction derivative = operator.derive(sqr);
        
        // При x = 3, h = 1: (16 - 4) / 2 = 6
        // Точная производная 2*3 = 6
        assertEquals(6.0, derivative.apply(3.0), 0.001);
        
        // При x = 5, h = 1: (36 - 16) / 2 = 10
        // Точная производная 2*5 = 10
        assertEquals(10.0, derivative.apply(5.0), 0.001);
    }

    @Test
    public void testSteppingDifferentialOperator_SmallStep() {
        MathFunction sqr = new SqrFunction();
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.001);
        
        MathFunction derivative = operator.derive(sqr);
        
        // При малом шаге приближение должно быть точнее
        // При x = 2, точная производная = 4
        assertEquals(4.0, derivative.apply(2.0), 0.01);
    }

    @Test
    public void testGetterSetter() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1.0);
        
        assertEquals(1.0, operator.getStep(), 0.001);
        
        operator.setStep(0.5);
        assertEquals(0.5, operator.getStep(), 0.001);
    }

    @Test
    public void testConstructor_InvalidStep_Negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(-1.0);
        });
    }

    @Test
    public void testConstructor_InvalidStep_Zero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RightSteppingDifferentialOperator(0.0);
        });
    }

    @Test
    public void testConstructor_InvalidStep_PositiveInfinity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MiddleSteppingDifferentialOperator(Double.POSITIVE_INFINITY);
        });
    }

    @Test
    public void testConstructor_InvalidStep_NaN() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.NaN);
        });
    }

    @Test
    public void testSetStep_Invalid() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(-0.5);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(0.0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(Double.POSITIVE_INFINITY);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(Double.NaN);
        });
    }

    @Test
    public void testDerivativeOfConstant() {
        // Производная от константы = 0
        MathFunction constant = x -> 5.0;
        
        LeftSteppingDifferentialOperator leftOp = new LeftSteppingDifferentialOperator(1.0);
        MathFunction leftDerivative = leftOp.derive(constant);
        assertEquals(0.0, leftDerivative.apply(10.0), 0.001);
        
        RightSteppingDifferentialOperator rightOp = new RightSteppingDifferentialOperator(1.0);
        MathFunction rightDerivative = rightOp.derive(constant);
        assertEquals(0.0, rightDerivative.apply(10.0), 0.001);
        
        MiddleSteppingDifferentialOperator middleOp = new MiddleSteppingDifferentialOperator(1.0);
        MathFunction middleDerivative = middleOp.derive(constant);
        assertEquals(0.0, middleDerivative.apply(10.0), 0.001);
    }

    @Test
    public void testDerivativeOfLinear() {
        // Производная от f(x) = 2x равна 2
        MathFunction linear = x -> 2 * x;
        
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.1);
        MathFunction derivative = operator.derive(linear);
        
        // Средняя разностная производная точна для линейных функций
        assertEquals(2.0, derivative.apply(5.0), 0.001);
        assertEquals(2.0, derivative.apply(10.0), 0.001);
    }
}

