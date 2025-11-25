package functions;

/**
 * Функция косинуса f(x) = cos(x)
 */
@MathFunctionInfo(name = "Косинус (cos x)", priority = 11)
public class CosFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.cos(x);
    }
}



