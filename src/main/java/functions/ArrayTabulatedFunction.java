package functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Табулированная функция на основе массивов.
 */
public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable, Serializable {
    private static final long serialVersionUID = -1646585929270415559L;
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunction.class);
    
    private double[] xValues;
    private double[] yValues;

    /**
     * Конструктор с массивами x и y
     */
    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        logger.info("Creating ArrayTabulatedFunction with {} points", xValues.length);

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        
        if (xValues.length < 2) {
            logger.error("Array length must be at least 2, but was {}", xValues.length);
            throw new IllegalArgumentException("Должно быть как минимум 2 точки");
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);

        logger.debug("ArrayTabulatedFunction created. Range: [{}, {}]", xValues[0], xValues[count-1]);
    }

    /**
     * Конструктор для табулирования функции на интервале
     */
    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        logger.info("Creating ArrayTabulatedFunction from {} in range [{}, {}] with {} points",
                source.getClass().getSimpleName(), xFrom, xTo, count);

        if (count < 2) {
            logger.error("Count must be at least 2, but was {}", count);
            throw new IllegalArgumentException("Количество точек должно быть как минимум 2");
        }

        // Меняем местами если xFrom > xTo
        if (xFrom > xTo) {
            logger.debug("Swapping xFrom and xTo: {} -> {}", xFrom, xTo);
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];

        // Заполняем xValues равномерно
        double step = (xTo - xFrom) / (count - 1);
        for (int i = 0; i < count; i++) {
            xValues[i] = xFrom + i * step;
            yValues[i] = source.apply(xValues[i]);
        }
        logger.debug("ArrayTabulatedFunction created successfully from function");
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            logger.error("Index out of bounds: {} (count: {})", index, count);
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            logger.error("Index out of bounds: {} (count: {})", index, count);
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        logger.debug("Setting Y at index {} to {}", index, value);
        if (index < 0 || index >= count) {
            logger.error("Index out of bounds: {} (count: {})", index, count);
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (xValues[i] == x) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (yValues[i] == y) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        logger.trace("Finding floor index for x = {}", x);
        if (x < xValues[0]) {
            //ERROR: критические ошибки (невалидные данные, исключения)
            logger.error("x = {} is less than left boundary {}", x, xValues[0]);
            throw new IllegalArgumentException("x = " + x + " меньше левой границы " + xValues[0]);
        }

        for (int i = 1; i < count; i++) {
            if (xValues[i] >= x) {
                return i - 1;
            }
        }

        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    protected double extrapolateRight(double x) {
        int lastIndex = count - 1;
        return interpolate(x, xValues[lastIndex - 1], xValues[lastIndex],
                          yValues[lastIndex - 1], yValues[lastIndex]);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        logger.trace("Interpolating at x = {} with floorIndex = {}", x, floorIndex);

        if (floorIndex < 0 || floorIndex >= count) {
            //ERROR: критические ошибки (невалидные данные, исключения)
            logger.error("Invalid floorIndex: {} (count: {})", floorIndex, count);
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }

        if (floorIndex == count) {
            logger.debug("Extrapolating right at x = {}", x);
            return extrapolateRight(x);
        }
        if (floorIndex < 0) {
            logger.debug("Extrapolating left at x = {}", x);
            return extrapolateLeft(x);
        }

        int rightIndex = floorIndex + 1;
        if (rightIndex >= count) {
            return extrapolateRight(x);
        }

        // Проверяем, что x находится в интервале интерполирования
        if (x < xValues[floorIndex] || x > xValues[rightIndex]) {
            logger.warn("Interpolation outside interval: x={}, should be in [{}, {}]",
                    x, xValues[floorIndex], xValues[rightIndex]);
            throw new exceptions.InterpolationException("x is outside the interpolation interval");
        }

        return interpolate(x, xValues[floorIndex], xValues[rightIndex],
                          yValues[floorIndex], yValues[rightIndex]);
    }

    @Override
    public void remove(int index) {
        logger.info("Removing point at index {}", index);

        if (index < 0 || index >= count) {
            //ERROR: критические ошибки (невалидные данные, исключения)
            logger.error("Index out of bounds: {} (count: {})", index, count);
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }
        if (count <= 2) {
            //ERROR: критические ошибки (невалидные данные, исключения)
            logger.error("Cannot remove from function with only {} points", count);
            throw new IllegalStateException("Нельзя удалить элемент из функции с менее чем 2 точками");
        }

        // Создаем новые массивы меньшего размера
        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        // Копируем элементы до индекса
        System.arraycopy(xValues, 0, newXValues, 0, index);
        System.arraycopy(yValues, 0, newYValues, 0, index);

        // Копируем элементы после индекса
        if (index < count - 1) {
            System.arraycopy(xValues, index + 1, newXValues, index, count - index - 1);
            System.arraycopy(yValues, index + 1, newYValues, index, count - index - 1);
        }

        // Обновляем поля
        xValues = newXValues;
        yValues = newYValues;
        count--;
        logger.debug("Point removed. New count: {}", count);
    }

    @Override
    public void insert(double x, double y) {
        logger.info("Inserting point: x={}, y={}", x, y);

        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            logger.debug("X already exists at index {}, updating Y value", existingIndex);
            // если x уже есть просто заменяем y и заканчиваем выполнение
            yValues[existingIndex] = y;
            return;
        }

        // если такого x нет создаем новые массивы большего размера
        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];


        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }


        System.arraycopy(xValues, 0, newXValues, 0, insertIndex);
        System.arraycopy(yValues, 0, newYValues, 0, insertIndex);

        // вставляем новые значения
        newXValues[insertIndex] = x;
        newYValues[insertIndex] = y;


        if (insertIndex < count) {
            System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex);
            System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);
        }

        // обновляем поля
        xValues = newXValues;
        yValues = newYValues;
        count++;
        logger.debug("Point inserted at index {}. New count: {}", insertIndex, count);
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Point point = new Point(xValues[i], yValues[i]);
                i++;
                return point;
            }
        };
    }
}
