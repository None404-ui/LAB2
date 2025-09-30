package functions;

import java.util.Arrays;

/**
 * Табулированная функция на основе массивов
 */
public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable {
    private double[] xValues;
    private double[] yValues;

    /**
     * Конструктор с массивами x и y
     */
    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Массивы xValues и yValues должны иметь одинаковую длину");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Должно быть как минимум 2 точки");
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);

        // Проверяем, что xValues упорядочены
        for (int i = 1; i < count; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("Значения x должны быть упорядочены по возрастанию");
            }
        }
    }

    /**
     * Конструктор для табулирования функции на интервале
     */
    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Количество точек должно быть как минимум 2");
        }

        // Меняем местами если xFrom > xTo
        if (xFrom > xTo) {
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
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
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
        if (x < xValues[0]) {
            return 0;
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
        if (count == 1) {
            return yValues[0];
        }
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    protected double extrapolateRight(double x) {
        if (count == 1) {
            return yValues[0];
        }
        int lastIndex = count - 1;
        return interpolate(x, xValues[lastIndex - 1], xValues[lastIndex],
                          yValues[lastIndex - 1], yValues[lastIndex]);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex == count) {
            return extrapolateRight(x);
        }
        if (floorIndex < 0) {
            return extrapolateLeft(x);
        }

        int rightIndex = floorIndex + 1;
        if (rightIndex >= count) {
            return extrapolateRight(x);
        }

        return interpolate(x, xValues[floorIndex], xValues[rightIndex],
                          yValues[floorIndex], yValues[rightIndex]);
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        if (count <= 2) {
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
    }

    @Override
    public void insert(double x, double y) {
        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
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
    }


}
