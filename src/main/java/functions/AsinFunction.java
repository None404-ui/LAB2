package functions;

/**
 * Арксинус f(x) = arcsin(x)
 */
@MathFunctionInfo(name = "Арксинус (arcsin x)", priority = 13)
public class AsinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.asin(x);
    }
}



