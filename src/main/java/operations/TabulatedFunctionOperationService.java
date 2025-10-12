package operations;

import functions.Point;
import functions.TabulatedFunction;

/**
 * Сервис для работы с табулированными функциями и их операциями
 */
public class TabulatedFunctionOperationService {

    /**
     * Преобразует табулированную функцию в массив точек
     * @param tabulatedFunction табулированная функция
     * @return массив точек
     */
    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        int count = tabulatedFunction.getCount();
        Point[] points = new Point[count];
        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i] = point;
            i++;
        }
        return points;
    }
}

