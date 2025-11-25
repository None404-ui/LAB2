package functions;

/**
 * Модуль числа f(x) = |x|
 */
@MathFunctionInfo(name = "Модуль (|x|)", priority = 7)
public class AbsFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.abs(x);
    }
}



