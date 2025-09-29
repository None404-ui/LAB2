package functions;

/**
 * Класс для композиции математических функций: h(x) = g(f(x))
 * Сначала применяется первая функция f(x), затем вторая функция g к результату
 */
public class CompositeFunction1 implements MathFunction {

    private final MathFunction firstFunction;  // Первая функция f(x)
    private final MathFunction secondFunction; // Вторая функция g(x)


    public CompositeFunction1(MathFunction firstFunction, MathFunction secondFunction) {
        if (firstFunction == null || secondFunction == null) {
            throw new IllegalArgumentException("Functions cannot be null");
        }
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    @Override
    public double apply(double x) {
        // Сначала применяем первую функцию: f(x)
        double intermediateResult = firstFunction.apply(x);
        // Затем применяем вторую функцию к результату: g(f(x))
        return secondFunction.apply(intermediateResult);
    }


    public MathFunction getFirstFunction() {
        return firstFunction;
    }


    public MathFunction getSecondFunction() {
        return secondFunction;
    }
}