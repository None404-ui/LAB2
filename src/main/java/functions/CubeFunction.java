package functions;

/**
 * Кубическая функция f(x) = x³
 */
@MathFunctionInfo(name = "Кубическая функция (x³)", priority = 6)
public class CubeFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return x * x * x;
    }
}



