package functions;

public class ArrayTabulatedFunctionRemovableTest {

    public static void main(String[] args) {
        System.out.println("=== Тестирование Removable интерфейса ArrayTabulatedFunction ===");

        testRemoveFromMiddle();
        testRemoveFromBeginning();
        testRemoveFromEnd();
        testRemoveAllElements();

        System.out.println("\n✓ Все тесты Removable интерфейса пройдены успешно!");
    }

    private static void testRemoveFromMiddle() {
        System.out.println("\n--- Тест удаления из середины ---");

        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем элемент с индексом 2 (x=3.0, y=9.0)
        function.remove(2);

        assert function.getCount() == 4 : "Неверное количество после удаления";
        assert function.getX(0) == 1.0 : "Неверное значение x[0] после удаления";
        assert function.getX(1) == 2.0 : "Неверное значение x[1] после удаления";
        assert function.getX(2) == 4.0 : "Неверное значение x[2] после удаления";
        assert function.getX(3) == 5.0 : "Неверное значение x[3] после удаления";

        System.out.println("✓ Удаление из середины работает корректно");
    }

    private static void testRemoveFromBeginning() {
        System.out.println("\n--- Тест удаления из начала ---");

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем первый элемент
        function.remove(0);

        assert function.getCount() == 3 : "Неверное количество после удаления";
        assert function.getX(0) == 2.0 : "Неверное значение x[0] после удаления";
        assert function.getX(1) == 3.0 : "Неверное значение x[1] после удаления";
        assert function.getX(2) == 4.0 : "Неверное значение x[2] после удаления";

        System.out.println("✓ Удаление из начала работает корректно");
    }

    private static void testRemoveFromEnd() {
        System.out.println("\n--- Тест удаления из конца ---");

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем последний элемент
        function.remove(3);

        assert function.getCount() == 3 : "Неверное количество после удаления";
        assert function.getX(0) == 1.0 : "Неверное значение x[0] после удаления";
        assert function.getX(1) == 2.0 : "Неверное значение x[1] после удаления";
        assert function.getX(2) == 3.0 : "Неверное значение x[2] после удаления";

        System.out.println("✓ Удаление из конца работает корректно");
    }

    private static void testRemoveAllElements() {
        System.out.println("\n--- Тест удаления всех элементов ---");

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем элемент
        function.remove(1);
        assert function.getCount() == 2 : "Неверное количество после первого удаления";

        // Пытаемся удалить еще один элемент (должно остаться 2)
        try {
            function.remove(0);
            assert false : "Должно было выбросить исключение";
        } catch (IllegalStateException e) {
            assert function.getCount() == 2 : "Количество должно остаться 2";
        }

        System.out.println("✓ Защита от удаления всех элементов работает корректно");
    }
}

