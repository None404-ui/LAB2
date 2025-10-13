package operations;

import exceptions.InconsistentFunctionsException;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

/**
 * Сервис для работы с табулированными функциями и их операциями
 */
public class TabulatedFunctionOperationService {

    private TabulatedFunctionFactory factory;

    /**
     * Конструктор с фабрикой
     * @param factory фабрика для создания табулированных функций
     */
    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    /**
     * Конструктор без параметров, использует ArrayTabulatedFunctionFactory по умолчанию
     */
    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    /**
     * Получить фабрику
     * @return фабрика
     */
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    /**
     * Установить фабрику
     * @param factory фабрика
     */
    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    /**
     * Преобразует табулированную функцию в массив точек
     * @param tabulatedFunction табулированная функция
     * @return массив точек
     */
    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        int count = tabulatedFunction.getCount();
        Point[] points = new Point[count];
        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i] = point;
            i++;
        }
        return points;
    }

    /**
     * Вложенный интерфейс для бинарных операций
     */
    private interface BiOperation {
        /**
         * Применяет операцию к двум числам
         * @param u первое число
         * @param v второе число
         * @return результат операции
         */
        double apply(double u, double v);
    }

    /**
     * Выполняет операцию над двумя табулированными функциями
     * @param a первая функция
     * @param b вторая функция
     * @param operation операция для применения
     * @return новая табулированная функция
     */
    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a.getCount() != b.getCount()) {
            throw new InconsistentFunctionsException("Functions have different number of points");
        }

        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);
        
        int count = a.getCount();
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            if (pointsA[i].x != pointsB[i].x) {
                throw new InconsistentFunctionsException("Functions have different x values at index " + i);
            }
            xValues[i] = pointsA[i].x;
            yValues[i] = operation.apply(pointsA[i].y, pointsB[i].y);
        }

        return factory.create(xValues, yValues);
    }

    /**
     * Сложение двух табулированных функций
     * @param a первая функция
     * @param b вторая функция
     * @return результат сложения
     */
    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u + v);
    }

    /**
     * Вычитание двух табулированных функций
     * @param a первая функция
     * @param b вторая функция
     * @return результат вычитания
     */
    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u - v);
    }

    /**
     * Умножение двух табулированных функций
     * @param a первая функция
     * @param b вторая функция
     * @return результат умножения
     */
    public TabulatedFunction multiply(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u * v);
    }

    /**
     * Деление двух табулированных функций
     * @param a первая функция
     * @param b вторая функция
     * @return результат деления
     */
    public TabulatedFunction divide(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (u, v) -> u / v);
    }
}

