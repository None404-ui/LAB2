package functions;

/**
 * Интерфейс для вставки значений в табулированную функцию.
 */
public interface Insertable {
    /**
     * Вставляет значение в табулированную функцию
     * @param x координата x
     * @param y значение функции в точке x
     */
    void insert(double x, double y);
}

