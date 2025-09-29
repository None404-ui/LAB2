package functions;

public class TabulatedFunctionCombinationsTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование комбинаций табулированных функций ===");

        testTabulatedWithRegularFunction();
        testTabulatedWithTabulatedFunction();
        testComplexComposition();

        System.out.println("\n✓ Все тесты комбинаций функций пройдены успешно!");
    }

    private static void testTabulatedWithRegularFunction() {
        System.out.println("\n--- Тест комбинации табулированной и обычной функции ---");

        // Создаем табулированную функцию y = x^2 на интервале [0, 4]
        SqrFunction sqrFunction = new SqrFunction();
        ArrayTabulatedFunction tabulatedSqr = new ArrayTabulatedFunction(sqrFunction, 0.0, 4.0, 5);

        // Создаем константную функцию y = 2
        ConstantFunction constFunction = new ConstantFunction(2.0);

        // Композиция: const(sqr(x)) = 2 для всех x
        MathFunction composition = tabulatedSqr.andThen(constFunction);

        // Тестируем результат
        assert composition.apply(0.0) == 2.0 : "Ошибка в композиции с x=0.0";
        assert composition.apply(1.0) == 2.0 : "Ошибка в композиции с x=1.0";
        assert composition.apply(2.0) == 2.0 : "Ошибка в композиции с x=2.0";
        assert composition.apply(4.0) == 2.0 : "Ошибка в композиции с x=4.0";

        System.out.println("✓ Комбинация табулированной и обычной функции работает корректно");
    }

    private static void testTabulatedWithTabulatedFunction() {
        System.out.println("\n--- Тест комбинации двух табулированных функций ---");

        // Создаем первую табулированную функцию y = x^2
        double[] x1 = {0.0, 1.0, 2.0, 3.0};
        double[] y1 = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction tabulatedSqr = new ArrayTabulatedFunction(x1, y1);

        // Создаем вторую табулированную функцию y = x + 1
        double[] x2 = {0.0, 1.0, 2.0, 3.0};
        double[] y2 = {1.0, 2.0, 3.0, 4.0};
        ArrayTabulatedFunction tabulatedPlus1 = new ArrayTabulatedFunction(x2, y2);

        // Композиция: (x+1)^2
        MathFunction composition = tabulatedSqr.andThen(tabulatedPlus1);

        // Тестируем результат
        assert Math.abs(composition.apply(0.0) - 1.0) < 0.001 : "Ошибка в композиции с x=0.0";
        assert Math.abs(composition.apply(1.0) - 4.0) < 0.001 : "Ошибка в композиции с x=1.0";
        assert Math.abs(composition.apply(2.0) - 9.0) < 0.001 : "Ошибка в композиции с x=2.0";

        System.out.println("✓ Комбинация двух табулированных функций работает корректно");
    }

    private static void testComplexComposition() {
        System.out.println("\n--- Тест сложной композиции функций ---");

        // Создаем исходную функцию y = x^2
        SqrFunction sqrFunction = new SqrFunction();
        ArrayTabulatedFunction tabulatedSqr = new ArrayTabulatedFunction(sqrFunction, 0.0, 2.0, 3);

        // Создаем функцию y = x + 1
        MathFunction plus1 = new MathFunction() {
            @Override
            public double apply(double x) {
                return x + 1;
            }
        };

        // Композиция: (x^2 + 1)
        MathFunction composition = tabulatedSqr.andThen(plus1);

        // Тестируем результат
        assert Math.abs(composition.apply(0.0) - 1.0) < 0.001 : "Ошибка в композиции с x=0.0";
        assert Math.abs(composition.apply(1.0) - 2.0) < 0.001 : "Ошибка в композиции с x=1.0";
        assert Math.abs(composition.apply(2.0) - 5.0) < 0.001 : "Ошибка в композиции с x=2.0";

        System.out.println("✓ Сложная композиция функций работает корректно");
    }
}

