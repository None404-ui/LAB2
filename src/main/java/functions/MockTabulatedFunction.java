package functions;

/**
 * Mock-класс для тестирования абстрактных методов AbstractTabulatedFunction
 */
public class MockTabulatedFunction extends AbstractTabulatedFunction {
    private final double x0, x1, y0, y1;

    /**
     * Конструктор MockTabulatedFunction
     * @param x0 первое значение x
     * @param y0 первое значение y
     * @param x1 второе значение x (должно быть > x0)
     * @param y1 второе значение y
     */
    public MockTabulatedFunction(double x0, double y0, double x1, double y1) {
        if (x0 >= x1) {
            throw new IllegalArgumentException("x0 должно быть меньше x1");
        }
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.count = 2;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        return (index == 0) ? x0 : x1;
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        return (index == 0) ? y0 : y1;
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        // Для mock-объекта изменение значений не поддерживается
        throw new UnsupportedOperationException("Изменение значений в mock-объекте не поддерживается");
    }

    @Override
    public int indexOfX(double x) {
        if (x == x0) return 0;
        if (x == x1) return 1;
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (y == y0) return 0;
        if (y == y1) return 1;
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < x0) return 0;
        if (x >= x1) return 1; // индекс максимального x, который <= x
        return 0; // x находится между x0 и x1, возвращаем индекс x0
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, x0, x1, y0, y1);
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, x0, x1, y0, y1);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex == 0) {
            return interpolate(x, x0, x1, y0, y1);
        } else {
            return interpolate(x, x0, x1, y0, y1);
        }
    }
}
