package functions;

public class IdentityFunction implements MathFunction {

     @Override
    public double apply(double x) {
        // тождественно возвращаем  x
        return x;
    }
}