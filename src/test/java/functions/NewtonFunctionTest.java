package functions;

public class NewtonFunctionTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование NewtonFunction ===");

        testQuadraticEquation();
        testCubicEquation();
        testTargetValue();

        System.out.println("\n✓ Все тесты NewtonFunction пройдены успешно!");
    }

    private static void testQuadraticEquation() {
        System.out.println("\n--- Тест квадратного уравнения x² - 4 = 0 ---");

        // Функция f(x) = x² - 4
        MathFunction quadratic = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * x - 4;
            }
        };

        // Производная f'(x) = 2x
        MathFunction derivative = new MathFunction() {
            @Override
            public double apply(double x) {
                return 2 * x;
            }
        };

        // Находим корень x² - 4 = 0 (ожидаемый корень: 2)
        NewtonFunction newtonFunction = new NewtonFunction(quadratic, derivative, 1.0, 1e-10);

        double root = newtonFunction.getRoot();
        double expectedRoot = 2.0;

        assert Math.abs(root - expectedRoot) < 1e-10 : "Неверный корень: " + root + ", ожидалось: " + expectedRoot;

        // Проверяем, что функция возвращает корень для любого x
        assert newtonFunction.apply(0) == root : "Функция должна возвращать корень";
        assert newtonFunction.apply(10) == root : "Функция должна возвращать корень";
        assert newtonFunction.apply(-5) == root : "Функция должна возвращать корень";

        System.out.println("✓ Корень найден: " + root);
    }

    private static void testCubicEquation() {
        System.out.println("\n--- Тест кубического уравнения x³ - 8 = 0 ---");

        // Функция f(x) = x³ - 8
        MathFunction cubic = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * x * x - 8;
            }
        };

        // Производная f'(x) = 3x²
        MathFunction derivative = new MathFunction() {
            @Override
            public double apply(double x) {
                return 3 * x * x;
            }
        };

        // Находим корень x³ - 8 = 0 (ожидаемый корень: 2)
        NewtonFunction newtonFunction = new NewtonFunction(cubic, derivative, 1.0, 1e-10);

        double root = newtonFunction.getRoot();
        double expectedRoot = 2.0;

        assert Math.abs(root - expectedRoot) < 1e-10 : "Неверный корень: " + root + ", ожидалось: " + expectedRoot;

        System.out.println("✓ Корень найден: " + root);
    }

    private static void testTargetValue() {
        System.out.println("\n--- Тест поиска x для f(x) = target ---");

        // Функция f(x) = x²
        MathFunction square = new SqrFunction();

        // Производная f'(x) = 2x
        MathFunction derivative = new MathFunction() {
            @Override
            public double apply(double x) {
                return 2 * x;
            }
        };

        // Находим x такое, что x² = 9, т.е. x = 3
        NewtonFunction newtonFunction = new NewtonFunction(square, derivative, 9.0, 1.0, 1e-10);

        double root = newtonFunction.getRoot();
        double expectedRoot = 3.0;

        assert Math.abs(root - expectedRoot) < 1e-10 : "Неверный корень: " + root + ", ожидалось: " + expectedRoot;

        // Проверяем, что x² = 9
        double squareResult = square.apply(root);
        assert Math.abs(squareResult - 9.0) < 1e-10 : "x² != 9: " + squareResult;

        System.out.println("✓ Корень найден: " + root + ", f(" + root + ") = " + squareResult);
    }
}
