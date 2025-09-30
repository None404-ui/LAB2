package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//дополнительные тесты для функции
class CompositeFunctionExtraTest {

    @Test
    void testLinkedListWithLinkedList() {
        // табулированная  + табулированная
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 2.0, 4.0}; // f(x) = 2x

        double[] xValues2 = {0.0, 2.0, 4.0};
        double[] yValues2 = {0.0, 1.0, 2.0}; // g(x) = x/2

        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        // h(x) = g(f(x)) = (2x)/2 = x
        CompositeFunction composite = new CompositeFunction(func1, func2);

        assertEquals(0.0, composite.apply(0.0), 0.1);
        assertEquals(1.0, composite.apply(1.0), 0.1);
        assertEquals(2.0, composite.apply(2.0), 0.1);
    }

    @Test
    void testTabulatedWithIdentityFunction() {
        // Табулированная функция + Тождественная функция
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0}; // f(x) ≈ x²

        IdentityFunction identity = new IdentityFunction(); // g(x) = x

        LinkedListTabulatedFunction tabulatedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Вариант 1: h(x) = identity(tabulatedFunc(x)) = f(x)
        CompositeFunction composite1 = new CompositeFunction(tabulatedFunc, identity);
        assertEquals(0.0, composite1.apply(0.0), 0.1);
        assertEquals(1.0, composite1.apply(1.0), 0.1);
        assertEquals(4.0, composite1.apply(2.0), 0.1);

        // Вариант 2: h(x) = tabulatedFunc(identity(x)) = f(x)
        CompositeFunction composite2 = new CompositeFunction(identity, tabulatedFunc);
        assertEquals(0.0, composite2.apply(0.0), 0.1);
        assertEquals(1.0, composite2.apply(1.0), 0.1);
        assertEquals(4.0, composite2.apply(2.0), 0.1);
    }



    @Test
    void testTabulatedWithCompositeFunction() {
        // Табулированная функция + Композитная функция
        // Используем точки которые точно есть в таблице, чтобы избежать интерполяции
        double[] xValues = {0.0, 2.0, 4.0, 6.0}; // Добавляем больше точек
        double[] yValues = {0.0, 4.0, 16.0, 36.0}; // f(x) = x²

        // Создаем композитную функцию: g(x) = (x + 1) * 2
        MathFunction plusOne = new MathFunction() {
            @Override
            public double apply(double x) {
                return x + 1;
            }
        };

        MathFunction timesTwo = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * 2;
            }
        };

        CompositeFunction compositeFunc = new CompositeFunction(plusOne, timesTwo);

        LinkedListTabulatedFunction tabulatedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Вариант 1: h(x) = compositeFunc(tabulatedFunc(x)) = ((x²) + 1) * 2
        CompositeFunction result1 = new CompositeFunction(tabulatedFunc, compositeFunc);

        // Используем точки из таблицы, чтобы избежать интерполяции
        assertEquals(2.0, result1.apply(0.0), 0.1);   // ((0) + 1) * 2 = 2
        assertEquals(10.0, result1.apply(2.0), 0.1);  // ((4) + 1) * 2 = 10
        assertEquals(34.0, result1.apply(4.0), 0.1);  // ((16) + 1) * 2 = 34

        // Вариант 2: h(x) = tabulatedFunc(compositeFunc(x)) = ((x + 1) * 2)²
        CompositeFunction result2 = new CompositeFunction(compositeFunc, tabulatedFunc);

        // compositeFunc(0) = (0+1)*2 = 2 → tabulatedFunc(2) = 4 (из таблицы)
        assertEquals(4.0, result2.apply(0.0), 0.1);
        // compositeFunc(1) = (1+1)*2 = 4 → tabulatedFunc(4) = 16 (из таблицы)
        assertEquals(16.0, result2.apply(1.0), 0.1);
        // compositeFunc(2) = (2+1)*2 = 6 → tabulatedFunc(6) = 36 (из таблицы)
        assertEquals(36.0, result2.apply(2.0), 0.1);
    }

    @Test
    void testNestedTabulatedCompositions() {
        // Многоуровневая композиция табулированных функций
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 2.0}; // f(x) = x

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {0.0, 2.0, 4.0}; // g(x) = 2x

        double[] xValues3 = {0.0, 2.0, 4.0};
        double[] yValues3 = {0.0, 1.0, 2.0}; // h(x) = x/2

        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);
        LinkedListTabulatedFunction func3 = new LinkedListTabulatedFunction(xValues3, yValues3);

        // k(x) = h(g(f(x))) = ((x) * 2) / 2 = x
        CompositeFunction level1 = new CompositeFunction(func1, func2); // 2x
        CompositeFunction level2 = new CompositeFunction(level1, func3); // (2x)/2

        assertEquals(0.0, level2.apply(0.0), 0.1);
        assertEquals(1.0, level2.apply(1.0), 0.1);
        assertEquals(2.0, level2.apply(2.0), 0.1);
    }

    @Test
    void testTabulatedFunctionFromOtherFunction() {
        // Создание табулированной функции из другой функции + композиция
        IdentityFunction identity = new IdentityFunction();

        // Создаем табулированную функцию из тождественной
        LinkedListTabulatedFunction tabulatedFromIdentity =
                new LinkedListTabulatedFunction(identity, 0, 3, 4); // точки: 0,1,2,3

        // Создаем квадратичную функцию
        MathFunction square = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * x;
            }
        };

        // Композиция: h(x) = square(tabulatedFromIdentity(x)) = x²
        CompositeFunction composite = new CompositeFunction(tabulatedFromIdentity, square);

        // Используем только точки из таблицы
        assertEquals(0.0, composite.apply(0.0), 0.1);
        assertEquals(1.0, composite.apply(1.0), 0.1);
        assertEquals(4.0, composite.apply(2.0), 0.1);
        assertEquals(9.0, composite.apply(3.0), 0.1);
    }

    @Test
    void testComplexRealWorldScenario() {
        //  преобразование температур
        //  точки которые точно есть в таблице
        double[] celsius = {-10.0, 0.0, 10.0, 20.0, 30.0};
        double[] fahrenheit = {14.0, 32.0, 50.0, 68.0, 86.0};

        double[] fahr = {14.0, 32.0, 50.0, 68.0, 86.0};
        double[] kelvin = {263.15, 273.15, 283.15, 293.15, 303.15};

        LinkedListTabulatedFunction celsiusToFahr = new LinkedListTabulatedFunction(celsius, fahrenheit);
        LinkedListTabulatedFunction fahrToKelvin = new LinkedListTabulatedFunction(fahr, kelvin);

        // h(x) = Цельсий → Фаренгейт → Кельвин
        CompositeFunction celsiusToKelvin = new CompositeFunction(celsiusToFahr, fahrToKelvin);

        //  0°C = 273.15K (используем точку из таблицы)
        assertEquals(273.15, celsiusToKelvin.apply(0.0), 0.1);
    }

    @Test
    void testSelfCompositionWithTabulated() {
        // Композиция табулированной функции с самой собой
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0}; // Больше точек для лучшей интерполяции
        double[] yValues = {1.0, 2.0, 3.0, 4.0, 5.0}; // f(x) = x + 1

        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        // h(x) = f(f(x)) = (x + 1) + 1 = x + 2
        CompositeFunction selfComposite = new CompositeFunction(func, func);


        assertEquals(3.0, selfComposite.apply(1.0), 0.1); // f(f(1)) = f(2) = 3
        assertEquals(4.0, selfComposite.apply(2.0), 0.1); // f(f(2)) = f(3) = 4
        assertEquals(5.0, selfComposite.apply(3.0), 0.1); // f(f(3)) = f(4) = 5
    }

    @Test
    void testMultipleTabulatedCompositions() {
        // Композиция нескольких табулированных функций
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 2.0}; // f(x) = x

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {1.0, 2.0, 3.0}; // g(x) = x + 1

        double[] xValues3 = {1.0, 2.0, 3.0, 4.0, 5.0}; // Больше точек
        double[] yValues3 = {1.0, 4.0, 9.0, 16.0, 25.0}; // h(x) = x²

        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);
        LinkedListTabulatedFunction func3 = new LinkedListTabulatedFunction(xValues3, yValues3);

        // k(x) = h(g(f(x))) = (x + 1)²
        CompositeFunction composite = new CompositeFunction(
                new CompositeFunction(func1, func2), // x + 1
                func3                                // (x + 1)²
        );


        assertEquals(1.0, composite.apply(0.0), 0.1); // (0+1)² = 1
        assertEquals(4.0, composite.apply(1.0), 0.1); // (1+1)² = 4
        assertEquals(9.0, composite.apply(2.0), 0.1); // (2+1)² = 9
    }

    @Test
    void testTabulatedWithDeBoorFunction() {
        // Табулированная функция + Функция де Бура
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0}; // f(x) ≈ x²

        double[] controlPoints = {0.0, 1.0, 0.0};
        int degree = 1;
        DeBoorFunction deBoorFunc = new DeBoorFunction(controlPoints, degree);

        LinkedListTabulatedFunction tabulatedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // h(x) = deBoorFunc(tabulatedFunc(x))
        CompositeFunction composite = new CompositeFunction(tabulatedFunc, deBoorFunc);


        assertDoesNotThrow(() -> {
            composite.apply(0.0);
            composite.apply(1.0);
            composite.apply(2.0);
        });
    }
}