package functions;

/**
 * Класс для тождественной функции f(x) = x
 */
@MathFunctionInfo(name = "Тождественная функция (x)", priority = 2)
public class IdentityFunction implements MathFunction {

    @Override
    public double apply(double x) {
        // тождественно возвращаем x
        return x;
    }
}




