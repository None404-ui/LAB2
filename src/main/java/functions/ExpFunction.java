package functions;

/**
 * Экспоненциальная функция f(x) = e^x
 */
@MathFunctionInfo(name = "Экспонента (eˣ)", priority = 20)
public class ExpFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.exp(x);
    }
}



