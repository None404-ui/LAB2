package functions.factory;

import functions.MathFunction;
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

    /**
     * Создает табулированную функцию из MathFunction
     * @param source исходная математическая функция
     * @param xFrom начало интервала
     * @param xTo конец интервала
     * @param count количество точек разбиения
     * @return табулированная функция
     */
    TabulatedFunction create(MathFunction source, double xFrom, double xTo, int count);
}

