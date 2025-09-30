package functions;

/**
 * Класс для тождественной функции f(x) = x
 */
public class IdentityFunction implements MathFunction {

    @Override
    public double apply(double x) {
        // тождественно возвращаем x
        return x;
    }
}

