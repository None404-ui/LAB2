package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;

import java.util.Arrays;

/**
 * Абстрактный класс для табулированных функций
 */
public abstract class AbstractTabulatedFunction implements TabulatedFunction {
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
        if (count == 1) {
            return getY(0);
        }

        if (x <= leftBound()) {
            return extrapolateLeft(x);
        }

        if (x >= rightBound()) {
            return extrapolateRight(x);
        }

        int index = indexOfX(x);
        if (index != -1) {
            return getY(index);
        }

        int floorIndex = floorIndexOfX(x);
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
            throw new DifferentLengthOfArraysException("Lengths of arrays are different");
        }
    }

    /**
     * Проверяет, что массив отсортирован по возрастанию
     * @param xValues массив значений x
     * @throws ArrayIsNotSortedException если массив не отсортирован
     */
    protected static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new ArrayIsNotSortedException("Array is not sorted");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" size = ").append(count).append("\n");
        for (int i = 0; i < count; i++) {
            sb.append("[").append(getX(i)).append("; ").append(getY(i)).append("]");
            if (i < count - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}

