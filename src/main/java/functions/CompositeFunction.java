package functions;

/**
 * Класс для композиции функций.
 */
public class CompositeFunction implements MathFunction {
    private final MathFunction firstFunction;
    private final MathFunction secondFunction;

    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        if (firstFunction == null || secondFunction == null) {
            throw new IllegalArgumentException("Functions cannot be null");
        }
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    @Override
    public double apply(double x) {
        // Сначала применяем первую функцию, затем ко результату - вторую
        return secondFunction.apply(firstFunction.apply(x));
    }

    public MathFunction getFirstFunction() {
        return firstFunction;
    }

    public MathFunction getSecondFunction() {
        return secondFunction;
    }
}