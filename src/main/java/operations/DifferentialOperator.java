package operations;

/**
 * Интерфейс дифференциального оператора
 * @param <T> тип функции
 */
public interface DifferentialOperator<T> {
    /**
     * Вычисляет производную функции
     * @param function функция
     * @return производная функции
     */
    T derive(T function);
}

