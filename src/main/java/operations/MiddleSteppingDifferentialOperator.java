package operations;

import functions.MathFunction;

/**
 * Оператор средней разностной производной
 */
public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator {

    /**
     * Конструктор с шагом дифференцирования
     * @param step шаг дифференцирования
     */
    public MiddleSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Средняя разностная производная: (f(x + h) - f(x - h)) / (2h)
                return (function.apply(x + step) - function.apply(x - step)) / (2 * step);
            }
        };
    }
}

