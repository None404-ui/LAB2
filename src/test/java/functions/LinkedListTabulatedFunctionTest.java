package functions;

public class LinkedListTabulatedFunctionTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование LinkedListTabulatedFunction ===");

        testConstructorWithArrays();
        testConstructorWithFunction();
        testInsertMethod();
        testInterpolation();
        testExtrapolation();
        testApplyMethod();

        System.out.println("\n✓ Все тесты LinkedListTabulatedFunction пройдены успешно!");
    }

    private static void testConstructorWithArrays() {
        System.out.println("\n--- Тест конструктора с массивами ---");

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

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
        LinkedListTabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(sqrFunction, 0.0, 4.0, 5);

        assert tabulatedFunction.getCount() == 5 : "Неверное количество точек";
        assert tabulatedFunction.getX(0) == 0.0 : "Неверное значение x[0]";
        assert tabulatedFunction.getX(4) == 4.0 : "Неверное значение x[4]";
        assert tabulatedFunction.getY(0) == 0.0 : "Неверное значение y[0] (0^2 = 0)";
        assert tabulatedFunction.getY(2) == 4.0 : "Неверное значение y[2] (2^2 = 4)";
        assert tabulatedFunction.getY(4) == 16.0 : "Неверное значение y[4] (4^2 = 16)";

        System.out.println("✓ Конструктор с функцией работает корректно");
    }

    private static void testInsertMethod() {
        System.out.println("\n--- Тест метода insert() ---");

        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Вставка в середину
        function.insert(2.0, 4.0);
        assert function.getCount() == 4 : "Неверное количество после вставки";
        assert function.getX(1) == 2.0 : "Неверное значение x после вставки";
        assert function.getY(1) == 4.0 : "Неверное значение y после вставки";

        // Замена существующего значения
        function.insert(2.0, 8.0);
        assert function.getCount() == 4 : "Количество не должно измениться при замене";
        assert function.getY(1) == 8.0 : "Значение должно быть заменено";

        // Вставка в начало
        function.insert(0.5, 0.25);
        assert function.getCount() == 5 : "Неверное количество после вставки в начало";
        assert function.getX(0) == 0.5 : "Неверное значение x в начале";
        assert function.getY(0) == 0.25 : "Неверное значение y в начале";

        // Вставка в конец
        function.insert(6.0, 36.0);
        assert function.getCount() == 6 : "Неверное количество после вставки в конец";
        assert function.getX(5) == 6.0 : "Неверное значение x в конце";
        assert function.getY(5) == 36.0 : "Неверное значение y в конце";

        System.out.println("✓ Метод insert() работает корректно");
    }

    private static void testInterpolation() {
        System.out.println("\n--- Тест интерполяции ---");

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0}; // y = x^2

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

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

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

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

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

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

