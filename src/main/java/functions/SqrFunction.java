package functions;

/**
 * Класс для вычисления квадрата числа
 */
@MathFunctionInfo(name = "Квадратичная функция (x²)", priority = 1)
public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.pow(x, 2);
    }
}

