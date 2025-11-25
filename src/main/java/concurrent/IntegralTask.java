package concurrent;

import functions.TabulatedFunction;

import java.util.concurrent.Callable;

/**
 * Задача для параллельного вычисления интеграла на части интервала
 */
public class IntegralTask implements Callable<Double> {
    private final TabulatedFunction function;
    private final double from;
    private final double to;
    private final int steps;

    public IntegralTask(TabulatedFunction function, double from, double to, int steps) {
        this.function = function;
        this.from = from;
        this.to = to;
        this.steps = steps;
    }

    @Override
    public Double call() {
        double h = (to - from) / steps;
        double sum = 0.0;
        
        // Метод трапеций
        for (int i = 0; i < steps; i++) {
            double x1 = from + i * h;
            double x2 = from + (i + 1) * h;
            double y1 = function.apply(x1);
            double y2 = function.apply(x2);
            sum += (y1 + y2) * h / 2;
        }
        
        return sum;
    }
}



