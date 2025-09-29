package functions;

/**
 * Класс для вычисления квадрата числа
 */
public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.pow(x, 2);
    }
}

