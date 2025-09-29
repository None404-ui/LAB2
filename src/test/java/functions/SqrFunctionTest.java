package functions;

public class SqrFunctionTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование SqrFunction ===");

        SqrFunction sqrFunction = new SqrFunction();

        // Тест 1: квадрат положительного числа
        double result1 = sqrFunction.apply(5.0);
        assert result1 == 25.0 : "Ошибка: квадрат 5 должен быть 25, получили " + result1;
        System.out.println("✓ Тест 1 пройден: 5^2 = " + result1);

        // Тест 2: квадрат отрицательного числа
        double result2 = sqrFunction.apply(-3.0);
        assert result2 == 9.0 : "Ошибка: квадрат -3 должен быть 9, получили " + result2;
        System.out.println("✓ Тест 2 пройден: (-3)^2 = " + result2);

        // Тест 3: квадрат нуля
        double result3 = sqrFunction.apply(0.0);
        assert result3 == 0.0 : "Ошибка: квадрат 0 должен быть 0, получили " + result3;
        System.out.println("✓ Тест 3 пройден: 0^2 = " + result3);

        // Тест 4: квадрат дробного числа
        double result4 = sqrFunction.apply(2.5);
        assert result4 == 6.25 : "Ошибка: квадрат 2.5 должен быть 6.25, получили " + result4;
        System.out.println("✓ Тест 4 пройден: (2.5)^2 = " + result4);

        // Тест 5: квадрат единицы
        double result5 = sqrFunction.apply(1.0);
        assert result5 == 1.0 : "Ошибка: квадрат 1 должен быть 1, получили " + result5;
        System.out.println("✓ Тест 5 пройден: 1^2 = " + result5);

        System.out.println("\n✓ Все тесты SqrFunction пройдены успешно!");
    }
}