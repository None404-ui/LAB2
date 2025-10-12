package functions.factory;

import functions.TabulatedFunction;

/**
 * Интерфейс фабрики для создания табулированных функций
 */
public interface TabulatedFunctionFactory {
    /**
     * Создает табулированную функцию из массивов x и y
     * @param xValues массив значений x
     * @param yValues массив значений y
     * @return табулированная функция
     */
    TabulatedFunction create(double[] xValues, double[] yValues);
}

