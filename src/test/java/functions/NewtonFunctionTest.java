package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для NewtonFunction
 */
class NewtonFunctionTest {

    @Test
    void testNewtonMethodForQuadraticEquation() {
        // Тест решения уравнения x^2 - 4 = 0 (корни: x = ±2)
        MathFunction quadratic = x -> x * x - 4; // f(x) = x^2 - 4
        MathFunction derivative = x -> 2 * x;    // f'(x) = 2x

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 1.0, 0.001);
        
        double root = newton.getRoot();
        assertTrue(Math.abs(root - 2.0) < 0.01 || Math.abs(root + 2.0) < 0.01);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(0.0, quadratic.apply(root), 0.01);
    }

    @Test
    void testNewtonMethodForCubicEquation() {
        // Тест решения уравнения x^3 - 8 = 0 (корень: x = 2)
        MathFunction cubic = x -> x * x * x - 8; // f(x) = x^3 - 8
        MathFunction derivative = x -> 3 * x * x; // f'(x) = 3x^2

        NewtonFunction newton = new NewtonFunction(cubic, derivative, 1.0, 0.001);
        
        double root = newton.getRoot();
        assertEquals(2.0, root, 0.01);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(0.0, cubic.apply(root), 0.01);
    }

    @Test
    void testNewtonMethodWithTarget() {
        // Тест решения уравнения x^2 = 9 (корни: x = ±3)
        MathFunction quadratic = x -> x * x;     // f(x) = x^2
        MathFunction derivative = x -> 2 * x;    // f'(x) = 2x

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 9.0, 1.0, 0.001);
        
        double root = newton.getRoot();
        assertTrue(Math.abs(root - 3.0) < 0.01 || Math.abs(root + 3.0) < 0.01);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(9.0, quadratic.apply(root), 0.01);
    }

    @Test
    void testNewtonMethodForLinearEquation() {
        // Тест решения уравнения 2x - 6 = 0 (корень: x = 3)
        MathFunction linear = x -> 2 * x - 6;    // f(x) = 2x - 6
        MathFunction derivative = x -> 2.0;      // f'(x) = 2

        NewtonFunction newton = new NewtonFunction(linear, derivative, 1.0, 0.001);
        
        double root = newton.getRoot();
        assertEquals(3.0, root, 0.001);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(0.0, linear.apply(root), 0.001);
    }

    @Test
    void testNewtonMethodForExponentialEquation() {
        // Тест решения уравнения e^x - 2 = 0 (корень: x = ln(2) ≈ 0.693)
        MathFunction exponential = x -> Math.exp(x) - 2; // f(x) = e^x - 2
        MathFunction derivative = x -> Math.exp(x);      // f'(x) = e^x

        NewtonFunction newton = new NewtonFunction(exponential, derivative, 0.0, 0.001);
        
        double root = newton.getRoot();
        assertEquals(Math.log(2), root, 0.01);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(0.0, exponential.apply(root), 0.01);
    }

    @Test
    void testNewtonMethodWithDifferentInitialGuesses() {
        // Тест с разными начальными приближениями
        MathFunction quadratic = x -> x * x - 4; // f(x) = x^2 - 4
        MathFunction derivative = x -> 2 * x;    // f'(x) = 2x

        // Начальное приближение близко к положительному корню
        NewtonFunction newton1 = new NewtonFunction(quadratic, derivative, 3.0, 0.001);
        double root1 = newton1.getRoot();
        assertTrue(Math.abs(root1 - 2.0) < 0.01);

        // Начальное приближение близко к отрицательному корню
        NewtonFunction newton2 = new NewtonFunction(quadratic, derivative, -3.0, 0.001);
        double root2 = newton2.getRoot();
        assertTrue(Math.abs(root2 + 2.0) < 0.01);
    }

    @Test
    void testNewtonMethodWithHighTolerance() {
        // Тест с высокой точностью
        MathFunction quadratic = x -> x * x - 4; // f(x) = x^2 - 4
        MathFunction derivative = x -> 2 * x;    // f'(x) = 2x

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 1.0, 1e-10);
        
        double root = newton.getRoot();
        assertTrue(Math.abs(root - 2.0) < 1e-8 || Math.abs(root + 2.0) < 1e-8);
        
        // Проверяем высокую точность
        assertEquals(0.0, quadratic.apply(root), 1e-8);
    }

    @Test
    void testNewtonMethodWithZeroDerivative() {
        // Тест с нулевой производной (должен выбросить исключение)
        MathFunction constant = x -> 5.0;        // f(x) = 5 (константа)
        MathFunction zeroDerivative = x -> 0.0;  // f'(x) = 0

        assertThrows(ArithmeticException.class, () -> {
            new NewtonFunction(constant, zeroDerivative, 1.0, 0.001);
        });
    }

    @Test
    void testNewtonMethodWithNoConvergence() {
        // Тест с функцией, которая не сходится (должен выбросить исключение)
        // Используем функцию, которая имеет точку с нулевой производной
        MathFunction problematic = x -> x * x * x; // f(x) = x^3
        MathFunction derivative = x -> 3 * x * x;  // f'(x) = 3x^2

        // Начиная с x = 0, производная равна 0, что вызовет исключение
        assertThrows(ArithmeticException.class, () -> {
            new NewtonFunction(problematic, derivative, 0.0, 1e-10);
        });
    }

    @Test
    void testNewtonFunctionAsMathFunction() {
        // Тест, что NewtonFunction реализует MathFunction
        MathFunction quadratic = x -> x * x - 4;
        MathFunction derivative = x -> 2 * x;

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 1.0, 0.001);
        
        assertTrue(newton instanceof MathFunction);
        
        // Тест через интерфейс MathFunction
        MathFunction func = newton;
        double root = func.apply(0.0); // apply должен возвращать найденный корень
        assertTrue(Math.abs(root - 2.0) < 0.01 || Math.abs(root + 2.0) < 0.01);
    }

    @Test
    void testGetters() {
        MathFunction quadratic = x -> x * x - 4;
        MathFunction derivative = x -> 2 * x;

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 1.0, 0.001);
        
        assertEquals(quadratic, newton.getFunction());
        assertEquals(derivative, newton.getDerivative());
        assertTrue(Math.abs(newton.getRoot() - 2.0) < 0.01 || Math.abs(newton.getRoot() + 2.0) < 0.01);
    }

    @Test
    void testNewtonMethodWithTargetConstructor() {
        // Тест конструктора с целевым значением
        MathFunction quadratic = x -> x * x;
        MathFunction derivative = x -> 2 * x;

        NewtonFunction newton = new NewtonFunction(quadratic, derivative, 16.0, 2.0, 0.001);
        
        double root = newton.getRoot();
        assertTrue(Math.abs(root - 4.0) < 0.01 || Math.abs(root + 4.0) < 0.01);
        
        // Проверяем, что найденный корень действительно является решением
        assertEquals(16.0, quadratic.apply(root), 0.01);
    }
}