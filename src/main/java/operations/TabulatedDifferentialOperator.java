package operations;

import concurrent.SynchronizedTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Оператор для вычисления производной табулированной функции
 */
public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedDifferentialOperator.class);

    private TabulatedFunctionFactory factory;

    /**
     * Конструктор с фабрикой
     * @param factory фабрика для создания функций
     */
    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    /**
     * Конструктор без параметров, использует ArrayTabulatedFunctionFactory по умолчанию
     */
    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    /**
     * Получить фабрику
     * @return фабрика
     */
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    /**
     * Установить фабрику
     * @param factory фабрика
     */
    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        logger.info("Computing derivative of tabulated function with {} points", function.getCount());

        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        // Вычисляем производные по формуле правой производной для первых n-1 точек
        for (int i = 0; i < count - 1; i++) {
            xValues[i] = points[i].x;
            yValues[i] = (points[i + 1].y - points[i].y) / (points[i + 1].x - points[i].x);
            logger.trace("Derivative at x[{}]={}: {}", i, points[i].x, yValues[i]);
        }

        // Для последней точки используем левую производную (такое же значение как у предпоследней)
        xValues[count - 1] = points[count - 1].x;
        yValues[count - 1] = yValues[count - 2];
        logger.trace("Last point derivative (copied): {}", yValues[count - 1]);

        TabulatedFunction result = factory.create(xValues, yValues);
        logger.debug("Derivative computation completed, created function with {} points", result.getCount());
        return result;
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        logger.info("Computing derivative synchronously for tabulated function with {} points", function.getCount());

        SynchronizedTabulatedFunction syncFunction;
        if (function instanceof SynchronizedTabulatedFunction) {
            logger.debug("Function is already synchronized, using existing instance");
            syncFunction = (SynchronizedTabulatedFunction) function;
        } else {
            logger.debug("Wrapping function in synchronized wrapper");
            syncFunction = new SynchronizedTabulatedFunction(function);
        }

        TabulatedFunction result = syncFunction.doSynchronously(this::derive);
        logger.debug("Synchronous derivative computation completed");
        return result;
    }
}

