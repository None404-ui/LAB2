package functions;
//Класс для функции ДеБура

public class DeBoorFunction implements MathFunction {

    private final double[] controlPoints; // Контрольные точки
    private final double[] knots;         // Узловой вектор
    private final int degree;             // Степень сплайна


    public DeBoorFunction(double[] controlPoints, double[] knots, int degree) {
        if (controlPoints == null || knots == null) {
            throw new IllegalArgumentException("Control points and knots cannot be null");
        }
        if (degree < 1) {
            throw new IllegalArgumentException("Degree must be at least 1");
        }
        if (controlPoints.length < degree + 1) {
            throw new IllegalArgumentException("Need at least degree + 1 control points");
        }
        if (knots.length != controlPoints.length + degree + 1) {
            throw new IllegalArgumentException("Knot vector length must be controlPoints.length + degree + 1");
        }

        this.controlPoints = controlPoints.clone();
        this.knots = knots.clone();
        this.degree = degree;
    }


    public DeBoorFunction(double[] controlPoints, int degree) {
        this(controlPoints, createUniformKnots(controlPoints.length, degree), degree);
    }

    @Override
    public double apply(double x) {
        // Алгоритм де Бура для вычисления значения B-сплайна в точке x
        return deBoorAlgorithm(x);
    }

    /**
     * Реализация алгоритма де Бура
     */
    private double deBoorAlgorithm(double x) {
        // Находим интервал узлов, в который попадает x
        int knotSpan = findKnotSpan(x);

        // Если x вне диапазона узлов, возвращаем 0 (для равномерного сплайна)
        if (knotSpan < degree || knotSpan >= controlPoints.length) {
            return 0.0;
        }

        // Инициализируем массив для алгоритма де Бура
        double[] d = new double[degree + 1];
        for (int i = 0; i <= degree; i++) {
            d[i] = controlPoints[knotSpan - degree + i];
        }

        // Выполняем алгоритм де Бура
        for (int r = 1; r <= degree; r++) {
            for (int j = degree; j >= r; j--) {
                int alphaIndex = knotSpan - degree + j;
                double alpha = (x - knots[alphaIndex]) /
                        (knots[alphaIndex + degree - r + 1] - knots[alphaIndex]);
                d[j] = (1 - alpha) * d[j - 1] + alpha * d[j];
            }
        }

        return d[degree];
    }


    private int findKnotSpan(double x) {
        // Бинарный поиск интервала узлов
        int low = degree;
        int high = controlPoints.length;

        while (low < high) {
            int mid = (low + high) / 2;
            if (x < knots[mid]) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }

        return low - 1;
    }


    private static double[] createUniformKnots(int numControlPoints, int degree) {
        int totalKnots = numControlPoints + degree + 1;
        double[] knots = new double[totalKnots];

        // Первые degree+1 узлов = 0
        for (int i = 0; i <= degree; i++) {
            knots[i] = 0.0;
        }

        // Средние узлы равномерно распределены
        for (int i = degree + 1; i < numControlPoints; i++) {
            knots[i] = (double) (i - degree) / (numControlPoints - degree);
        }

        // Последние degree+1 узлов = 1
        for (int i = numControlPoints; i < totalKnots; i++) {
            knots[i] = 1.0;
        }

        return knots;
    }


    public double[] getControlPoints() {
        return controlPoints.clone();
    }

    public double[] getKnots() {
        return knots.clone();
    }

    public int getDegree() {
        return degree;
    }
}

