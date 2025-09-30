package functions;

/**
 * Функция, использующая метод Ньютона для нахождения корней уравнения f(x) = 0
 * Результатом является константная функция, возвращающая найденный корень
 */
public class NewtonFunction implements MathFunction {
    private final double root;
    private final MathFunction function;
    private final MathFunction derivative;

    /**
     * Конструктор для создания функции, которая является корнем уравнения f(x) = 0
     * @param function функция f(x)
     * @param derivative производная f'(x)
     * @param initialGuess начальное приближение
     * @param tolerance точность
     */
    public NewtonFunction(MathFunction function, MathFunction derivative, double initialGuess, double tolerance) {
        this.function = function;
        this.derivative = derivative;
        this.root = solveNewton(function, derivative, initialGuess, tolerance);
    }

    /**
     * Конструктор для создания функции, которая является решением f(x) = target
     * @param function функция f(x)
     * @param derivative производная f'(x)
     * @param target целевое значение
     * @param initialGuess начальное приближение
     * @param tolerance точность
     */
    public NewtonFunction(MathFunction function, MathFunction derivative, double target, double initialGuess, double tolerance) {
        this.function = x -> function.apply(x) - target;
        this.derivative = derivative;
        this.root = solveNewton(this.function, derivative, initialGuess, tolerance);
    }

    /**
     * Метод Ньютона для решения уравнения f(x) = 0
     */
    private double solveNewton(MathFunction f, MathFunction df, double x0, double tolerance) {
        double x = x0;
        int maxIterations = 1000;

        for (int i = 0; i < maxIterations; i++) {
            double fx = f.apply(x);
            double dfx = df.apply(x);

            if (Math.abs(dfx) < 1e-10) {
                throw new ArithmeticException("Производная слишком мала в точке x = " + x);
            }

            double xNew = x - fx / dfx;

            if (Math.abs(xNew - x) < tolerance) {
                return xNew;
            }

            x = xNew;
        }

        throw new ArithmeticException("Не удалось найти решение после " + maxIterations + " итераций");
    }

    @Override
    public double apply(double x) {
        // Возвращаем найденный корень для любого x (константная функция)
        return root;
    }

    /**
     * Возвращает найденный корень
     */
    public double getRoot() {
        return root;
    }

    /**
     * Возвращает исходную функцию
     */
    public MathFunction getFunction() {
        return function;
    }

    /**
     * Возвращает производную
     */
    public MathFunction getDerivative() {
        return derivative;
    }
}
