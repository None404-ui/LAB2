package functions;

public class AndThenMethodTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование метода andThen ===");

        // Создаем различные функции для тестов
        SqrFunction sqr = new SqrFunction();           // f(x) = x²
        ConstantFunction const5 = new ConstantFunction(5);  // g(x) = 5
        UnitFunction unit = new UnitFunction();        // h(x) = 1
        ZeroFunction zero = new ZeroFunction();        // z(x) = 0
        ConstantFunction const2 = new ConstantFunction(2);  // k(x) = 2

        testSimpleComposition(sqr, const5);
        testChainComposition(sqr, const5, unit);
        testLongChain(sqr, const2, unit, zero);
        testDirectApplication();

        System.out.println("\n✓ Все тесты метода andThen пройдены успешно!");
    }

    private static void testSimpleComposition(SqrFunction sqr, ConstantFunction const5) {
        System.out.println("\n--- Тест простой композиции ---");

        // Тест: sqr.andThen(const5) должно давать: const5(sqr(x)) = const5(x²) = 5
        MathFunction composite1 = sqr.andThen(const5);
        double result1 = composite1.apply(3); // sqr(3) = 9, const5(9) = 5
        assert result1 == 5.0 : "Ошибка: sqr.andThen(const5).apply(3) должно быть 5, получили " + result1;
        System.out.println("✓ sqr.andThen(const5).apply(3) = " + result1);

        // Тест: const5.andThen(sqr) должно давать: sqr(const5(x)) = sqr(5) = 25
        MathFunction composite2 = const5.andThen(sqr);
        double result2 = composite2.apply(10); // const5(10) = 5, sqr(5) = 25
        assert result2 == 25.0 : "Ошибка: const5.andThen(sqr).apply(10) должно быть 25, получили " + result2;
        System.out.println("✓ const5.andThen(sqr).apply(10) = " + result2);

        System.out.println("✓ Простая композиция работает корректно");
    }

    private static void testChainComposition(SqrFunction sqr, ConstantFunction const5, UnitFunction unit) {
        System.out.println("\n--- Тест цепочки из 3 функций ---");

        // Тест: sqr.andThen(const5).andThen(unit) = unit(const5(sqr(x))) = unit(const5(x²)) = unit(5) = 1
        MathFunction composite = sqr.andThen(const5).andThen(unit);
        double result1 = composite.apply(7); // sqr(7) = 49, const5(49) = 5, unit(5) = 1
        assert result1 == 1.0 : "Ошибка: цепочка должна вернуть 1, получили " + result1;
        System.out.println("✓ sqr.andThen(const5).andThen(unit).apply(7) = " + result1);

        // Тест с другим порядком: unit.andThen(sqr).andThen(const5)
        MathFunction composite2 = unit.andThen(sqr).andThen(const5);
        double result2 = composite2.apply(-5); // unit(-5) = 1, sqr(1) = 1, const5(1) = 5
        assert result2 == 5.0 : "Ошибка: цепочка должна вернуть 5, получили " + result2;
        System.out.println("✓ unit.andThen(sqr).andThen(const5).apply(-5) = " + result2);

        System.out.println("✓ Цепочки из 3 функций работают корректно");
    }

    private static void testLongChain(SqrFunction sqr, ConstantFunction const2, UnitFunction unit, ZeroFunction zero) {
        System.out.println("\n--- Тест длинной цепочки из 4 функций ---");

        // Тест: const2.andThen(sqr).andThen(unit).andThen(zero)
        // const2(x) = 2, sqr(2) = 4, unit(4) = 1, zero(1) = 0
        MathFunction longChain = const2.andThen(sqr).andThen(unit).andThen(zero);
        double result = longChain.apply(999); // Независимо от входа, результат должен быть 0
        assert result == 0.0 : "Ошибка: длинная цепочка должна вернуть 0, получили " + result;
        System.out.println("✓ const2.andThen(sqr).andThen(unit).andThen(zero).apply(999) = " + result);

        System.out.println("✓ Длинная цепочка работает корректно");
    }

    private static void testDirectApplication() {
        System.out.println("\n--- Тест прямого применения без создания переменной ---");

        SqrFunction sqr = new SqrFunction();
        ConstantFunction const3 = new ConstantFunction(3);

        // Прямое применение: sqr.andThen(const3).apply(5)
        // sqr(5) = 25, const3(25) = 3
        double directResult = sqr.andThen(const3).apply(5);
        assert directResult == 3.0 : "Ошибка: прямое применение должно вернуть 3, получили " + directResult;
        System.out.println("✓ sqr.andThen(const3).apply(5) = " + directResult);

        // Более сложная прямая цепочка
        double complexResult = new SqrFunction()
                .andThen(new ConstantFunction(10))
                .andThen(new SqrFunction())
                .apply(123);
        // sqr(123) = любое число, const10(число) = 10, sqr(10) = 100
        assert complexResult == 100.0 : "Ошибка: сложная цепочка должна вернуть 100, получили " + complexResult;
        System.out.println("✓ Сложная прямая цепочка: " + complexResult);

        System.out.println("✓ Прямое применение работает корректно");
    }
}