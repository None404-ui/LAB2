package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Абстрактный класс для табулированных функций
 */
public abstract class AbstractTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractTabulatedFunction.class);

    protected int count;

    /**
     * Возвращает количество точек в таблице
     */
    public int getCount() {
        return count;
    }

    /**
     * Возвращает левую границу таблицы
     */
    public double leftBound() {
        return getX(0);
    }

    /**
     * Возвращает правую границу таблицы
     */
    public double rightBound() {
        return getX(count - 1);
    }

    /**
     * Находит индекс максимального x, который меньше заданного x
     */
    protected abstract int floorIndexOfX(double x);

    /**
     * Экстраполяция слева
     */
    protected abstract double extrapolateLeft(double x);

    /**
     * Экстраполяция справа
     */
    protected abstract double extrapolateRight(double x);

    /**
     * Интерполяция с указанием индекса интервала
     */
    protected abstract double interpolate(double x, int floorIndex);

    /**
     * Метод интерполяции по формуле
     */
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }

    @Override
    public double apply(double x) {
        logger.debug("Применение функции к значению x = {}", x);

        if (count == 1) {
            logger.debug("Функция содержит только одну точку, возвращаем единственное значение y = {}", getY(0));
            return getY(0);
        }

        if (x <= leftBound()) {
            logger.debug("Значение x = {} находится за левой границей ({}), выполняем экстраполяцию влево", x, leftBound());
            return extrapolateLeft(x);
        }

        if (x >= rightBound()) {
            logger.debug("Значение x = {} находится за правой границей ({}), выполняем экстраполяцию вправо", x, rightBound());
            return extrapolateRight(x);
        }

        int index = indexOfX(x);
        if (index != -1) {
            logger.debug("Найдено точное совпадение в таблице по индексу {}, возвращаем y = {}", index, getY(index));
            return getY(index);
        }

        int floorIndex = floorIndexOfX(x);
        logger.debug("Выполняем интерполяцию между точками с индексами {} и {}", floorIndex, floorIndex + 1);
        return interpolate(x, floorIndex);
    }

    /**
     * Проверяет, что длины массивов одинаковые
     * @param xValues массив значений x
     * @param yValues массив значений y
     * @throws DifferentLengthOfArraysException если длины массивов различаются
     */
    protected static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            logger.error("Длины массивов x и y различны: x.length = {}, y.length = {}", xValues.length, yValues.length);
            throw new DifferentLengthOfArraysException("Lengths of arrays are different");
        }
        logger.debug("Проверка длин массивов пройдена успешно: длина = {}", xValues.length);
    }

    /**
     * Проверяет, что массив отсортирован по возрастанию
     * @param xValues массив значений x
     * @throws ArrayIsNotSortedException если массив не отсортирован
     */
    protected static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                logger.error("Массив x не отсортирован по возрастанию: x[{}] = {} <= x[{}] = {}", i, xValues[i], i - 1, xValues[i - 1]);
                throw new ArrayIsNotSortedException("Array is not sorted");
            }
        }
        logger.debug("Проверка сортировки массива x пройдена успешно");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Первая строка: название класса и размер
        sb.append(getClass().getSimpleName())
                .append(" size = ")
                .append(count)
                .append("\n");

        // Добавляем все точки через цикл for-each
        for (Point point : this) {
            sb.append("[")
                    .append(point.x)
                    .append("; ")
                    .append(point.y)
                    .append("]\n");
        }

        return sb.toString();
    }


}

