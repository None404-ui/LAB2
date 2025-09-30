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

    private static void testInsertMethod() {

        // создаем начальную функцию с 3 точками
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0}; // y = x^2
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставка новой точки в середину
        function.insert(2.0, 4.0);

        assert function.getCount() == 4 ;
        assert function.getX(1) == 2.0 ;
        assert function.getY(1) == 4.0 ;

        // Проверяем порядок x значений
        assert function.getX(0) == 1.0 ;
        assert function.getX(1) == 2.0;
        assert function.getX(2) == 3.0 ;
        assert function.getX(3) == 5.0 ;


        // Вставка новой точки в начало
        function.insert(0.0, 0.0);

        assert function.getCount() == 5;
        assert function.getX(0) == 0.0 ;
        assert function.getY(0) == 0.0 ;


        //  Вставка новой точки в конец
        function.insert(6.0, 36.0);

        assert function.getCount() == 6 ;
        assert function.getX(5) == 6.0 ;
        assert function.getY(5) == 36.0 ;


        //  Обновление существующей точки (x уже есть в массиве)
        int initialCount = function.getCount();
        double initialY = function.getY(2); // y для x=3.0 должно быть 9.0

        function.insert(3.0, 100.0); // Обновляем значение для существующего x

        assert function.getCount() == initialCount ;
        assert function.getY(2) == 100.0 ;
        assert function.getY(2) != initialY ;


        //  Проверка сохранения порядка после множественных вставок
        double[] xValues2 = {1.0, 5.0};
        double[] yValues2 = {1.0, 25.0};
        ArrayTabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        function2.insert(3.0, 9.0);
        function2.insert(2.0, 4.0);
        function2.insert(4.0, 16.0);

        assert function2.getCount() == 5 : "Неверное количество точек после множественных вставок";

        // Проверяем, что все x упорядочены по возрастанию
        for (int i = 1; i < function2.getCount(); i++) {
            assert function2.getX(i) > function2.getX(i - 1) :
                    "Нарушен порядок x значений после множественных вставок: " +
                            function2.getX(i - 1) + " >= " + function2.getX(i);
        }

        // Проверяем конкретные значения
        assert function2.getX(0) == 1.0;
        assert function2.getX(1) == 2.0 ;
        assert function2.getX(2) == 3.0 ;
        assert function2.getX(3) == 4.0 ;
        assert function2.getX(4) == 5.0 ;

        assert function2.getY(0) == 1.0 ;
        assert function2.getY(1) == 4.0 ;
        assert function2.getY(2) == 9.0 ;
        assert function2.getY(3) == 16.0 ;
        assert function2.getY(4) == 25.0 ;


    }
}

