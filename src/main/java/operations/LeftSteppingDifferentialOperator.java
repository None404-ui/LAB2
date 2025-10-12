package operations;

import functions.MathFunction;

/**
 * Оператор левой разностной производной
 */
public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {

    /**
     * Конструктор с шагом дифференцирования
     * @param step шаг дифференцирования
     */
    public LeftSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Левая разностная производная: (f(x) - f(x - h)) / h
                return (function.apply(x) - function.apply(x - step)) / step;
            }
        };
    }
}

