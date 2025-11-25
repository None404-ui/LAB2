package functions;

/**
 * Десятичный логарифм f(x) = log₁₀(x)
 */
@MathFunctionInfo(name = "Десятичный логарифм (log₁₀ x)", priority = 22)
public class Log10Function implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.log10(x);
    }
}



