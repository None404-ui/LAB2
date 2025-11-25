package functions;

/**
 * Квадратный корень f(x) = √x
 */
@MathFunctionInfo(name = "Квадратный корень (√x)", priority = 5)
public class SqrtFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sqrt(x);
    }
}



