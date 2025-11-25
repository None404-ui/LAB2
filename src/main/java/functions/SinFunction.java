package functions;

/**
 * Функция синуса f(x) = sin(x)
 */
@MathFunctionInfo(name = "Синус (sin x)", priority = 10)
public class SinFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.sin(x);
    }
}



