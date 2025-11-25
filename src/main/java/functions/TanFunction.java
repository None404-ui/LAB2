package functions;

/**
 * Функция тангенса f(x) = tan(x)
 */
@MathFunctionInfo(name = "Тангенс (tan x)", priority = 12)
public class TanFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.tan(x);
    }
}



