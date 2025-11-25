package functions;

/**
 * Арккосинус f(x) = arccos(x)
 */
@MathFunctionInfo(name = "Арккосинус (arccos x)", priority = 14)
public class AcosFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.acos(x);
    }
}



