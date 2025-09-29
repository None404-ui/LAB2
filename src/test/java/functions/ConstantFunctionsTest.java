package functions;

public class ConstantFunctionsTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование ConstantFunction ===");

        // Тест с положительной константой
        ConstantFunction func1 = new ConstantFunction(5.7);
        assert func1.apply(10) == 5.7 : "Ошибка: должна вернуться константа 5.7";
        assert func1.apply(-3.2) == 5.7 : "Ошибка: должна вернуться константа 5.7";
        assert func1.apply(0) == 5.7 : "Ошибка: должна вернуться константа 5.7";
        assert func1.getConstant() == 5.7 : "Ошибка: getConstant() должен вернуть 5.7";
        System.out.println("✓ Тест ConstantFunction(5.7) пройден");

        // Тест с отрицательной константой
        ConstantFunction func2 = new ConstantFunction(-2.5);
        assert func2.apply(100) == -2.5 : "Ошибка: должна вернуться константа -2.5";
        assert func2.apply(-100) == -2.5 : "Ошибка: должна вернуться константа -2.5";
        assert func2.getConstant() == -2.5 : "Ошибка: getConstant() должен вернуть -2.5";
        System.out.println("✓ Тест ConstantFunction(-2.5) пройден");

        // Тест с нулевой константой
        ConstantFunction func3 = new ConstantFunction(0);
        assert func3.apply(123.45) == 0 : "Ошибка: должна вернуться константа 0";
        assert func3.getConstant() == 0 : "Ошибка: getConstant() должен вернуть 0";
        System.out.println("✓ Тест ConstantFunction(0) пройден");

        System.out.println("\n=== Тестирование ZeroFunction ===");
        ZeroFunction zeroFunc = new ZeroFunction();

        // Тест с различными значениями x
        assert zeroFunc.apply(5) == 0 : "Ошибка: ZeroFunction должна всегда возвращать 0";
        assert zeroFunc.apply(-10.5) == 0 : "Ошибка: ZeroFunction должна всегда возвращать 0";
        assert zeroFunc.apply(0) == 0 : "Ошибка: ZeroFunction должна всегда возвращать 0";
        assert zeroFunc.apply(999.999) == 0 : "Ошибка: ZeroFunction должна всегда возвращать 0";

        // Тест геттера
        assert zeroFunc.getConstant() == 0 : "Ошибка: getConstant() должен вернуть 0";

        System.out.println("✓ Все тесты ZeroFunction пройдены");

        System.out.println("\n=== Тестирование UnitFunction ===");
        UnitFunction unitFunc = new UnitFunction();

        // Тест с различными значениями x
        assert unitFunc.apply(5) == 1 : "Ошибка: UnitFunction должна всегда возвращать 1";
        assert unitFunc.apply(-10.5) == 1 : "Ошибка: UnitFunction должна всегда возвращать 1";
        assert unitFunc.apply(0) == 1 : "Ошибка: UnitFunction должна всегда возвращать 1";
        assert unitFunc.apply(999.999) == 1 : "Ошибка: UnitFunction должна всегда возвращать 1";

        // Тест геттера
        assert unitFunc.getConstant() == 1 : "Ошибка: getConstant() должен вернуть 1";

        System.out.println("✓ Все тесты UnitFunction пройдены");

        System.out.println("\n✓ Все тесты пройдены успешно!");
    }
}