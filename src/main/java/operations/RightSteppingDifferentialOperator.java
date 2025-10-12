package operations;

import functions.MathFunction;

/**
 * Оператор правой разностной производной
 */
public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    /**
     * Конструктор с шагом дифференцирования
     * @param step шаг дифференцирования
     */
    public RightSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Правая разностная производная: (f(x + h) - f(x)) / h
                return (function.apply(x + step) - function.apply(x)) / step;
            }
        };
    }
}

