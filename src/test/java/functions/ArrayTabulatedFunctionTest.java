package functions;

public class ArrayTabulatedFunctionTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование ArrayTabulatedFunction ===");

        testConstructorWithArrays();
        testConstructorWithFunction();
        testInterpolation();
        testExtrapolation();
        testApplyMethod();

        System.out.println("\n✓ Все тесты ArrayTabulatedFunction пройдены успешно!");
    }

    private static void testConstructorWithArrays() {
        System.out.println("\n--- Тест конструктора с массивами ---");

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assert function.getCount() == 4 : "Неверное количество точек";
        assert function.getX(0) == 1.0 : "Неверное значение x[0]";
        assert function.getX(3) == 4.0 : "Неверное значение x[3]";
        assert function.getY(0) == 1.0 : "Неверное значение y[0]";
        assert function.getY(3) == 16.0 : "Неверное значение y[3]";

        System.out.println("✓ Конструктор с массивами работает корректно");
    }

    private static void testConstructorWithFunction() {
        System.out.println("\n--- Тест конструктора с функцией ---");

        SqrFunction sqrFunction = new SqrFunction();
        ArrayTabulatedFunction tabulatedFunction = new ArrayTabulatedFunction(sqrFunction, 0.0, 4.0, 5);

        assert tabulatedFunction.getCount() == 5 : "Неверное количество точек";
        assert tabulatedFunction.getX(0) == 0.0 : "Неверное значение x[0]";
        assert tabulatedFunction.getX(4) == 4.0 : "Неверное значение x[4]";
        assert tabulatedFunction.getY(0) == 0.0 : "Неверное значение y[0] (0^2 = 0)";
        assert tabulatedFunction.getY(2) == 4.0 : "Неверное значение y[2] (2^2 = 4)";
        assert tabulatedFunction.getY(4) == 16.0 : "Неверное значение y[4] (4^2 = 16)";

        System.out.println("✓ Конструктор с функцией работает корректно");
    }

    private static void testInterpolation() {
        System.out.println("\n--- Тест интерполяции ---");

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0}; // y = x^2

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Точка внутри интервала (1, 3)
        double result = function.apply(1.5);
        double expected = 2.25; // (1.5)^2 = 2.25
        assert Math.abs(result - expected) < 0.001 : "Неверная интерполяция: " + result + " != " + expected;

        // Точка в узле таблицы
        double result2 = function.apply(2.0);
        assert result2 == 4.0 : "Неверное значение в узле: " + result2;

        System.out.println("✓ Интерполяция работает корректно");
    }

    private static void testExtrapolation() {
        System.out.println("\n--- Тест экстраполяции ---");

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0}; // y = x^2

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Экстраполяция слева (x < 1.0)
        double resultLeft = function.apply(0.0);
        double expectedLeft = 0.0; // Продолжение линии от (1,1) к (2,4)
        assert Math.abs(resultLeft - expectedLeft) < 0.001 : "Неверная экстраполяция слева";

        // Экстраполяция справа (x > 3.0)
        double resultRight = function.apply(4.0);
        double expectedRight = 16.0; // Продолжение линии от (2,4) к (3,9)
        assert Math.abs(resultRight - expectedRight) < 0.001 : "Неверная экстраполяция справа";

        System.out.println("✓ Экстраполяция работает корректно");
    }

    private static void testApplyMethod() {
        System.out.println("\n--- Тест метода apply() ---");

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0}; // y = x^2

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Точки в таблице
        assert function.apply(0.0) == 0.0 : "Неверное значение для x=0.0";
        assert function.apply(1.0) == 1.0 : "Неверное значение для x=1.0";
        assert function.apply(2.0) == 4.0 : "Неверное значение для x=2.0";

        // Интерполяция
        double result = function.apply(0.5);
        assert Math.abs(result - 0.25) < 0.001 : "Неверная интерполяция для x=0.5";

        // Экстраполяция слева
        double resultLeft = function.apply(-1.0);
        assert Math.abs(resultLeft - (-1.0)) < 0.001 : "Неверная экстраполяция слева";

        // Экстраполяция справа
        double resultRight = function.apply(3.0);
        assert Math.abs(resultRight - 9.0) < 0.001 : "Неверная экстраполяция справа";

        System.out.println("✓ Метод apply() работает корректно");
    }
}

