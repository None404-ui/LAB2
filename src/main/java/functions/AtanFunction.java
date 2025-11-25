package functions;

/**
 * Арктангенс f(x) = arctan(x)
 */
@MathFunctionInfo(name = "Арктангенс (arctan x)", priority = 15)
public class AtanFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.atan(x);
    }
}



