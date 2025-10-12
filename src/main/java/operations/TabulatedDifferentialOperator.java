package operations;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

/**
 * Оператор для вычисления производной табулированной функции
 */
public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    
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
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;
        
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        
        // Вычисляем производные по формуле правой производной для первых n-1 точек
        for (int i = 0; i < count - 1; i++) {
            xValues[i] = points[i].x;
            yValues[i] = (points[i + 1].y - points[i].y) / (points[i + 1].x - points[i].x);
        }
        
        // Для последней точки используем левую производную (такое же значение как у предпоследней)
        xValues[count - 1] = points[count - 1].x;
        yValues[count - 1] = yValues[count - 2];
        
        return factory.create(xValues, yValues);
    }
}

