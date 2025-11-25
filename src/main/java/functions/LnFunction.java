package functions;

/**
 * Натуральный логарифм f(x) = ln(x)
 */
@MathFunctionInfo(name = "Натуральный логарифм (ln x)", priority = 21)
public class LnFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.log(x);
    }
}



