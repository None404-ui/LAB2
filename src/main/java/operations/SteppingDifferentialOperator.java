package operations;

import functions.MathFunction;

/**
 * Абстрактный класс дифференциального оператора с шагом
 */
public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction> {
    
    protected double step;

    /**
     * Конструктор с шагом дифференцирования
     * @param step шаг дифференцирования
     * @throws IllegalArgumentException если шаг неположительный, бесконечный или NaN
     */
    public SteppingDifferentialOperator(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            throw new IllegalArgumentException("Step must be positive and finite");
        }
        this.step = step;
    }

    /**
     * Получить шаг дифференцирования
     * @return шаг
     */
    public double getStep() {
        return step;
    }

    /**
     * Установить шаг дифференцирования
     * @param step шаг
     * @throws IllegalArgumentException если шаг неположительный, бесконечный или NaN
     */
    public void setStep(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            throw new IllegalArgumentException("Step must be positive and finite");
        }
        this.step = step;
    }

    @Override
    public abstract MathFunction derive(MathFunction function);
}

